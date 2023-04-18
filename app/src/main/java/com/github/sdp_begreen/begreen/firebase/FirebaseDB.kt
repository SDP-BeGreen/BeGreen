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
object FirebaseDB: DB {

    private const val TAG: String = "Firebase Database"

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

    override suspend fun get(key: String, timeout: Long): String? {

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

    override suspend fun set(key: String, value: String) {
        if (key.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        databaseReference.child(key).setValue(value).await()
    }

    override suspend fun addUser(user: User, userId: String) {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        databaseReference.child(USERS_PATH).child(userId).setValue(user).await()
    }

    override suspend fun getUser(userId: String, timeout: Long): User? {
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

    override suspend fun getAllUsers(timeout: Long): List<User> {
        return try {
            withTimeout(timeout) {
                val data = databaseReference.child(USERS_PATH).get().await()
                val users = mutableListOf<User>()
                data.children.forEach {
                    users.add(it.getValue(User::class.java)!!)
                }
                users
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

    override suspend fun storeUserProfilePicture(image: Bitmap, userId: String, metadata: PhotoMetadata): PhotoMetadata? {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        metadata.pictureId = "${userId}_profile_picture"

        return storePicture(image, USER_PROFILE_PICTURE_METADATA, metadata,
            databaseReference.child(USERS_PATH).child(userId),
            storageReference.child(USERS_PATH).child(userId).child(
                USER_PROFILE_PICTURE_METADATA))
    }

    override suspend fun addImage(image: Bitmap, userId: Int, metadata: PhotoMetadata): PhotoMetadata? {

        return storePicture(image, null, metadata,
            databaseReference.child("pictures").child(userId.toString()),
            storageReference.child("userId").child(userId.toString()))
    }

    override suspend fun userExists(userId: String, timeout: Long): Boolean {
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

    override suspend fun getImage(metadata: PhotoMetadata, userId: Int, timeout: Long): Bitmap? {
        // Points to the node where we the image SHOULD be
        // The path will change when we will actually stores the real pictures
        return metadata.pictureId?.let {
            getPicture(storageReference.child("userId").child(
                userId.toString()).child(it), timeout)
        }
    }

    override suspend fun getUserProfilePicture(metadata: PhotoMetadata, userId: String, timeout: Long): Bitmap? {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        return metadata.pictureId?.let {
            getPicture(storageReference.child(USERS_PATH).child(userId).child(
                USER_PROFILE_PICTURE_METADATA).child(it), timeout)
        }
    }

    override suspend fun storeBinLocation(location: LatLng): Boolean {
        val freshId = databaseReference.child(BIN_LOCATION_PATH).push().key ?: return false
        databaseReference.child(BIN_LOCATION_PATH).child(freshId).setValue(location).await()
        return true
    }

    override suspend fun getAllBinLocations(): Set<LatLng>? {
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

    override suspend fun getAdvices(): Set<String> {

        val childrens = databaseReference.child(ADVICES_LOCATION_PATH).get().await().children
        return childrens.mapNotNull {
            val advice = it.value as? String
            advice
        }.toSet()
    }

    /**
     * Helper function to perform the actual call to the database to retrieve the image
     *
     * @param storageNode the node from which to retrieve the image
     * @param timeout the maximum time we wait for the database to respond
     * @return the retrieved [Bitmap] or null if an error occured
     */
    private suspend fun getPicture(storageNode: StorageReference, timeout: Long): Bitmap? {
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
}