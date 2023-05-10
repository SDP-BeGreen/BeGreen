package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent

object EventParticipantServiceImpl : EventParticipantService {

    private val dbRefs by KoinJavaComponent.inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private val MEETINGS_PATH = RootPath.MEETINGS.path
    private const val PARTICIPANTS_PATH = "participants"

    override suspend fun <T: EventParticipant> addParticipant(eventId: String, participant: T): T {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkArgument(!participant.id.isNullOrBlank(), "The participant id cannot be blank")

        return setObjToDb(
            dbRef.child(MEETINGS_PATH).child(eventId).child(PARTICIPANTS_PATH)
                .child(participant.id!!),
            participant,
            "Error while adding a participant"
        )
    }

    override suspend fun <T: EventParticipant> getAllParticipants(eventId: String, clazz: Class<T>): Flow<List<T>> {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        return getFlowOfObjects(
            dbRef.child(MEETINGS_PATH).child(eventId).child(PARTICIPANTS_PATH),
            clazz
        )
    }

    override suspend fun removeParticipant(eventId: String, participantId: String) {
        checkArgument(eventId.isNotBlank(), "The event id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")

        FirebaseUtils.removeObjFromDb(
            dbRef.child(MEETINGS_PATH).child(eventId)
                .child(PARTICIPANTS_PATH)
                .child(participantId),
            "Error while removing the participant"
        )
    }
}
