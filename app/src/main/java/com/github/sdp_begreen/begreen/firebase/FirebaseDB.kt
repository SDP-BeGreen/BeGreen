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
    }
}