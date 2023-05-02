package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.models.Meeting
import kotlinx.coroutines.flow.Flow

/**
 * Service to interact with the database to work with meetings
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
     * The returned list of meetings only contains meetings that have not yet occurred,
     * and they are ordered from soonest to latest.
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
}