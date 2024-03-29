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
import org.koin.java.KoinJavaComponent.inject

object EventServiceImpl : EventService {

    private const val START_DATE_TIME = "startDateTime"
    private const val END_DATE_TIME = "endDateTime"

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

    override suspend fun <T : Event<T>> getAllEvents(
        rootPath: RootPath,
        eventImplType: Class<T>
    ): Flow<List<T>> {
        checkRootPathMatchEventClassImpl(rootPath, eventImplType)
        val orderBy = when (rootPath) {
            RootPath.MEETINGS -> START_DATE_TIME
            RootPath.CONTESTS -> END_DATE_TIME
        }
        return getFlowOfObjects(
            dbRef.child(rootPath.path).orderByChild(orderBy)
                .startAt(System.currentTimeMillis().toDouble()),
            eventImplType
        )
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
