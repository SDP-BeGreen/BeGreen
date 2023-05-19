package com.github.sdp_begreen.begreen.firebase.eventServices

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.matchers.ContainsWithSameOrder.Companion.inWithOrder
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.`in`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * The event service will only be tested using [Meeting]s, but it would work the same
 * with [Contest]
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class EventServiceImplTest {

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
                id = "-NU2KtnI6nKuswFWakns",
                title = "Modified title",
                description = "The description also changed"
            )

        private val meetingInDb = Meeting(
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
                EventServiceImpl.createEvent(newMeeting.copy(creator = " "))
            }
        }

        assertThat(exception.message, `is`(equalTo("The creator cannot be blank or null")))
    }

    @Test
    fun createMeetingNullCreatorShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.createEvent(newMeeting.copy(creator = null))
            }
        }

        assertThat(exception.message, `is`(equalTo("The creator cannot be blank or null")))
    }

    @Test
    fun createMeetingCorrectlyCreateMeetingInDB() {
        runTest {
            val addedMeeting = EventServiceImpl.createEvent(newMeeting)
            val initMeetingWithId = newMeeting.copy(id = addedMeeting.id)
            assertThat(addedMeeting, `is`(equalTo(initMeetingWithId)))

            val getAddedMeeting = EventServiceImpl.getEvent(
                initMeetingWithId.id!!,
                RootPath.MEETINGS,
                Meeting::class.java
            )
            assertThat(getAddedMeeting, `is`(equalTo(initMeetingWithId)))
        }
    }

    @Test
    fun createContestCorrectlyCreateContestInDB() {
        val newContest = Contest(
            null, "contest creator", "This is a contest", "speed run",
            Calendar.getInstance().apply {
                set(2024, 11, 8, 13, 0, 0)
            }.timeInMillis,
        )
        runTest {
            val addContest = EventServiceImpl.createEvent(newContest)
            val newContestWithId = newContest.copy(id = addContest.id)
            assertThat(addContest, `is`(equalTo(newContestWithId)))

            val getAddedContest = EventServiceImpl.getEvent(
                newContestWithId.id!!,
                RootPath.CONTESTS,
                Contest::class.java
            )
            assertThat(getAddedContest, `is`(equalTo(newContestWithId)))
        }
    }

    @Test
    fun createMeetingNullStartDateShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.createEvent(newMeeting.copy(startDateTime = null))
            }
        }

        assertThat(exception.message, `is`(equalTo("The starting time cannot be null")))
    }

    @Test
    fun modifyMeetingBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.modifyEvent(modifiedMeeting, " ")
            }
        }

        assertThat(exception.message, `is`(equalTo("The user id cannot be blank")))
    }

    @Test
    fun modifyMeetingDifferentUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.modifyEvent(modifiedMeeting, "2")
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that modify the event must be its creator"))
        )
    }

    @Test
    fun modifyMeetingBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.modifyEvent(
                    modifiedMeeting.copy(id = " "),
                    newMeeting.creator!!
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The event to modify cannot have a blank or null id"))
        )
    }

    @Test
    fun modifyMeetingNullMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.modifyEvent(
                    modifiedMeeting.copy(id = null),
                    newMeeting.creator!!
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The event to modify cannot have a blank or null id"))
        )
    }

    @Test
    fun modifyMeetingCorrectlyModifyMeetingInDB() {
        runTest {
            EventServiceImpl.modifyEvent(modifiedMeeting, newMeeting.creator!!)

            val getModifiedMeeting = EventServiceImpl.getEvent(
                modifiedMeeting.id!!,
                RootPath.MEETINGS,
                Meeting::class.java
            )
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
            val list = EventServiceImpl.getAllEvents(RootPath.MEETINGS, Meeting::class.java).first()
            // As its hard to know exactly how many meetings are going to be in the database when
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
                EventServiceImpl.getAllEvents(RootPath.MEETINGS, Meeting::class.java).collect {
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
            EventServiceImpl.modifyEvent(modifiedMeeting1, meeting1.creator!!)
            assertThat(
                listOf(modifiedMeeting1, meeting2, meeting3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify second elem
            val modifiedMeeting2 = meeting2.copy(
                title = "Modified second meeting",
                description = "Modified description second meeting"
            )
            EventServiceImpl.modifyEvent(modifiedMeeting2, meeting2.creator!!)
            assertThat(
                listOf(modifiedMeeting1, modifiedMeeting2, meeting3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify third elem
            val modifiedMeeting3 = meeting3.copy(
                title = "Modified third meeting",
                description = "Modified description third meeting"
            )
            EventServiceImpl.modifyEvent(modifiedMeeting3, meeting3.creator!!)
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
            val newMeeting1 = EventServiceImpl.createEvent(meeting1)
            val newMeeting2 = EventServiceImpl.createEvent(meeting2)
            val newMeeting3 = EventServiceImpl.createEvent(meeting3)

            val meetings =
                EventServiceImpl.getAllEvents(RootPath.MEETINGS, Meeting::class.java).first()

            // check that those two new meeting are in the correct order
            assertThat(listOf(newMeeting1, newMeeting2), `is`(inWithOrder(meetings)))

            // check that the third meting is not returned as it has already happened
            assertThat(newMeeting3, `is`(not(`in`(meetings))))
        }
    }

    @Test
    fun getAllEventsMeetingsRootPathContestThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.getAllEvents(RootPath.MEETINGS, Contest::class.java)
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The root path is of type ${RootPath.MEETINGS.name} but the expected event type is Contest"))
        )
    }

    @Test
    fun getAllEventsContestRootPathMeetingShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.getAllEvents(RootPath.CONTESTS, Meeting::class.java)
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The root path is of type ${RootPath.CONTESTS.name} but the expected event type is Meeting"))
        )
    }

    @Test
    fun getEventMetingRootPathContestShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.getEvent(
                    meetingInDb.id!!,
                    RootPath.MEETINGS,
                    Contest::class.java
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The root path is of type ${RootPath.MEETINGS.name} but the expected event type is Contest"))
        )
    }

    @Test
    fun getEventContestRootPathMeetingShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.getEvent(
                    meetingInDb.id!!,
                    RootPath.CONTESTS,
                    Meeting::class.java
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The root path is of type ${RootPath.CONTESTS.name} but the expected event type is Meeting"))
        )
    }

    @Test
    fun getNonExistingMeetingShouldThrowMeetingServiceException() {
        val exception = assertThrows(EventServiceException::class.java) {
            runTest {
                EventServiceImpl.getEvent("not existing", RootPath.MEETINGS, Meeting::class.java)
            }
        }

        assertThat(exception.message, `is`(equalTo("Data not found, or could not be parsed")))
    }

    @Test
    fun getMeetingBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.getEvent("  ", RootPath.MEETINGS, Meeting::class.java)
            }
        }

        assertThat(exception.message, `is`("The event id cannot be blank"))
    }

    @Test
    fun getMeetingShouldRetrievedCorrectMeetingFromDB() {
        runTest {
            val retrievedMeeting = EventServiceImpl.getEvent(
                meetingInDb.id!!,
                RootPath.MEETINGS,
                Meeting::class.java
            )
            assertThat(retrievedMeeting, `is`(equalTo(meetingInDb)))
        }
    }

    @Test
    fun removeMeetingBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.removeEvent(
                    meetingInDb,
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
                EventServiceImpl.removeEvent(
                    meetingInDb.copy(id = " "),
                    meetingInDb.creator!!
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The event id cannot be blank or null")))
    }

    @Test
    fun removeMeetingNullMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.removeEvent(
                    meetingInDb.copy(id = null),
                    meetingInDb.creator!!
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The event id cannot be blank or null")))
    }

    @Test
    fun removeMeetingDifferentUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                EventServiceImpl.removeEvent(
                    meetingInDb.copy(creator = "different"),
                    meetingInDb.creator!!
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
            val retrievedMeeting =
                EventServiceImpl.getEvent(meeting.id!!, RootPath.MEETINGS, Meeting::class.java)
            assertThat(retrievedMeeting, `is`(equalTo(meeting)))

            //remove it
            withContext(Dispatchers.Default) {
                EventServiceImpl.removeEvent(
                    meeting,
                    meeting.creator!!
                )
            }

            // assert meeting removed
            val exception = assertThrows(EventServiceException::class.java) {
                runBlocking {
                    EventServiceImpl.getEvent(meeting.id!!, RootPath.MEETINGS, Meeting::class.java)
                }
            }
            assertThat(exception.message, `is`(equalTo("Data not found, or could not be parsed")))
        }
    }

    @Test
    fun getAllContestsShouldContainExactlyAllUnfinishedContestsAndInTheRightOrder() {
        // not finished event
        val contest1 = Contest(
            null,
            "creator1",
            "contest 1",
            "some description for contest 1",
            1,
            Calendar.getInstance().timeInMillis.plus(999999)
        )
        // not finished event
        val contest2 = Contest(
            null,
            "creator2",
            "contest 2",
            "some description for contest 2",
            1,
            Calendar.getInstance().timeInMillis.plus(777777)
        )
        // not finished event
        val contest3 = Contest(
            null,
            "creator3",
            "contest 3",
            "some description for contest 3",
            1,
            Calendar.getInstance().timeInMillis.plus(888888)
        )
        // finshed event
        val contest4 = Contest(
            null,
            "creator4",
            "contest 4",
            "some description for contest 4",
            1,
            Calendar.getInstance().timeInMillis.minus(999999)
        )
        runTest {
            val newContest1 = EventServiceImpl.createEvent(contest1)
            val newContest2 = EventServiceImpl.createEvent(contest2)
            val newContest3 = EventServiceImpl.createEvent(contest3)
            val newContest4 = EventServiceImpl.createEvent(contest4)


            val contests =
                EventServiceImpl.getAllEvents(RootPath.CONTESTS, Contest::class.java).first()

            // check that the three new contests are in the correct order
            assertThat(listOf(newContest2, newContest3, newContest1), `is`(inWithOrder(contests)))

            // check that the fourth contest is not returned as it has already ended
            assertThat(newContest4, `is`(not(`in`(contests))))
        }
    }

}