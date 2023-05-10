package com.github.sdp_begreen.begreen.firebase.eventServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.removeObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.Contest
import com.github.sdp_begreen.begreen.models.Event
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent.inject
import java.util.Calendar

object EventServiceImpl : EventService {

    private val dbRefs by inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference

    override suspend fun <T : Event<T>> createEvent(event: T): T {
        checkArgument(!event.creator.isNullOrBlank(), "The creator cannot be blank or null")
        checkArgument(event.startDateTime != null, "The starting time cannot be null")
        val eventReference = dbRef.child(event.rootPath.path)
        return eventReference.push().key?.let {
            val metingWithId = event.copy(it)
            setObjToDb(eventReference.child(it), metingWithId, "Error while creating the event")
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
        checkRootPathMatchClass(rootPath, eventImplType)
        return getFlowOfObjects(
            dbRef.child(rootPath.path).orderByChild("startDateTime")
                .startAt(Calendar.getInstance().timeInMillis.toDouble()),
            eventImplType
        )
    }


    override suspend fun <T : Event<T>> getEvent(
        eventId: String,
        rootPath: RootPath,
        eventImplType: Class<T>
    ): T {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkRootPathMatchClass(rootPath, eventImplType)
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

    /**
     * Helper function to ensure that the root path correspond to the expected object return type
     */
    private fun <T : Event<T>> checkRootPathMatchClass(rootPath: RootPath, clazz: Class<T>) =
        when (rootPath) {
            RootPath.MEETINGS -> checkArgument(
                clazz.isAssignableFrom(Meeting::class.java),
                "The root path is of type ${RootPath.MEETINGS.name} but the expected object type is ${clazz.simpleName}"
            )

            RootPath.CONTESTS -> checkArgument(
                clazz.isAssignableFrom(Contest::class.java),
                "The root path is of type ${RootPath.MEETINGS.name} but the expected object type is ${clazz.simpleName}"
            )
        }
}
