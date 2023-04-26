package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import android.util.Log
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.models.Comment
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.utils.checkArgument
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MeetingServiceImpl : MeetingService {

    companion object {
        private const val MEETING_PATH = "meeting"
        private val dbRef = Firebase.database.reference
    }

    override suspend fun createMeeting(meeting: Meeting): Meeting? =
        suspendCoroutine { continuation ->
            val meetingReference = dbRef.child(MEETING_PATH)
            meetingReference.push().key?.also {
                val meetingWithId = meeting.copy(meetingId = it)
                meetingReference.child(it).setValue(meetingWithId) { error, _ ->
                    if (error != null) {
                        // If error while adding to the db, log it and return null
                        Log.d("Meeting creation failed", error.message)
                        continuation.resume(null)
                    } else {
                        continuation.resume(meetingWithId)
                    }
                }
            } ?: continuation.resume(null) // if key is null, then return null
        }

    override suspend fun modifyMeeting(meeting: Meeting, userId: String): Meeting {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMeetings(): Flow<List<Meeting>> {
        TODO("Not yet implemented")
    }


    override suspend fun getMeeting(meetingId: String): Meeting {
        checkArgument(meetingId.isNotEmpty(), "The meeting id cannot be null")
        return suspendCoroutine { continuation ->
            dbRef.child(MEETING_PATH).child(meetingId).get().addOnSuccessListener { snapshot ->
                snapshot.getValue(Meeting::class.java)?.also {
                    continuation.resume(it)
                } ?: continuation.resumeWithException(
                    MeetingServiceException("Data not found, or could not be parsed")
                )
            }.addOnFailureListener {
                Log.d("Get meeting database error", it.message.orEmpty())
                continuation.resumeWithException(MeetingServiceException("Error from db ${it.message}"))
            }
        }
    }

    override suspend fun removeMeeting(meetingId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addComment(meetingId: String, comment: Comment): Comment {
        TODO("Not yet implemented")
    }

    override suspend fun removeComment(meetingId: String, commentId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addMeetingsPhoto(
        photoMetadata: PhotoMetadata, photo: Bitmap
    ): PhotoMetadata {
        TODO("Not yet implemented")
    }
}
