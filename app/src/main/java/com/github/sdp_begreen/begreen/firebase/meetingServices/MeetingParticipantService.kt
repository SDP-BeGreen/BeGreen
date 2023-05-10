package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import kotlinx.coroutines.flow.Flow

/**
 * Service to interact with the database to work with meeting's participants
 */
interface MeetingParticipantService {

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
     * @throws EventServiceException Throw if an error occurred while adding the participant
     */
    suspend fun addParticipant(meetingId: String, participantId: String): String

    /**
     * Remove a participant from the meeting
     *
     * @param meetingId The id of the meeting to which add the participant
     * @param participantId The id of the participant to add
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws EventServiceException Throw if an error occurred while removing the participant
     */
    suspend fun removeParticipant(meetingId: String, participantId: String)
}