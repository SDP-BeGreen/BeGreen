package com.github.sdp_begreen.begreen.firebase.eventServices

import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import kotlinx.coroutines.flow.Flow

/**
 * Service to interact with the database to work with meeting's participants
 */
interface EventParticipantService {

    /**
     * Get the participant with the given [participantId]
     *
     * @param rootPath The enum object representing the path where to find all the participants
     * @param participantId The id of the participant we want to retrieve
     * @param eventId The id of the event from which to retrieve the participant
     *
     * @return A flow of participants ids
     *
     * @throws IllegalArgumentException Throw if the event id is blank or the participant id is blank
     */
    suspend fun <T : EventParticipant> getParticipant(
        rootPath: RootPath,
        eventId: String,
        participantId: String,
        clazz: Class<T>
    ): T

    /**
     * Get all the participants. The participants will be returned dynamically in a flow
     *
     * @param rootPath The enum object representing the path where to find all the participants
     * @param eventId The id of the event from which to retrieve the participants
     *
     * @return A flow of participants ids
     *
     * @throws IllegalArgumentException Throw if the event id is blank
     */
    suspend fun <T : EventParticipant> getAllParticipants(
        rootPath: RootPath,
        eventId: String,
        clazz: Class<T>
    ): Flow<List<T>>

    /**
     * Add a new participant to the event
     *
     * @param rootPath The enum object representing the path where to add the participant to
     * @param eventId The id of the event to which add the participant
     * @param participant The participant to add
     *
     * @return The added participant
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws EventServiceException Throw if an error occurred while adding the participant
     */
    suspend fun <T : EventParticipant> addParticipant(rootPath: RootPath,  eventId: String, participant: T): T

    /**
     * Remove a participant from the event
     *
     * @param rootPath The enum object representing the path where to remove the participant from
     * @param eventId The id of the event to which add the participant
     * @param participantId The id of the participant to add
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws EventServiceException Throw if an error occurred while removing the participant
     */
    suspend fun removeParticipant(rootPath: RootPath, eventId: String, participantId: String)
}