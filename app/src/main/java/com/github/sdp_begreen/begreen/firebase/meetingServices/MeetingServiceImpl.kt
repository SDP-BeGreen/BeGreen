package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.removeObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.models.meetings.Meeting
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent.inject
import java.util.Calendar

object MeetingServiceImpl : MeetingService {

    private val dbRefs by inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private const val MEETING_PATH = "meeting"

    override suspend fun createMeeting(meeting: Meeting): Meeting {
        checkArgument(!meeting.creator.isNullOrBlank(), "The creator cannot be blank or null")
        checkArgument(meeting.startDateTime != null, "The starting time cannot be null")
        val meetingReference = dbRef.child(MEETING_PATH)
        return meetingReference.push().key?.let {
            val metingWithId = meeting.copy(meetingId = it)
            setObjToDb(meetingReference.child(it), metingWithId, "Error while creating the meeting")
        } ?: throw MeetingServiceException("Error while generating new key for meeting entry")
    }

    override suspend fun modifyMeeting(meeting: Meeting, userId: String): Meeting {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(
            meeting.creator == userId,
            "The user that modify the meeting must be its creator"
        )
        checkArgument(
            !meeting.meetingId.isNullOrBlank(),
            "The meeting to modify cannot have a blank or null meetingId"
        )
        return setObjToDb(
            dbRef.child(MEETING_PATH).child(meeting.meetingId!!), meeting,
            "Error while modifying the meeting"
        )
    }

    override suspend fun getAllMeetings(): Flow<List<Meeting>> = getFlowOfObjects(
        dbRef.child(MEETING_PATH).orderByChild("startDateTime")
            .startAt(Calendar.getInstance().timeInMillis.toDouble()),
        Meeting::class.java
    )


    override suspend fun getMeeting(meetingId: String): Meeting {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        return getObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId),
            Meeting::class.java,
            "Error while getting meeting $meetingId from the database"
        )

    }

    override suspend fun removeMeeting(meeting: Meeting, userId: String) {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(!meeting.meetingId.isNullOrBlank(), "The meeting id cannot be blank or null")
        checkArgument(
            meeting.creator == userId,
            "The user that modify the comment must be its author"
        )

        removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meeting.meetingId!!),
            "Error while removing the meeting"
        )
    }
}
