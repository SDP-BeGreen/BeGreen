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
 * Database implementation using Firebase
 */
class FirebaseDB : Database() {

    private val TAG = "FirebaseImageUpload"

    private val firebaseDB: DatabaseReference = Firebase.database.reference

    /**
     * Return the value associated with the given [key]
     */
    override fun get(key: String): CompletableFuture<String> {

        val future = CompletableFuture<String>()

        firebaseDB.child(key).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    /**
     * Create a new tuple ([key], [value]) if [key] was not present and update the value otherwise
     */
    override fun set(key: String, value: String) {
        firebaseDB.child(key).setValue(value)
    }

    /**
     * Store the given [image] under the ID [userId]
     * and returns the imageId set for that image in the database
     * Returns null if the image can't be stored
     */
    override fun addImage(image: Bitmap, userId: Int): String? {

        val picturesNode = firebaseDB.child("pictures").child("userId")
            .child(userId.toString())

        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        // Convert the stream to a list of Integers
        val intList = stream.toByteArray().map{ it.toInt()}

        // Create a unique ID for the image
        val uniqueID = picturesNode.push().key

        // Upload the image to the database
        if (uniqueID != null) {
            val imageMap = HashMap<String, Any>()
            imageMap["imageBytes"] = intList
            // Logs messages in case of success or failure
            picturesNode.child(uniqueID).setValue(imageMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Image uploaded successfully")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Error uploading image", it)
                }
        }
        return uniqueID
    }

    /**
     * Retrieves the image associated with the given [userId] and [imageId]
     * from the database
     */
    override fun getImage(imageId: String, userId: Int): CompletableFuture<Bitmap> {

        // Points to the node where the image SHOULD be
        val imageNode = firebaseDB.child("pictures").child("userId")
            .child(userId.toString()).child(imageId)

        val future = CompletableFuture<Bitmap>()


        imageNode.addListenerForSingleValueEvent(object : ValueEventListener {
            // Retrieves the image from the database, and map it back to a Bitmap
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageMap = dataSnapshot.value as HashMap<*, *>
                val intList = imageMap["imageBytes"] as List<*>

                val byteArray = intList.map { (it as Long).toByte() }.toByteArray()
                val bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                future.complete(bitmapImage)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                future.completeExceptionally(databaseError.toException())
            }
        })

        return future
    }
}