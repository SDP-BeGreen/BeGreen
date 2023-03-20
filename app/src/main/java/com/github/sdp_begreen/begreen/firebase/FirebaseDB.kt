package com.github.sdp_begreen.begreen.firebase

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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


/**
 * Database implementation using Firebase's services
 */
object FirebaseDB {

    private val TAG: String = "Firebase Database"
    private val TIMEOUT: Long = 10000 // Waiting time before aborting a <get> on the database
    private val ONE_MEGABYTE: Long = 1024 * 1024 // Maximal image size allowed (in bytes), to prevent out of memory errors
    // Realtime database ref
    private val databaseReference: DatabaseReference = Firebase.database.reference
    // Storage ref (for images)
    private val storageReference: StorageReference = Firebase.storage.reference
    private val connectedReference = Firebase.database.getReference(".info/connected")

    // Logs (in the console) the connections and disconnections with the Firebase database
    // We might want to provide a new constructor that takes code to execute on connections/disconnections
    // (to show the user when he gets connected/disconnected by example)
    init {

        // Enables disk persistence (the phone now caches data)
        //Firebase.database.setPersistenceEnabled(true)
        // If we want to keep some data synced, we can use the following instruction:
        /*
        val scoresRef = Firebase.database.getReference("scores")
        scoresRef.keepSynced(true)
         */
        // And unsync it with the following:
        /*
        scoresRef.keepSynced(false)
         */

        // We can use this code to record on the server when the user was last seen
        /*val userLastOnlineRef = Firebase.database.getReference("users/joe/lastOnline")
        userLastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)*/

        // The following code can be used to track when users are online and when they were last seen
        // Since I can connect from multiple devices, we store each connection instance separately
        // any time that connectionsRef's value is null (i.e. has no children) I am offline
        /*val database = Firebase.database
        val myConnectionsRef = database.getReference("users/joe/connections")

        // Stores the timestamp of my last disconnect (the last time I was seen online)
        val lastOnlineRef = database.getReference("/users/joe/lastOnline")

        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue<Boolean>() ?: false
                if (connected) {
                    val con = myConnectionsRef.push()

                    // When this device disconnects, remove it
                    con.onDisconnect().removeValue()

                    // When I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)

                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(java.lang.Boolean.TRUE)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled at .info/connected")
            }
        })*/

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
     * @return the value associated to the [key]
     * @throws DatabaseException if the value cannot be retrieved
     */
    @SuppressLint("RestrictedApi")
    suspend fun get(key: String): String? {

        return try {
            // Cancel the query and throw exception after TIMEOUT ms
            withTimeout(TIMEOUT) {
                val data = databaseReference.child(key).get().await()
                data.value?.let { it as? String }
            }
        } catch (timeOutEx: TimeoutCancellationException) {
            Log.d(TAG, "Timeout, cant connect with database")
            throw DatabaseException("Timeout, cant connect with database")
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
     */
    operator fun set(key: String, value: String) {
        if (key.isNotEmpty()) databaseReference.child(key).setValue(value)
    }

    /**
     * Store the given [image] under the ID [userId]
     * and returns the imageId set for that image in the database
     * Returns null if the image can't be stored
     */
    /**
     * Adds and [image] for the user [userId] in the database
     *
     * @param image the image we want to add to the database
     * @param userId the ID of the user wanting to store a new image
     * @return a unique ID under which the image got stored, and null if the image couldn't get stored
     */
    suspend fun addImage(image: Bitmap, userId: Int): String? {

        // Points to the node where we want to store the metadata of the image in the realtime database
        val imageMetadataNode = databaseReference.child("pictures").child("userId")
            .child(userId.toString())
        // Generates a new imageID for the image we're going to add, and return null if it fails
        val uniqueID = imageMetadataNode.push().key ?: return null

        // Points to the node where we want to store the image in Firebase storage
        val imageNode = storageReference.child("userId").child(userId.toString()).child(uniqueID)
        // Compress the image in a JPEG format
        val compressedImage = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, compressedImage)
        return try {
            // Upload the image
            imageNode.putBytes(compressedImage.toByteArray()).await()
            // Upload the image's metadata (might need to add/remove fields later, and pass them as args)
            val imageMetadataMap = HashMap<String, Any>()
            imageMetadataMap["time"] = System.currentTimeMillis()
            imageMetadataMap["type"] = "plastic bottle"

            imageMetadataNode.child(uniqueID).setValue(imageMetadataMap)
            uniqueID
        } catch (e: Error) {
            null
        }

        /* OLD IMPLEMENTATION
        val picturesNode = databaseReference.child("pictures").child("userId")
            .child(userId.toString())

        // Compress the Bitmap
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        // Convert the stream to a list of Integers
        val intList = stream.toByteArray().map { it.toInt() }

        // Create a unique ID for the image

        val uniqueID = picturesNode.push().key

        // Upload the image to the database
        if (uniqueID != null) {
            val imageMap = HashMap<String, Any>()
            imageMap["imageBytes"] = intList
            picturesNode.child(uniqueID).setValue(imageMap)
        }
        return uniqueID*/
    }

    /**
     * Retrieves the image associated with the given [userId] and [imageId] from the database
     *
     * @param imageId the ID of the image we want to retrieve from the database
     * @param userId the ID of the user where we should find the image
     * @return the image, or null if no image was found
     * @throws StorageException if the image could not get retrieved
     * @throws DatabaseException if the metadata could not get retrieved
     *
     */
    @SuppressLint("RestrictedApi")
    suspend fun getImage(imageId: String, userId: Int): Bitmap? {
        /*
        // Points to the node where the image metadata SHOULD be (we don't get the metadata yet)
        val imageMetadataNode = databaseReference.child("pictures").child("userId")
            .child(userId.toString()).child(imageId)*/

        // Points to the node where we the image SHOULD be
        val imageNode = storageReference.child("userId").child(userId.toString()).child(imageId)
        return try {
            withTimeout(TIMEOUT) {
                // Retrieve the (compressed) image
                val compressedImage = imageNode.getBytes(ONE_MEGABYTE).await()
                BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size)
            }
        } catch (timeOutEx: TimeoutCancellationException) {
            Log.d(TAG, "Timeout, cant connect with database")
            throw DatabaseException("Timeout, cant connect with database")
        } catch (storageEx: StorageException) {
            Log.d(TAG, "Failed with error message: ${storageEx.message}")
            throw storageEx
        }

        /* OLD IMPLEMENTATION
        return try {
            // Points to the node where the image SHOULD be
            val imageNode = databaseReference.child("pictures").child("userId")
                .child(userId.toString()).child(imageId)

            // Get the HashMap that contains the image
            val imageMap = imageNode.get().await().value
            // Map the HashMap back to a List of Ints  and return null if this fails
            val intList =
                imageMap?.let { it as? HashMap<*, *> }?.get("imageBytes") as? List<*> ?: return null
            // Map the list of ints back to a Bitmap and returns it
            val byteArray = intList.map { (it as Long).toByte() }.toByteArray()
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Error) {
            null
        }*/
    }
}