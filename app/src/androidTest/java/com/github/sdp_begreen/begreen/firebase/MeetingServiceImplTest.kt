package com.github.sdp_begreen.begreen.firebase

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.models.Comment
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Test
import java.util.Calendar

class MeetingServiceImplTest {

    companion object {
        @get:ClassRule @JvmStatic val firebaseEmulatorRule = FirebaseEmulatorRule()
        val newMeeting = Meeting(
            null,
            "1",
            "Test Meeting",
            "In this meeting we will collect some waste",
            listOf(
                Comment(
                    "1",
                    "2",
                    Calendar.getInstance().apply {
                        set(2023, 4, 12, 13, 14, 0)
                    }.timeInMillis,
                    Calendar.getInstance().apply {
                        set(2023,4, 15, 18, 25, 0)
                    }.timeInMillis,
                    "This is a comment"
                )
            ),
            Calendar.getInstance().apply {
                set(2023, 12, 2, 12, 0, 0)
            }.timeInMillis,
            Calendar.getInstance().apply {
                set(2023, 12, 2, 14, 15, 0)
            }.timeInMillis,
            CustomLatLng(46.518867, 6.561845),
            CustomLatLng(46.518950, 6.568080),
            listOf(CustomLatLng(46.518178, 6.565507)),
            listOf("2", "3", "4"),
            listOf(PhotoMetadata("12", "coke can photo")),
            null
        )

    }

    // Cannot be in the companion object to use emulator.
    private val meetingService = MeetingServiceImpl()

    @Test
    fun addMeetingCorrectlyCreateMeetingInDB() {
        runBlocking {
            val addedMeeting = meetingService.createMeeting(newMeeting)
            val initMeetingWithId = newMeeting.copy(meetingId = addedMeeting?.meetingId)
            assertThat(addedMeeting, `is`(initMeetingWithId))

            val getAddedMeeting = meetingService.getMeeting(initMeetingWithId.meetingId!!)
            assertThat(getAddedMeeting, `is`(initMeetingWithId))
        }
    }

    @Test
    fun getNonExistingMeeting() {
        runBlocking {
            val exception = assertThrows(MeetingServiceException::class.java) {
                runBlocking {
                    meetingService.getMeeting("not existing")
                }
            }

            assertThat(exception.message, `is`("Data not found, or could not be parsed"))
        }
    }
}