package com.github.sdp_begreen.begreen.firebase.meetingServices

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.matchers.ContainsWithSameOrder.Companion.inWithOrder
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.everyItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.`in`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class MeetingServiceImplTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()

        private val newMeeting = Meeting(
            null,
            "1",
            "Test Meeting",
            "In this meeting we will collect some waste",
            Calendar.getInstance().apply {
                set(2023, 11, 2, 12, 0, 0)
            }.timeInMillis,
            Calendar.getInstance().apply {
                set(2023, 11, 2, 14, 15, 0)
            }.timeInMillis,
            CustomLatLng(46.518867, 6.561845),
            CustomLatLng(46.518950, 6.568080),
            listOf(CustomLatLng(46.518178, 6.565507))
        )

        // This meeting is already pre-stored in the database, but all its field are the same as
        // `newMeeting`
        private val modifiedMeeting =
            newMeeting.copy(
                meetingId = "-NU2KtnI6nKuswFWakns",
                title = "Modified title",
                description = "The description also changed"
            )

        private val meetingToRemove = Meeting(
            "-NU5e_cOMcBd1A_fMx5H",
            "123456789",
            "single meeting title",
            "description of single meeting title"
        )
    }

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun createMeetingBlankCreatorShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.createMeeting(newMeeting.copy(creator = " "))
            }
        }

        assertThat(exception.message, `is`(equalTo("The creator cannot be blank or null")))
    }

    @Test
    fun createMeetingNullCreatorShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.createMeeting(newMeeting.copy(creator = null))
            }
        }

        assertThat(exception.message, `is`(equalTo("The creator cannot be blank or null")))
    }

    @Test
    fun createMeetingCorrectlyCreateMeetingInDB() {
        runTest {
            val addedMeeting = MeetingServiceImpl.createMeeting(newMeeting)
            val initMeetingWithId = newMeeting.copy(meetingId = addedMeeting.meetingId)
            assertThat(addedMeeting, `is`(equalTo(initMeetingWithId)))

            val getAddedMeeting = MeetingServiceImpl.getMeeting(initMeetingWithId.meetingId!!)
            assertThat(getAddedMeeting, `is`(equalTo(initMeetingWithId)))
        }
    }

    @Test
    fun createMeetingNullStartDateShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.createMeeting(newMeeting.copy(startDateTime = null))
            }
        }

        assertThat(exception.message, `is`(equalTo("The starting time cannot be null")))
    }

    @Test
    fun modifyMeetingBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyMeeting(modifiedMeeting, " ")
            }
        }

        assertThat(exception.message, `is`(equalTo("The user id cannot be blank")))
    }

    @Test
    fun modifyMeetingDifferentUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyMeeting(modifiedMeeting, "2")
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that modify the meeting must be its creator"))
        )
    }

    @Test
    fun modifyMeetingBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyMeeting(
                    modifiedMeeting.copy(meetingId = " "),
                    newMeeting.creator!!
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting to modify cannot have a blank or null meetingId"))
        )
    }

    @Test
    fun modifyMeetingNullMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyMeeting(
                    modifiedMeeting.copy(meetingId = null),
                    newMeeting.creator!!
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting to modify cannot have a blank or null meetingId"))
        )
    }

    @Test
    fun modifyMeetingCorrectlyModifyMeetingInDB() {
        runTest {
            MeetingServiceImpl.modifyMeeting(modifiedMeeting, newMeeting.creator!!)

            val getModifiedMeeting = MeetingServiceImpl.getMeeting(modifiedMeeting.meetingId!!)
            assertThat(getModifiedMeeting, `is`(equalTo(modifiedMeeting)))
        }
    }

    @Test
    fun getAllMeetingReturnCorrectListOfMeetingFromDB() {
        // set date really fare in future so test won't break before 2050.
        val meetings = listOf(
            Meeting(
                "-NU2QlTB5mflQYthYViI", "123", "Meeting1",
                "This is meeting 1", startDateTime = 2524612200000
            ),
            Meeting(
                "-NU2QlsU3bAsML6gJCeb", "234", "Meeting2",
                "This is meeting 2", startDateTime = 2524612200000
            ),
            Meeting(
                "-NU2QlsbKgy9yLizwifN", "345", "Meeting3",
                "This is meeting 3", startDateTime = 2524612200000
            ),
            Meeting(
                "-NU2Qlso32EooKdvCzUW", "456", "Meeting4",
                "This is meeting 4", startDateTime = 2524612200000
            )
        )

        runTest {
            val list = MeetingServiceImpl.getAllMeetings().first()
            // As its hard to now exactly how many meetings are going to be in the database when
            // we run this test, as test are executed in any order, some new meetings may have
            // been added in between, we check that at least all the meetings from the list `meetings`
            // are in the retrieved list
            assertThat(meetings, everyItem(`is`(`in`(list))))
        }
    }

    @Test
    fun getAllMeetingReturnCorrectModifiedListUponModification() {
        val meeting1 =
            Meeting(
                "-NU3MWvfdMASrqPEDSUF", "1111", "first meeting",
                "Description first meeting", startDateTime = 2524612200000
            )
        val meeting2 =
            Meeting(
                "-NU3MXHX2qcQUZ1qM5Bf", "2222", "second meeting",
                "Description second meeting", startDateTime = 2524612200000
            )
        val meeting3 =
            Meeting(
                "-NU3MXHeBN3Nedwl4Qc_", "3333", "third meeting",
                "Description third meeting", startDateTime = 2524612200000
            )

        runTest() {
            val channel = Channel<List<Meeting>>(1)
            backgroundScope.launch {
                MeetingServiceImpl.getAllMeetings().collect {
                    channel.send(it)
                }
            }

            // check initial meetings
            assertThat(
                listOf(meeting1, meeting2, meeting3),
                everyItem(`is`(`in`(channel.receive())))
            )
            // modify first elem
            val modifiedMeeting1 = meeting1.copy(
                title = "Modified first meeting",
                description = "Modified description first meeting"
            )
            MeetingServiceImpl.modifyMeeting(modifiedMeeting1, meeting1.creator!!)
            assertThat(
                listOf(modifiedMeeting1, meeting2, meeting3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify second elem
            val modifiedMeeting2 = meeting2.copy(
                title = "Modified second meeting",
                description = "Modified description second meeting"
            )
            MeetingServiceImpl.modifyMeeting(modifiedMeeting2, meeting2.creator!!)
            assertThat(
                listOf(modifiedMeeting1, modifiedMeeting2, meeting3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify third elem
            val modifiedMeeting3 = meeting3.copy(
                title = "Modified third meeting",
                description = "Modified description third meeting"
            )
            MeetingServiceImpl.modifyMeeting(modifiedMeeting3, meeting3.creator!!)
            assertThat(
                listOf(modifiedMeeting1, modifiedMeeting2, modifiedMeeting3),
                everyItem(`is`(`in`(channel.receive())))
            )
        }
    }

    @Test
    fun getAllMeetingShouldBeOrderedByTheirStartingDate() {
        val meeting1 = Meeting(
            null,
            "creator1",
            "Collect meeting 1",
            "some description for meeting 1",
            Calendar.getInstance().timeInMillis.plus(1000)
        )
        val meeting2 = Meeting(
            null,
            "creator2",
            "Collect meeting 2",
            "some description for meeting 2",
            Calendar.getInstance().timeInMillis.plus(2000)
        )
        val meeting3 = Meeting(
            null,
            "creator3",
            "Collect meeting 3",
            "some description for meeting 3",
            Calendar.getInstance().timeInMillis.minus(1000)
        )
        runTest {
            val newMeeting1 = MeetingServiceImpl.createMeeting(meeting1)
            val newMeeting2 = MeetingServiceImpl.createMeeting(meeting2)
            val newMeeting3 = MeetingServiceImpl.createMeeting(meeting3)

            val meetings = MeetingServiceImpl.getAllMeetings().first()

            // check that those two new meeting are in the correct order
            assertThat(listOf(newMeeting1, newMeeting2), `is`(inWithOrder(meetings)))

            // check that the third meting is not returned as it has already happened
            assertThat(newMeeting3, `is`(not(`in`(meetings))))
        }
    }

    @Test
    fun getNonExistingMeetingShouldThrowMeetingServiceException() {
        val exception = assertThrows(MeetingServiceException::class.java) {
            runTest {
                MeetingServiceImpl.getMeeting("not existing")
            }
        }

        assertThat(exception.message, `is`(equalTo("Data not found, or could not be parsed")))
    }

    @Test
    fun getMeetingBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.getMeeting("  ")
            }
        }

        assertThat(exception.message, `is`("The meeting id cannot be blank"))
    }

    @Test
    fun getMeetingShouldRetrievedCorrectMeetingFromDB() {
        runTest {
            val retrievedMeeting = MeetingServiceImpl.getMeeting(meetingToRemove.meetingId!!)
            assertThat(retrievedMeeting, `is`(equalTo(meetingToRemove)))
        }
    }

    @Test
    fun removeMeetingBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeeting(
                    meetingToRemove,
                    " "
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The user id cannot be blank")))
    }

    @Test
    fun removeMeetingBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeeting(
                    meetingToRemove.copy(meetingId = " "),
                    meetingToRemove.creator!!
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank or null")))
    }

    @Test
    fun removeMeetingNullMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeeting(
                    meetingToRemove.copy(meetingId = null),
                    meetingToRemove.creator!!
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank or null")))
    }

    @Test
    fun removeMeetingDifferentUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeeting(
                    meetingToRemove.copy(creator = "different"),
                    meetingToRemove.creator!!
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that modify the comment must be its author"))
        )
    }

    @Test
    fun removeMeetingShouldCorrectlyRemoveMeetingFromDB() {
        val meeting = Meeting(
            "-NU5gPRPLhM4suC8siHQ",
            "565656",
            "meeting to be removed",
            "This meeting is simply a meeting that will be removed"
        )

        runTest {
            // assert first that meeting is present
            val retrievedMeeting = MeetingServiceImpl.getMeeting(meeting.meetingId!!)
            assertThat(retrievedMeeting, `is`(equalTo(meeting)))

            //remove it
            withContext(Dispatchers.Default) {
                MeetingServiceImpl.removeMeeting(
                    meeting,
                    meeting.creator!!
                )
            }

            // assert meeting removed
            val exception = assertThrows(MeetingServiceException::class.java) {
                runBlocking {
                    MeetingServiceImpl.getMeeting(meeting.meetingId!!)
                }
            }
            assertThat(exception.message, `is`(equalTo("Data not found, or could not be parsed")))


        }
    }
}