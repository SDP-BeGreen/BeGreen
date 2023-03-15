package com.github.sdp_begreen.begreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

/**
 * Database implementation using Firebase's realtime database
 */
class FirebaseDB() {

    /**
     * Singleton value to access the database
     */
    companion object {
        val db: FirebaseDB = FirebaseDB()
    }

    private val databaseReference: DatabaseReference = Firebase.database.reference

    /**
     * Return the value associated with the given [key]
     * If an error occurs (by ex: because the key does not exist,
     * or because the value associated with the key is not a value), returns null
     */
    suspend fun get(key: String): String? {
        return try {
            val data = databaseReference.child(key).get().await()
            data.value?.let { it as? String }
        } catch (e: Error) {
            null
        }
    }

    /**
     * Create a new tuple ([key], [value]) if [key] was not present and update the value otherwise
     */
    operator fun set(key: String, value: String) {
        databaseReference.child(key).setValue(value)
    }

    /**
     * Store the given [image] under the ID [userId]
     * and returns the imageId set for that image in the database
     * Returns null if the image can't be stored
     */
    fun addImage(image: Bitmap, userId: Int): String? {

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
        return uniqueID
    }

    /**
     * Retrieves the image associated with the given [userId] and [imageId]
     * from the database. Returns null in case of an error of the DB, or if no image is found
     */
    suspend fun getImage(imageId: String, userId: Int): Bitmap? {

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
        }
    }
}