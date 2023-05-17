package com.github.sdp_begreen.begreen.firebase.eventServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.removeObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.utils.checkArgument
import com.github.sdp_begreen.begreen.utils.checkRootPathMatchEventClassImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent.inject

object EventServiceImpl : EventService {

    private val dbRefs by inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference

    override suspend fun <T : Event<T>> createEvent(event: T): T {
        checkArgument(!event.creator.isNullOrBlank(), "The creator cannot be blank or null")
        checkArgument(event.startDateTime != null, "The starting time cannot be null")
        val eventReference = dbRef.child(event.rootPath.path)
        return eventReference.push().key?.let {
            val eventWithId = event.copyWithNewId(it)
            setObjToDb(eventReference.child(it), eventWithId, "Error while creating the event")
        } ?: throw EventServiceException("Error while generating new key for event entry")
    }

    override suspend fun <T : Event<T>> modifyEvent(event: T, userId: String): T {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(
            event.creator == userId,
            "The user that modify the event must be its creator"
        )
        checkArgument(
            !event.id.isNullOrBlank(),
            "The event to modify cannot have a blank or null id"
        )
        return setObjToDb(
            dbRef.child(event.rootPath.path).child(event.id!!), event,
            "Error while modifying the event"
        )
    }

    override suspend fun <T : Event<T>> getAllUpcomingEvents(
        rootPath: RootPath,
        eventImplType: Class<T>
    ): Flow<List<T>> {
        return getAllEvents(rootPath, eventImplType, true)
    }

    override suspend fun <T : Event<T>> getAllOngoingEvents(
        rootPath: RootPath,
        eventImplType: Class<T>
    ): Flow<List<T>> {
        return getAllEvents(
            rootPath,
            eventImplType,
            false
        ).map { events -> events.filter { it.isStarted() ?: false } }
    }

    /**
     * If [notStartedEvents] is true, returns the flow of all events that did not start yet and
     * if [notStartedEvents] is false, returns the flow of all events that did not end yet
     */
    private suspend fun <T : Event<T>> getAllEvents(
        rootPath: RootPath,
        eventImplType: Class<T>,
        notStartedEvents: Boolean
    ): Flow<List<T>> {
        checkRootPathMatchEventClassImpl(rootPath, eventImplType)

        return if (notStartedEvents) {
            getFlowOfObjects(
                dbRef.child(rootPath.path).orderByChild("startDateTime")
                    .startAt(System.currentTimeMillis().toDouble()),
                eventImplType
            )
        } else {
            getFlowOfObjects(
                dbRef.child(rootPath.path).orderByChild("endDateTime")
                    .startAt(System.currentTimeMillis().toDouble()),
                eventImplType
            )
        }
    }

    override suspend fun <T : Event<T>> getEvent(
        eventId: String,
        rootPath: RootPath,
        eventImplType: Class<T>
    ): T {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkRootPathMatchEventClassImpl(rootPath, eventImplType)
        return getObjFromDb(
            dbRef.child(rootPath.path).child(eventId),
            eventImplType,
            "Error while getting event $eventId from the database"
        )

    }

    override suspend fun <T : Event<T>> removeEvent(event: T, userId: String) {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(!event.id.isNullOrBlank(), "The event id cannot be blank or null")
        checkArgument(
            event.creator == userId,
            "The user that modify the comment must be its author"
        )

        removeObjFromDb(
            dbRef.child(event.rootPath.path).child(event.id!!),
            "Error while removing the event"
        )
    }
}
