package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.meetings.Comment
import com.github.sdp_begreen.begreen.models.meetings.Meeting
import com.github.sdp_begreen.begreen.utils.checkArgument
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
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayOutputStream

object MeetingServiceImpl : MeetingService {

    private val dbRefs by inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private val storageRef = dbRefs.storageReference
    private const val MEETING_PATH = "meeting"
    private const val COMMENTS_PATH = "comments"
    private const val PARTICIPANTS_PATH = "participants"
    private const val PHOTOS_PATH = "meetingPhotos"
    private const val ONE_MEGABYTE: Long = 1024 * 1024

    override suspend fun createMeeting(meeting: Meeting): Meeting {
        checkArgument(!meeting.creator.isNullOrBlank(), "The creator cannot be blank or null")
        val meetingReference = dbRef.child(MEETING_PATH)
        return meetingReference.push().key?.let {
            val metingWithId = meeting.copy(meetingId = it)
            setObjToDb(meetingReference.child(it), metingWithId, "Error while creating the meeting")
        } ?: throw MeetingServiceException("Error while generating new key for meeting entry")
    }

    override suspend fun modifyMeeting(meeting: Meeting, userId: String): Meeting {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(
            meeting.creator == userId,
            "The user that modify the meeting must be its creator"
        )
        checkArgument(
            !meeting.meetingId.isNullOrBlank(),
            "The meeting to modify cannot have a blank or null meetingId"
        )
        return setObjToDb(
            dbRef.child(MEETING_PATH).child(meeting.meetingId!!), meeting,
            "Error while modifying the meeting"
        )
    }

    override suspend fun getAllMeetings(): Flow<List<Meeting>> = callbackFlow {
        val eventListener = createEventListener(this, Meeting::class.java)
        dbRef.child(MEETING_PATH).addValueEventListener(eventListener)

        // once done remove the listener
        awaitClose {
            dbRef.child(MEETING_PATH).removeEventListener(eventListener)
        }
    }


    override suspend fun getMeeting(meetingId: String): Meeting {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        return getObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId),
            Meeting::class.java,
            "Error while getting meeting $meetingId from the database"
        )

    }

    override suspend fun removeMeeting(meeting: Meeting, userId: String) {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(!meeting.meetingId.isNullOrBlank(), "The meeting id cannot be blank or null")
        checkArgument(
            meeting.creator == userId,
            "The user that modify the comment must be its author"
        )

        removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meeting.meetingId!!),
            "Error while removing the meeting"
        )
    }

    override suspend fun addParticipant(meetingId: String, participantId: String): String {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")

        return setObjToDb(
            dbRef.child(MEETING_PATH).child(meetingId).child(PARTICIPANTS_PATH)
                .child(participantId),
            participantId,
            "Error while adding a participant"
        )
    }

    override suspend fun getAllParticipants(meetingId: String): Flow<List<String>> {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        return callbackFlow {
            val eventListener = createEventListener(this, String::class.java)
            dbRef.child(MEETING_PATH).child(meetingId).child(PARTICIPANTS_PATH)
                .addValueEventListener(eventListener)

            // once done remove the listener
            awaitClose {
                dbRef.child(MEETING_PATH).child(meetingId).child(PARTICIPANTS_PATH)
                    .removeEventListener(eventListener)
            }
        }
    }

    override suspend fun removeParticipant(meetingId: String, participantId: String) {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")

        removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId).child(PARTICIPANTS_PATH)
                .child(participantId),
            "Error while removing the meeting"
        )
    }

    override suspend fun addComment(meetingId: String, comment: Comment): Comment {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !comment.author.isNullOrBlank(),
            "The author of the comment cannot be blank or null"
        )
        val commentReference = dbRef.child(MEETING_PATH).child(meetingId).child(COMMENTS_PATH)
        return commentReference.push().key?.let {
            val commentWithId = comment.copy(commentId = it)
            setObjToDb(
                commentReference.child(it),
                commentWithId,
                "Error while creating the comment"
            )
        } ?: throw MeetingServiceException("Error while generating new key for comment entry")
    }

    override suspend fun modifyComment(
        meetingId: String,
        userId: String,
        comment: Comment
    ): Comment {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(
            comment.author == userId,
            "The user that modify the comment must be its author"
        )
        checkArgument(
            !comment.commentId.isNullOrBlank(),
            "The comment to modify cannot have an empty or blank commentId"
        )
        checkArgument(
            meetingId.isNotBlank(),
            "The meeting id for which to modify a comment cannot be blank"
        )

        return setObjToDb(
            dbRef.child(MEETING_PATH).child(meetingId).child(COMMENTS_PATH)
                .child(comment.commentId!!),
            comment,
            "Error while modifying the comment"
        )
    }

    override suspend fun getAllComments(meetingId: String): Flow<List<Comment>> {
        checkArgument(meetingId.isNotBlank(), "The meeting id should not be blank")
        return callbackFlow {
            val eventListener = createEventListener(this, Comment::class.java)
            dbRef.child(MEETING_PATH).child(meetingId).child(COMMENTS_PATH)
                .addValueEventListener(eventListener)

            // once done remove the listener
            awaitClose {
                dbRef.child(MEETING_PATH).child(meetingId).child(COMMENTS_PATH)
                    .removeEventListener(eventListener)
            }
        }
    }

    override suspend fun removeComment(meetingId: String, comment: Comment, userId: String) {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(!comment.commentId.isNullOrBlank(), "The comment id cannot be blank or null")

        removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId).child(COMMENTS_PATH)
                .child(comment.commentId!!), "Error while removing the comment"
        )
    }

    override suspend fun addMeetingsPhoto(
        meetingId: String,
        photoMetadata: PhotoMetadata,
        photo: Bitmap
    ): PhotoMetadata {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !photoMetadata.takenBy.isNullOrBlank(),
            "The user that took the photo cannot be blank or null"
        )
        val photoRef = dbRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
        return photoRef.push().key?.let {
            val newPhotoMetadata = photoMetadata.copy(pictureId = it)
            val compressedImage = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 100, compressedImage)
            putBytesToStorage(
                storageRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH).child(it),
                compressedImage.toByteArray(),
                "Error while storing the photo"
            )
            setObjToDb(
                photoRef.child(it),
                newPhotoMetadata,
                "Error while adding the metadata for the photo"
            )
        } ?: throw MeetingServiceException("Error while generating new key for photo entry")
    }

    override suspend fun getAllPhotosMetadata(meetingId: String): Flow<List<PhotoMetadata>> {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        return callbackFlow {
            val eventListener = createEventListener(this, PhotoMetadata::class.java)
            dbRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
                .addValueEventListener(eventListener)

            // once done remove the listener
            awaitClose {
                dbRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
                    .removeEventListener(eventListener)
            }
        }
    }

    override suspend fun getPhoto(meetingId: String, photoMetadata: PhotoMetadata): Bitmap? {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !photoMetadata.pictureId.isNullOrBlank(),
            "The picture id cannot be blank or null"
        )

        val compressedPhoto = getBytesFromStorage(
            storageRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
                .child(photoMetadata.pictureId!!),
            "Error while getting picture bytes from storage"
        )
        return BitmapFactory.decodeByteArray(compressedPhoto, 0, compressedPhoto.size)
    }

    override suspend fun removeMeetingPhoto(meetingId: String, photoMetadata: PhotoMetadata) {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !photoMetadata.pictureId.isNullOrBlank(),
            "The picture id cannot be blank or null"
        )

        removeObjFromStorage(
            storageRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
                .child(photoMetadata.pictureId!!),
            "Error while removing the photo from the storage"
        )
        removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
                .child(photoMetadata.pictureId!!), "Error while removing the photo metadata"
        )
    }

    /**
     * Helper function to save an object into the database
     *
     * @param reference The reference where to store the object
     * @param obj The object to store
     * the outer coroutine
     * @param errorMessage The error message to add to the thrown exception
     */
    private suspend fun <T> setObjToDb(
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
     * Helper function to save an object into the storage
     *
     * @param reference The reference where to store the object
     * @param bytes The bytes to store
     * @param errorMessage The error message to add to the thrown exception
     */
    private suspend fun putBytesToStorage(
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
     * Helper function to get an object form the database
     *
     * @param reference The reference from where to get the object
     * @param valueType The type of value we are trying to get
     * @param errorMessage The error message to add to the thrown exception
     */
    private suspend fun <T> getObjFromDb(
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
     * Helper function to get the bytes fro the storage
     *
     * @param reference The reference from where to get the object
     * @param errorMessage The error message to add to the thrown exception
     */
    private suspend fun getBytesFromStorage(
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
     * Helper function to remove an object from the database
     *
     * @param reference The reference of the object to remove
     * @param errorMessage The error message to add to the thrown exception
     */
    private suspend fun removeObjFromDb(reference: DatabaseReference, errorMessage: String) {
        try {
            reference.removeValue().await()
        } catch (e: Exception) {
            Log.d("Object removing failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}", e)
        }
    }

    /**
     * Helper function to remove an object from the storage
     *
     * @param reference The reference of the object to remove
     * @param errorMessage The error message to add to the thrown exception
     */
    private suspend fun removeObjFromStorage(reference: StorageReference, errorMessage: String) {
        try {
            reference.delete().await()
        } catch (e: Exception) {
            Log.d("Object removing from storage failed", e.message.orEmpty())
            throw MeetingServiceException("$errorMessage ${e.message}", e)
        }
    }

    /**
     * Helper function to create a new event listener to listen for change in a list of elements
     * in the database
     *
     * @param producer The producer to call to send new array upon changes
     * @param valueType The type of value we are reading
     */
    private fun <T> createEventListener(
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
