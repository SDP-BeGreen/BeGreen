package com.github.sdp_begreen.begreen.firebase.eventServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.removeObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.utils.checkArgument
import com.github.sdp_begreen.begreen.utils.checkRootPathMatchParticipantClassImpl
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent

object EventParticipantServiceImpl : EventParticipantService {

    private val dbRefs by KoinJavaComponent.inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private const val PARTICIPANTS_PATH = "participants"
    override suspend fun <T : EventParticipant> getParticipant(
        rootPath: RootPath,
        eventId: String,
        participantId: String,
        clazz: Class<T>
    ): T {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")
        checkRootPathMatchParticipantClassImpl(rootPath, clazz)
        return getObjFromDb(
            dbRef.child(rootPath.path).child(eventId).child(PARTICIPANTS_PATH).child(participantId),
            clazz,
            "Error while getting participant $participantId from the database"
        )
    }

    override suspend fun <T : EventParticipant> getAllParticipants(
        rootPath: RootPath,
        eventId: String,
        clazz: Class<T>
    ): Flow<List<T>> {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkRootPathMatchParticipantClassImpl(rootPath, clazz)
        return getFlowOfObjects(
            dbRef.child(rootPath.path).child(eventId).child(PARTICIPANTS_PATH),
            clazz
        )
    }

    override suspend fun <T : EventParticipant> addParticipant(
        rootPath: RootPath,
        eventId: String,
        participant: T
    ): T {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkArgument(!participant.id.isNullOrBlank(), "The participant id cannot be blank")

        // Add the participant the event's list of participants
        return setObjToDb(
            dbRef.child(rootPath.path).child(eventId).child(PARTICIPANTS_PATH)
                .child(participant.id!!),
            participant,
            "Error while adding a participant"
        )
    }

    override suspend fun removeParticipant(
        rootPath: RootPath,
        eventId: String,
        participantId: String
    ) {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")

        // Remove the participant from the event's list of participants
        removeObjFromDb(
            dbRef.child(rootPath.path).child(eventId)
                .child(PARTICIPANTS_PATH)
                .child(participantId),
            "Error while removing the participant"
        )
    }
}
