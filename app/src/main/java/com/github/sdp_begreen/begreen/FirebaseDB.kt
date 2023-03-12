package com.github.sdp_begreen.begreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture

/**
 * Database implementation using Firebase's realtime database
 */
class FirebaseDB() {

    /**
     * Singleton value to access the database
     */
    companion object {
        var db: FirebaseDB = FirebaseDB()
    }

    /**
     * Overwritten during tests! (to perform tests locally)
     */
    var databaseReference: DatabaseReference = Firebase.database.reference

    /**
     * Return the value associated with the given [key]
     * If an error occurs (by ex: because the key does not exist,
     * or because the value associated with the key is not a value), returns CompletableFuture<null>
     */
    operator fun get(key: String): CompletableFuture<String> {

        val future = CompletableFuture<String>()

        databaseReference.child(key).get().addOnSuccessListener {
            // Check if the value has the expected format
            if (it.value == null || it.value !is String) future.complete(null)
            else future.complete(it.value as String)

        }.addOnFailureListener {
            future.complete(null)
        }

        return future
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
     * from the database. Completes exceptionally in case of an error of the DB
     * Returns CompletableFuture<IllegalArgumentException> if no image is found
     */
    fun getImage(imageId: String, userId: Int): CompletableFuture<Bitmap> {

        // Points to the node where the image SHOULD be
        val imageNode = databaseReference.child("pictures").child("userId")
            .child(userId.toString()).child(imageId)

        val future = CompletableFuture<Bitmap>()


        imageNode.addListenerForSingleValueEvent(object : ValueEventListener {
            // Retrieves the image from the database, and map it back to a Bitmap
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageMap = dataSnapshot.value
                // Checks that the node indeed contains an image, and convert it back to a Bitmap
                if (imageMap != null && imageMap is HashMap<*,*> && imageMap["imageBytes"] != null) {
                    val intList = imageMap["imageBytes"] as List<*>
                    val byteArray = intList.map { (it as Long).toByte() }.toByteArray()
                    val bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    future.complete(bitmapImage)
                } else {
                    future.completeExceptionally(java.lang.IllegalArgumentException())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                future.completeExceptionally(databaseError.toException())
            }
        })

        return future
    }
}