package com.github.sdp_begreen.begreen

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Database implementation using Firebase
 */
class FirebaseDB : Database() {

    private val firebaseDB: DatabaseReference = Firebase.database.reference

    override fun set(key: String, value: String) {
        firebaseDB.child(key).setValue(value)
    }

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
}