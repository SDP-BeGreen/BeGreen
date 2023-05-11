package com.github.sdp_begreen.begreen.firebase.eventServices

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.models.event.MeetingParticipant
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.`in`
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class EventParticipantServiceTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()

        private val meetingWithParticipants = Meeting(
            "-NU6zL2hzerexk1M3xS-",
            "78787878",
            "Test participant meeting",
            "Meeting acting as a container to test participants"
        )
    }

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun addParticipantBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventParticipantServiceImpl.addParticipant(
                    RootPath.MEETINGS,
                    " ",
                    MeetingParticipant("hhhh")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The event id cannot be blank"))
        )
    }

    @Test
    fun addParticipantBlankParticipantShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventParticipantServiceImpl.addParticipant(
                    RootPath.MEETINGS,
                    meetingWithParticipants.id!!,
                    MeetingParticipant(" ")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The participant id cannot be blank"))
        )
    }

    @Test
    fun addParticipantNullParticipantShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventParticipantServiceImpl.addParticipant(
                    RootPath.MEETINGS,
                    meetingWithParticipants.id!!,
                    MeetingParticipant()
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The participant id cannot be blank"))
        )
    }

    @Test
    fun addParticipantCorrectlyAddParticipantToMeetingInDB() {
        runTest {
            assertThat(
                EventParticipantServiceImpl.addParticipant(
                    RootPath.MEETINGS,
                    meetingWithParticipants.id!!,
                    MeetingParticipant("abcd")
                ),
                `is`(equalTo(MeetingParticipant("abcd")))
            )
        }
    }

    @Test
    fun getAllParticipantBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventParticipantServiceImpl.getAllParticipants(
                    RootPath.MEETINGS,
                    " ",
                    MeetingParticipant::class.java
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The event id cannot be blank"))
        )
    }

    @Test
    fun removeParticipantBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventParticipantServiceImpl.removeParticipant(
                    RootPath.MEETINGS,
                    " ",
                    "aaaaaa"
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The event id cannot be blank"))
        )
    }

    @Test
    fun removeParticipantBlankParticipantIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventParticipantServiceImpl.removeParticipant(
                    RootPath.MEETINGS,
                    meetingWithParticipants.id!!,
                    " "
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The participant id cannot be blank"))
        )
    }

    @Test
    fun getAllParticipantsReturnCorrectModifiedListUponModification() {
        val participant1 = MeetingParticipant("participant1")
        val participant2 = MeetingParticipant("participant2")
        val participant3 = MeetingParticipant("participant3")
        val participant4 = MeetingParticipant("participant4")

        runTest {
            val channel = Channel<List<MeetingParticipant>>(1)
            backgroundScope.launch {
                EventParticipantServiceImpl.getAllParticipants(
                    RootPath.MEETINGS,
                    meetingWithParticipants.id!!,
                    MeetingParticipant::class.java
                )
                    .collect {
                        channel.send(it)
                    }
            }

            // check initial meetings
            assertThat(
                listOf(participant1, participant2, participant3),
                everyItem(`is`(`in`(channel.receive())))
            )

            EventParticipantServiceImpl.removeParticipant(
                RootPath.MEETINGS,
                meetingWithParticipants.id!!,
                participant2.id!!
            )
            assertThat(
                listOf(participant1, participant3),
                everyItem(`is`(`in`(channel.receive())))
            )

            EventParticipantServiceImpl.addParticipant(
                RootPath.MEETINGS,
                meetingWithParticipants.id!!,
                participant4
            )
            assertThat(
                listOf(participant1, participant3, participant4),
                everyItem(`is`(`in`(channel.receive())))
            )

        }
    }
}