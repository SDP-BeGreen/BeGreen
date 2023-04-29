package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.meetings.Comment
import com.github.sdp_begreen.begreen.models.meetings.Meeting
import kotlinx.coroutines.flow.Flow

/**
 * Interface that gives all the possible function to work with the meeting part of the
 * database
 */
interface MeetingService {

    /**
     * Create a new meeting in the database
     *
     * @param meeting The meeting to be created in the database
     *
     * @return The meeting, with its newly generated id
     *
     * @throws IllegalArgumentException If the creator of the meeting is blank or null
     * @throws MeetingServiceException If it was impossible to generate the new meeting key,
     * or if an error occurred while adding the meeting
     */
    suspend fun createMeeting(meeting: Meeting): Meeting

    /**
     * Modify an already existing meeting in the database
     *
     * @param meeting The modified meeting to save in the database
     * @param userId The id of the user that wants to modify the meeting
     * Only the user that created the meeting should be able to modify it
     *
     * @return The modified meeting
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while adding the meeting
     */
    suspend fun modifyMeeting(meeting: Meeting, userId: String): Meeting

    /**
     * Get all the meeting. Will get the meetings dynamically, list stay up to date
     * upon meeting changes.
     *
     * @return A flow of all the meetings
     */
    suspend fun getAllMeetings(): Flow<List<Meeting>>

    /**
     * Get a meeting given its meetingId
     *
     * @param meetingId The id of the meeting to get from the database
     *
     * @return The fetched meeting
     *
     * @throws IllegalArgumentException Throw if the meeting id is empty
     * @throws MeetingServiceException if the data could not be parsed, or if an error
     * occurred on the database side
     */
    suspend fun getMeeting(meetingId: String): Meeting

    /**
     * Remove a meeting from the database
     *
     * @param meeting The meeting to be removed
     * @param userId The id of the user that want to remove the meeting
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while removing the meeting
     */
    suspend fun removeMeeting(meeting: Meeting, userId: String)

    /**
     * Get all the participants. The participants will be returned dynamically in a flow
     *
     * @param meetingId The id of the meeting from which to retrieve the participants
     *
     * @return A flow of participants ids
     *
     * @throws IllegalArgumentException Throw if the meeting id is blank
     */
    suspend fun getAllParticipants(meetingId: String): Flow<List<String>>

    /**
     * Add a new participant to the meeting
     *
     * @param meetingId The id of the meeting to which add the participant
     * @param participantId The id of the participant to add
     *
     * @return The id of the participant
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while adding the participant
     */
    suspend fun addParticipant(meetingId: String, participantId: String): String

    /**
     * Remove a participant from the meeting
     *
     * @param meetingId The id of the meeting to which add the participant
     * @param participantId The id of the participant to add
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while removing the participant
     */
    suspend fun removeParticipant(meetingId: String, participantId: String)

    /**
     * Add a comment to the meeting corresponding to the received [meetingId]
     *
     * @param meetingId The id of the meeting for which to add a comment
     * @param comment The comment to add to the meeting
     *
     * @return The comment along with its newly generated commentId
     *
     * @throws IllegalArgumentException Throw if the meetingId is blank
     * @throws MeetingServiceException Throw if the new key for the comment could not be generated,
     * or if an error occurred while adding it
     */
    suspend fun addComment(meetingId: String, comment: Comment): Comment

    /**
     * Modify an existing comment in the database
     *
     * @param meetingId The id of the meeting in which the comment is
     * @param userId The id of the user that wrote the comment, only the user that wrote the
     * comment initially should be able to modify it
     * @param comment The modified comment to save in the database
     *
     * @return The comment that we modified
     *
     * @throws IllegalArgumentException Throw if one of the argument does not meet the requirement
     * @throws MeetingServiceException Throw  if an error occurred while adding the comment
     * to the database
     */
    suspend fun modifyComment(meetingId: String, userId: String, comment: Comment): Comment

    /**
     * Get all the comments, retrieve the comment dynamically
     *
     * @param meetingId The id of the meeting from which to retrieve the list of comment
     *
     * @return a flow of all the comments for a particular meeting
     *
     * @throws IllegalArgumentException Throw if the meting id is blank
     */
    suspend fun getAllComments(meetingId: String): Flow<List<Comment>>

    /**
     * Remove a comment identified by its [comment] from a meeting identified by its [meetingId]
     *
     * @param meetingId The id of the meeting from which to remove the comment
     * @param comment The comment to be removed
     * @param userId The id of the user that wrote the comment. Only the user that wrote the comment
     * can remove it
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while removing the comment
     */
    suspend fun removeComment(meetingId: String, comment: Comment, userId: String)

    /**
     * Add a photo to the meeting
     *
     * @param meetingId The id of the meeting to which to add the photo
     * @param photoMetadata The metadata of the photo to be added
     * @param photo The actual photo to add
     *
     * @return The photo metadata containing the new id generated when adding the photo
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while adding the picture to the database,
     * or if an error occurred while generating the key for the photo
     */
    suspend fun addMeetingsPhoto(
        meetingId: String,
        photoMetadata: PhotoMetadata,
        photo: Bitmap
    ): PhotoMetadata

    /**
     * Get all the comments, retrieve the comment dynamically
     *
     * @param meetingId The id of the meeting from which to retrieve the list of photo metadata
     *
     * @return a flow of all the photos metadata for a particular meeting
     *
     * @throws IllegalArgumentException Throw if the meting id is blank
     */
    suspend fun getAllPhotosMetadata(meetingId: String): Flow<List<PhotoMetadata>>

    /**
     * Get a photo given its metadata
     *
     * @param meetingId The id of the meting from which to retrieve the photo
     * @param photoMetadata The metadata of the photo to retrieve
     *
     * @return The photo as a bitmap, or null if the photo could not be decoded
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while retrieving the photo
     */
    suspend fun getPhoto(meetingId: String, photoMetadata: PhotoMetadata): Bitmap?

    /**
     * Remove a photo from the meeting
     *
     * @param meetingId The id of the meeting from which to remove the photo
     * @param photoMetadata The metadata of the photo to remove
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws MeetingServiceException Throw if an error occurred while removing the comment
     */
    suspend fun removeMeetingPhoto(meetingId: String, photoMetadata: PhotoMetadata)
}