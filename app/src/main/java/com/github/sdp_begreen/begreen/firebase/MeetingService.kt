package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import com.github.sdp_begreen.begreen.models.Comment
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import kotlinx.coroutines.flow.Flow

interface MeetingService {

    /**
     * Create a new meeting in the database
     *
     * @param meeting The meeting to be created in the database
     * @return The meeting, with its newly generated id, or null if
     * it was not possible to add it
     */
    suspend fun createMeeting(meeting: Meeting): Meeting?

    /**
     * Modify an already existing meeting in the database
     *
     * @param meeting The modified meeting to save in the database
     * @param userId The id of the user that wants to modify the meeting
     */
    suspend fun modifyMeeting(meeting: Meeting, userId: String): Meeting

    /**
     * Get all the meeting. Will get the meetings dynamically, list stay up to date
     * upon meeting changes.
     */
    suspend fun getAllMeetings(): Flow<List<Meeting>>

    /**
     * Get a meeting given its meetingId
     *
     * @param meetingId The id of the meeting to get from the database
     * @throws IllegalArgumentException Throw if the meeting id is empty
     * @throws MeetingServiceException if the data could not be parsed, or if an error
     * occurred on the database side
     */
    suspend fun getMeeting(meetingId: String): Meeting?

    /**
     * Remove a meeting from the database
     *
     * @param meetingId The id of the meeting to be removed
     * @param userId The id of hte user that want to remove the meeting
     */
    suspend fun removeMeeting(meetingId: String, userId: String)

    /**
     * Add a comment to the meeting corresponding to the received [meetingId]
     *
     * @param meetingId The id of the meeting for which to add a comment
     * @param comment The comment to add to the meeting
     */
    suspend fun addComment(meetingId: String, comment: Comment): Comment

    /**
     * Remove a comment identified by its [commentId] from a meeting identified by its [meetingId]
     *
     * @param meetingId The id of the meeting from which to remove the comment
     * @param commentId The id of the comment to be removed
     */
    suspend fun removeComment(meetingId: String, commentId: String, userId: String)

    /**
     * Add a photo to the meeting
     *
     * @param photoMetadata The metadata of the photo to be added
     * @param photo The actual photo to add
     */
    suspend fun addMeetingsPhoto(photoMetadata: PhotoMetadata, photo: Bitmap): PhotoMetadata
}