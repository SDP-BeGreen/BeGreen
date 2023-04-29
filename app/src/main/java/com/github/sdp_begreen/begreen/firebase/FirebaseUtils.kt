package com.github.sdp_begreen.begreen.firebase

import android.util.Log
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Object that contains utility methods to interact with Firebase
 */
object FirebaseUtils {

    private const val ONE_MEGABYTE: Long = 1024 * 1024

    /**
     * Function to save an object into the database
     *
     * @param reference The reference where to store the object
     * @param obj The object to store
     * the outer coroutine
     * @param errorMessage The error message to add to the thrown exception
     *
     * @return The object that has been saved
     *
     * @throws MeetingServiceException If an error occurred while saving the object to the database
     */
    suspend fun <T> setObjToDb(
        reference: DatabaseReference,
        obj: T,
        errorMessage: String
    ): T {
        try {
            reference.setValue(obj).await()
            return obj
        } catch (e: Exception) {
            Log.d("Object addition failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}", e)
        }
    }

    /**
     * Function to save an object into the storage
     *
     * @param reference The reference where to store the object
     * @param bytes The bytes to store
     * @param errorMessage The error message to add to the thrown exception
     *
     * @throws MeetingServiceException If an error occurred while saving the object to the storage
     */
    suspend fun putBytesToStorage(
        reference: StorageReference,
        bytes: ByteArray,
        errorMessage: String
    ) {
        try {
            reference.putBytes(bytes).await()
        } catch (e: Exception) {
            Log.d("Object storing failed", e.message.orEmpty())
            throw MeetingServiceException(
                "$errorMessage ${e.message}", e
            )
        }
    }

    /**
     * Function to get an object form the database
     *
     * @param reference The reference from where to get the object
     * @param valueType The type of value we are trying to get
     * @param errorMessage The error message to add to the thrown exception
     *
     * @return The required object, if it could be fetched and parsed
     *
     * @throws MeetingServiceException If an error occurred while getting the object from the database
     */
    suspend fun <T> getObjFromDb(
        reference: DatabaseReference,
        valueType: Class<T>,
        errorMessage: String
    ): T {
        return try {
            Log.d("Passed Here in test", "Passed here")
            reference.get().await().getValue(valueType)
        } catch (e: Exception) {
            Log.d("Object retrieval failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}")
        } ?: throw MeetingServiceException("Data not found, or could not be parsed")
    }

    /**
     * Function to get the bytes fro the storage
     *
     * @param reference The reference from where to get the object
     * @param errorMessage The error message to add to the thrown exception
     *
     * @return The array of byte representing the object retrieved from the storage
     *
     * @throws MeetingServiceException If an error occurred while getting the object from the storage
     */
    suspend fun getBytesFromStorage(
        reference: StorageReference,
        errorMessage: String
    ): ByteArray {
        return try {
            reference.getBytes(ONE_MEGABYTE).await()
        } catch (e: Exception) {
            Log.d("Object loading failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}")
        }
    }

    /**
     * Function to remove an object from the database
     *
     * @param reference The reference of the object to remove
     * @param errorMessage The error message to add to the thrown exception
     *
     * @throws MeetingServiceException If an error occurred while removing the object from the database
     */
    suspend fun removeObjFromDb(reference: DatabaseReference, errorMessage: String) {
        try {
            reference.removeValue().await()
        } catch (e: Exception) {
            Log.d("Object removing failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}", e)
        }
    }

    /**
     * Function to remove an object from the storage
     *
     * @param reference The reference of the object to remove
     * @param errorMessage The error message to add to the thrown exception
     *
     * @throws MeetingServiceException If an error occurred while removing the object from the storage
     */
    suspend fun removeObjFromStorage(reference: StorageReference, errorMessage: String) {
        try {
            reference.delete().await()
        } catch (e: Exception) {
            Log.d("Object removing from storage failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}", e)
        }
    }

    /**
     * Function to get a flow of objects (i.e. list of object), by placing a listener
     * on a node in the database
     *
     * @param reference The reference node on which to place the listener
     * @param valueType The type of value we are reading on the node
     *
     * @return The flow of a list of objects, new value emitted upon changes in the listened node
     */
    suspend fun <T> getFlowOfObjects(
        reference: DatabaseReference,
        valueType: Class<T>
    ): Flow<List<T>> = callbackFlow {
        val eventListener = createEventListenerListOfObjects(this, valueType)
        reference.addValueEventListener(eventListener)

        // once done remove the listener
        awaitClose {
            reference.removeEventListener(eventListener)
        }
    }

    /**
     * Helper function to create a new event listener to listen for change in a list of elements
     * in the database
     *
     * @param producer The producer to call to send new array upon changes
     * @param valueType The type of value we are reading
     */
    private fun <T> createEventListenerListOfObjects(
        producer: ProducerScope<List<T>>,
        valueType: Class<T>
    ) = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            producer.trySend(snapshot.children.mapNotNull { it.getValue(valueType) })
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("Get list of all comment error", error.message)
        }
    }
}
