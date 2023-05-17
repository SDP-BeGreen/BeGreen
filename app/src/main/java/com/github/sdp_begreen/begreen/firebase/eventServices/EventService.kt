package com.github.sdp_begreen.begreen.firebase.eventServices

import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Event
import kotlinx.coroutines.flow.Flow

/**
 * Service to interact with the database to work with events
 */
interface EventService {

    /**
     * Create a new event in the database
     *
     * @param event The event to be created in the database
     *
     * @return The event, with its newly generated id
     *
     * @throws IllegalArgumentException If the creator of the event is blank or null
     * @throws EventServiceException If it was impossible to generate the new event key,
     * or if an error occurred while adding the event.
     */
    suspend fun <T : Event<T>> createEvent(event: T): T

    /**
     * Modify an already existing event in the database
     *
     * @param event The modified event to save in the database
     * @param userId The id of the user that wants to modify the event
     * Only the user that created the event should be able to modify it
     *
     * @return The modified event
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws EventServiceException Throw if an error occurred while adding the event
     */
    suspend fun <T : Event<T>> modifyEvent(event: T, userId: String): T
    
    

    /**
     * Get all the event. Will get the events dynamically, list stay up to date
     * upon event changes.
     *
     * The returned list of events only contains events that have not yet started,
     * and they are ordered from soonest to latest.
     *
     * @param rootPath The enum object representing the path where to find the object we want
     * @param eventImplType The class of the object we expect to retrieve
     *
     * @return A flow of all the events
     */
    suspend fun <T : Event<T>> getAllUpcomingEvents(rootPath: RootPath, eventImplType: Class<T>): Flow<List<T>>

    /**
     * Get all the event. Will get the events dynamically, list stay up to date
     * upon event changes.
     *
     * The returned list of events only contains events that have already started but not finished,
     * and they are ordered from soonest to latest.
     *
     * @param rootPath The enum object representing the path where to find the object we want
     * @param eventImplType The class of the object we expect to retrieve
     *
     * @return A flow of all the events
     */
    suspend fun <T : Event<T>> getAllOngoingEvents(rootPath: RootPath, eventImplType: Class<T>): Flow<List<T>>

    /**
     * Get an event given its id
     *
     * @param eventId The id of the event to get from the database
     * @param rootPath The enum object representing the path where to find the object we want
     * @param eventImplType The class of the object we expect to retrieve
     *
     * @return The fetched event
     *
     * @throws IllegalArgumentException Throw if the event id is empty
     * @throws EventServiceException if the data could not be parsed, or if an error
     * occurred on the database side
     */
    suspend fun <T : Event<T>> getEvent(eventId: String, rootPath: RootPath, eventImplType: Class<T>): T

    /**
     * Remove a event from the database
     *
     * @param event The event to be removed
     * @param userId The id of the user that want to remove the meeting
     *
     * @throws IllegalArgumentException Throw if one of the argument does not match the requirement
     * @throws EventServiceException Throw if an error occurred while removing the meeting
     */
    suspend fun <T : Event<T>> removeEvent(event: T, userId: String)
}