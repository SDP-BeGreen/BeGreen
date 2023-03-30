package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.github.sdp_begreen.begreen.exceptions.DatabaseTimeoutException
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.ByteArrayOutputStream
import com.google.android.gms.maps.model.LatLng


/**
 * Database implementation using Firebase's services
 */
object FirebaseDB {

    private const val TAG: String = "Firebase Database"
    private const val TIMEOUT: Long = 10000 // Default waiting time before aborting a <get> on the database
    private const val ONE_MEGABYTE: Long = 1024 * 1024 // Maximal image size allowed (in bytes), to prevent out of memory errors
    // Realtime database ref
    private val databaseReference: DatabaseReference = Firebase.database.reference
    // Storage ref (for images)
    private val storageReference: StorageReference = Firebase.storage.reference
    private val connectedReference = Firebase.database.getReference(".info/connected")
    private const val USERS_PATH = "users"
    private const val USER_PROFILE_PICTURE_METADATA = "profilePictureMetadata"
    private const val USER_ID_ATTRIBUTE = "id"
    private const val BIN_LOCATION_PATH = "bin"
    private const val ADVICES_LOCATION_PATH = "advices"

    // Logs (in the console) the connections and disconnections with the Firebase database
    // We might want to provide a new constructor that takes code to execute on connections/disconnections
    // (to show the user when he gets connected/disconnected by example)
    init {
        connectedReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(Boolean::class.java) == true) {
                    Log.d(TAG, "connected")
                } else {
                    Log.d(TAG, "disconnected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Connection listener was cancelled")
            }
        })
    }

    /**
     * Returns the value associated to the to given [key] in the database
     *
     * @param key the key we want to know the value of
     * @param timeout the maximum time we wait for the database to respond
     * @return the value associated to the [key] or null if could not retrieve it
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if the an exception occurred while retrieving the data
     */
    suspend fun get(key: String, timeout: Long = TIMEOUT): String? {

        return try {
            // Cancel the query and throw exception after [timeout] ms
            withTimeout(timeout) {
                val data = databaseReference.child(key).get().await()
                data.value?.let { it as? String }
            }
        } catch (timeOutEx: TimeoutCancellationException) {
            Log.d(TAG, "Timeout, cant connect with database")
            throw DatabaseTimeoutException("Timeout, cant connect with database")
        }
        catch (databaseEx: DatabaseException) {
            Log.d(TAG, "Failed with error message: ${databaseEx.message}")
            throw databaseEx
        }
    }

    /**
     * Associate a new [value] to the given [key].
     * Create a new entry if the [key] did not exist
     *
     * @param key the key we want to set the value (non empty)
     * @param value the new value for the [key]
     * * @throws IllegalArgumentException if the userId is blank or empty
     */
    suspend fun set(key: String, value: String) {
        if (key.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        databaseReference.child(key).setValue(value).await()
    }

    /**
     * Add a new [user] to the given [userId]
     *
     * @param user the user we want to add to the database
     * @param userId the userId to use as key to store the user
     * @throws IllegalArgumentException if the userId is blank or empty
     */
    suspend fun addUser(user: User, userId: String) {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        databaseReference.child(USERS_PATH).child(userId).setValue(user).await()
    }

    /**
     * Retrieve the [User] associated with the given [userId]
     *
     * @param userId The id of the user we want to retrieve from the
     * @param timeout the maximum time we wait for the database to respond
     * @return the [User] associated to the given [userId], or null if it wasn't found
     *
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if the an exception occurred while retrieving the data
     * @throws IllegalArgumentException if the [userId] was blank or empty
     */
    suspend fun getUser(userId: String, timeout: Long = TIMEOUT): User? {
        return try {
            withTimeout(timeout) {
                if (userId.isBlank())
                    throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

                val data = databaseReference.child(USERS_PATH).child(userId).get().await()
                data.getValue(User::class.java)
            }
        } catch (timeoutEx: TimeoutCancellationException) {
            Log.d(TAG, "Timeout, can't connect with database")
            throw DatabaseTimeoutException("Timeout, cant connect with database")
        }
        catch (databaseEx: DatabaseException) {
            Log.d(TAG, "Failed with error message: ${databaseEx.message}")
            throw databaseEx
        }
    }

    /**
     * Store the profile picture for the given [User]
     *
     * @param image the profile picture to store
     * @param userId the [User] for whom to add the pictures
     * @return the updated [PhotoMetadata] with the current id of the image we stored
     *
     * @throws IllegalArgumentException if the [userId] was blank or empty
     */
    suspend fun storeUserProfilePicture(image: Bitmap, userId: String, metadata: PhotoMetadata): PhotoMetadata? {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        metadata.pictureId = "${userId}_profile_picture"

        return storePicture(image, USER_PROFILE_PICTURE_METADATA, metadata,
            databaseReference.child(USERS_PATH).child(userId),
            storageReference.child(USERS_PATH).child(userId).child(
                USER_PROFILE_PICTURE_METADATA))
    }

    /**
     * Adds and [image] for the user [userId] in the database
     *
     * @param image the image we want to add to the database
     * @param userId the ID of the user wanting to store a new image
     * @return a unique ID under which the image got stored, and null if the image couldn't get stored
     */
    suspend fun addImage(image: Bitmap, userId: Int, metadata: PhotoMetadata): PhotoMetadata? {

        return storePicture(image, null, metadata,
            databaseReference.child("pictures").child(userId.toString()),
            storageReference.child("userId").child(userId.toString()))
    }

    /**
     * Test whether a [User] exists in the database for the given [userId]
     *
     * @param userId The id of the user to check for the existence in the database
     * @param timeout the maximum time we wait for the database to respond
     * @return true if the user exists, false otherwise
     */
    suspend fun userExists(userId: String, timeout: Long = TIMEOUT): Boolean {
        return try {
            withTimeout(timeout) {
                if (userId.isBlank())
                    throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

                val data = databaseReference
                    .child(USERS_PATH)
                    .child(userId)
                    .child(USER_ID_ATTRIBUTE).get().await()
                data.exists()
            }
        } catch (timeoutEx: TimeoutCancellationException) {
            Log.d(TAG, "Timeout, can't connect with database")
            throw DatabaseTimeoutException("Timeout, cant connect with database")
        }
        catch (databaseEx: DatabaseException) {
            Log.d(TAG, "Failed with error message: ${databaseEx.message}")
            throw databaseEx
        }
    }

    /**
     * Retrieves the image associated with the given [userId] and [metadata] from the database
     *
     * @param metadata the metadata associated with the given image we want to retrieve
     * @param userId the ID of the user where we should find the image
     * @param timeout the maximum time we wait for the database to respond
     * @return the image, or null if no image was found
     *
     * @throws StorageException if the image could not be retrieved
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if the an exception occurred while retrieving the image
     */
    suspend fun getImage(metadata: PhotoMetadata, userId: Int, timeout: Long = TIMEOUT): Bitmap? {
        // Points to the node where we the image SHOULD be
        // The path will change when we will actually stores the real pictures
        return metadata.pictureId?.let {
            getPicture(storageReference.child("userId").child(
                userId.toString()).child(it), timeout)
        }
    }

    /**
     * Retrieves the profile image associated with the given [userId] and [metadata] from the database
     *
     * @param metadata the metadata associated with the given image we want to retrieve
     * @param userId the ID of the user where we should find the image
     * @param timeout the maximum time we wait for the database to respond
     * @return the image, or null if no image was found
     * @throws StorageException if the image could not be retrieved
     * @throws DatabaseTimeoutException if the database could not be reached
     * @throws DatabaseException if the an exception occurred while retrieving the image
     */
    suspend fun getUserProfilePicture(metadata: PhotoMetadata, userId: String, timeout: Long = TIMEOUT): Bitmap? {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        return metadata.pictureId?.let {
            getPicture(storageReference.child(USERS_PATH).child(userId).child(
                USER_PROFILE_PICTURE_METADATA).child(it), timeout)
        }
    }

    /**
     * Helper function to perform the actual call to the database to retrieve the image
     *
     * @param storageNode the node from which to retrieve the image
     * @param timeout the maximum time we wait for the database to respond
     * @return the retrieved [Bitmap] or null if an error occured
     */
    private suspend fun getPicture(storageNode: StorageReference, timeout: Long = TIMEOUT): Bitmap? {
        return try {
            withTimeout(timeout) {
                val compressedImage = storageNode.getBytes(ONE_MEGABYTE).await()
                BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size)
            }
        } catch (timeOutEx: TimeoutCancellationException) {
            Log.d(TAG, "Timeout, cant connect with database")
            throw DatabaseTimeoutException("Timeout, cant connect with database")
        } catch (storageEx: StorageException) {
            Log.d(TAG, "Failed with error message: ${storageEx.message}")
            throw storageEx
        }
    }

    /**
     * Helper function to store the given [image] under the [storageNode]
     *
     * @param image The image to store
     * @param parentNode An optional string that can be used to specify the name of the key under
     * which the metadata will be stored in the db, if not specified the name of the picture will
     * be used by default
     * @param photoMetadata The metadata associated with the given image
     * @param dbNode The node in which to store the metadata
     * @param storageNode The node in which to store the image
     *
     * @return The image metadata updated with the actual picture Id under witch the image has been stored
     */
    private suspend fun storePicture(image: Bitmap, parentNode: String?, photoMetadata: PhotoMetadata,
                                     dbNode: DatabaseReference, storageNode: StorageReference): PhotoMetadata? {
        // if the url is not yet contained in the metadata, then generate a new uid by calling push
        val pictureId = photoMetadata.pictureId ?: dbNode.push().key ?: return null

        photoMetadata.pictureId = pictureId

        val compressedImage = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, compressedImage)

        return try {
            storageNode.child(pictureId).putBytes(compressedImage.toByteArray()).await()
            dbNode.child(parentNode ?: pictureId).setValue(photoMetadata)
            return photoMetadata
        } catch (e: Error) {
            null
        }
    }

    /**
     * Store the given [location] as the location of a recycling bin.
     *
     * @param location the location of the bin
     *
     * @return true if the location got stored in the database, and false if it failed
     */
    suspend fun storeBinLocation(location: LatLng): Boolean{
        val freshId = databaseReference.child(BIN_LOCATION_PATH).push().key ?: return false
        databaseReference.child(BIN_LOCATION_PATH).child(freshId).setValue(location).await()
        return true
    }

    /**
     * Retrieves the set of all bin locations currently present in the database
     *
     * @return the set of locations of all the bins, or null if an error happened
     */
    suspend fun getAllBinLocations(): Set<LatLng>?{
        // Looks like manually deleting the unchecked cast is the only way to remove the warnings
        @Suppress("UNCHECKED_CAST")
        val childrens = databaseReference.child(BIN_LOCATION_PATH).get().await().value
                as? Map<String, Any> ?: return null

        val locations = mutableSetOf<LatLng>()
        for (child in childrens.values) {
            // Converts the Map back to a LatLng. If some child does not have the correct format, skip it
            @Suppress("UNCHECKED_CAST")
            val location = child as? HashMap<String, Double>? ?: child as? HashMap<String, Long>? ?: continue
            val lat = location["latitude"] ?: continue
            val lng = location["longitude"] ?: continue
            locations.add(LatLng(lat.toDouble(), lng.toDouble()))
        }
        return locations
    }

    /**
     * Retrieves the list of advices from the realtime database
     *
     * @return the set of all advices
     */
    suspend fun getAdvices(): Set<String> {

        val childrens = databaseReference.child(ADVICES_LOCATION_PATH).get().await().children
        val advicesList = mutableSetOf<String>()

        childrens.forEach { postSnapshot ->
            // If the advice is not of type String, ignore it (should never happen in theory)
            val advice = postSnapshot.value as? String
            advice?.also { advicesList.add(it) }
        }
        return advicesList
    }
}