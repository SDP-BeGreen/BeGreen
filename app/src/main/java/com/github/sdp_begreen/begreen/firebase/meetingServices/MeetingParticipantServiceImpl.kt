package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent

object MeetingParticipantServiceImpl : MeetingParticipantService {

    private val dbRefs by KoinJavaComponent.inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private const val MEETING_PATH = "meeting"
    private const val PARTICIPANTS_PATH = "participants"

    override suspend fun addParticipant(meetingId: String, participantId: String): String {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")

        return setObjToDb(
            dbRef.child(MEETING_PATH).child(meetingId).child(
                PARTICIPANTS_PATH
            )
                .child(participantId),
            participantId,
            "Error while adding a participant"
        )
    }

    override suspend fun getAllParticipants(meetingId: String): Flow<List<String>> {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        return getFlowOfObjects(
            dbRef.child(MEETING_PATH).child(meetingId).child(
                PARTICIPANTS_PATH
            ),
            String::class.java
        )
    }

    override suspend fun removeParticipant(meetingId: String, participantId: String) {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(participantId.isNotBlank(), "The participant id cannot be blank")

        FirebaseUtils.removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId)
                .child(PARTICIPANTS_PATH)
                .child(participantId),
            "Error while removing the meeting"
        )
    }
}
