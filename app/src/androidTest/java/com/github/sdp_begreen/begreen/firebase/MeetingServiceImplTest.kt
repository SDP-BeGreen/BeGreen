package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.matchers.EqualsToBitmap.Companion.equalsBitmap
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.meetings.Comment
import com.github.sdp_begreen.begreen.models.meetings.Meeting
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.everyItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.`in`
import org.hamcrest.Matchers.stringContainsInOrder
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class MeetingServiceImplTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()

        val newMeeting = Meeting(
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
        val modifiedMeeting =
            newMeeting.copy(
                meetingId = "-NU2KtnI6nKuswFWakns",
                title = "Modified title",
                description = "The description also changed"
            )

        val meetingToRemove = Meeting(
            "-NU5e_cOMcBd1A_fMx5H",
            "123456789",
            "single meeting title",
            "description of single meeting title"
        )

        val meetingWithComments = Meeting(
            "-NU5gPoTl4hxHCywcdWs",
            "89898989",
            "Test comment meeting",
            "Meeting acting as a container to test comments"
        )

        val modifiedComment =
            Comment("-NU5tkh5YDVxESHe3WC5", "1234512345", body = "The comment has been modified")

        val commentToRemove =
            Comment("-NUB11733L2AbvOUZmai", "abcdefg", body = "Comment to test remove throws")

        val meetingWithParticipants = Meeting(
            "-NU6zL2hzerexk1M3xS-",
            "78787878",
            "Test participant meeting",
            "Meeting acting as a container to test participants"
        )

        val meetingWithPhotos = Meeting(
            "-NU6zLARtig1QxHxK532",
            "67676767",
            "Test photo meeting",
            "Meeting acting as a container to test photos"
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
        val meetings = listOf(
            Meeting("-NU2QlTB5mflQYthYViI", "123", "Meeting1", "This is meeting 1"),
            Meeting("-NU2QlsU3bAsML6gJCeb", "234", "Meeting2", "This is meeting 2"),
            Meeting("-NU2QlsbKgy9yLizwifN", "345", "Meeting3", "This is meeting 3"),
            Meeting("-NU2Qlso32EooKdvCzUW", "456", "Meeting4", "This is meeting 4")
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
            Meeting("-NU3MWvfdMASrqPEDSUF", "1111", "first meeting", "Description first meeting")
        val meeting2 =
            Meeting("-NU3MXHX2qcQUZ1qM5Bf", "2222", "second meeting", "Description second meeting")
        val meeting3 =
            Meeting("-NU3MXHeBN3Nedwl4Qc_", "3333", "third meeting", "Description third meeting")

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

    @Test
    fun addParticipantBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addParticipant(" ", "hhhh")
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun addParticipantBlankParticipantShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addParticipant(meetingWithParticipants.meetingId!!, " ")
            }
        }

        assertThat(exception.message, `is`(equalTo("The participant id cannot be blank")))
    }

    @Test
    fun addParticipantCorrectlyAddParticipantToMeetingInDB() {
        runTest {
            assertThat(
                MeetingServiceImpl.addParticipant(meetingWithParticipants.meetingId!!, "abcd"),
                `is`(
                    equalTo("abcd")
                )
            )
        }
    }

    @Test
    fun getAllParticipantBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest { MeetingServiceImpl.getAllParticipants(" ") }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun removeParticipantBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest { MeetingServiceImpl.removeParticipant(" ", "aaaaaa") }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun removeParticipantBlankParticipantIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeParticipant(
                    meetingWithParticipants.meetingId!!,
                    " "
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The participant id cannot be blank")))
    }

    @Test
    fun getAllParticipantsReturnCorrectModifiedListUponModification() {
        val participant1 = "participant1"
        val participant2 = "participant2"
        val participant3 = "participant3"
        val participant4 = "participant4"

        runTest {
            val channel = Channel<List<String>>(1)
            backgroundScope.launch {
                MeetingServiceImpl.getAllParticipants(meetingWithParticipants.meetingId!!).collect {
                    channel.send(it)
                }
            }

            // check initial meetings
            assertThat(
                listOf(participant1, participant2, participant3),
                everyItem(`is`(`in`(channel.receive())))
            )

            MeetingServiceImpl.removeParticipant(meetingWithParticipants.meetingId!!, participant2)
            assertThat(
                listOf(participant1, participant3),
                everyItem(`is`(`in`(channel.receive())))
            )

            MeetingServiceImpl.addParticipant(meetingWithParticipants.meetingId!!, participant4)
            assertThat(
                listOf(participant1, participant3, participant4),
                everyItem(`is`(`in`(channel.receive())))
            )

        }
    }

    @Test
    fun addCommentBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addComment(" ", Comment())
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun addCommentBlankAuthorShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addComment(
                    meetingWithComments.meetingId!!,
                    Comment(author = " ")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The author of the comment cannot be blank or null"))
        )
    }

    @Test
    fun addCommentNullAuthorShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addComment(
                    meetingWithComments.meetingId!!,
                    Comment(author = null)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The author of the comment cannot be blank or null"))
        )
    }

    @Test
    fun addCommentCorrectlyAddCommentToMeetingInDB() {
        val comment = Comment(null, "121212", body = "This is a nice comment")

        runTest {
            val addedComment =
                MeetingServiceImpl.addComment(meetingWithComments.meetingId!!, comment)
            val commentWithId = comment.copy(commentId = addedComment.commentId)
            assertThat(addedComment, `is`(equalTo(commentWithId)))
        }
    }

    @Test
    fun modifyCommentBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyComment(
                    meetingWithComments.meetingId!!,
                    " ",
                    modifiedComment
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The user id cannot be blank")))
    }

    @Test
    fun modifyCommentBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyComment(" ", modifiedComment.author!!, modifiedComment)
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id for which to modify a comment cannot be blank"))
        )
    }

    @Test
    fun modifyCommentDifferentUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyComment(
                    meetingWithComments.meetingId!!,
                    "different",
                    modifiedComment
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that modify the comment must be its author"))
        )
    }

    @Test
    fun modifyCommentBlankCommentIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyComment(
                    meetingWithComments.meetingId!!,
                    modifiedComment.author!!,
                    modifiedComment.copy(commentId = " ")
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The comment to modify cannot have an empty or blank commentId"))
        )
    }

    @Test
    fun modifyCommentNullCommentIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.modifyComment(
                    meetingWithComments.meetingId!!,
                    modifiedComment.author!!,
                    modifiedComment.copy(commentId = null)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The comment to modify cannot have an empty or blank commentId"))
        )
    }

    @Test
    fun getAllCommentsBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.getAllComments(" ").collect()
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id should not be blank")))
    }


    @Test
    fun getAllCommentsForMeetingReturnCorrectListOfCommentsFromDB() {
        val comments = listOf(
            Comment("-NU6-oFySS4uNwdOEP6o", "1234", body = "The first comment"),
            Comment("-NU6-oGo8r5U3kgJEOw8", "2345", body = "The second comment"),
            Comment("-NU6-oGvGXDYuYnCR5L9", "3456", body = "The third comment")
        )

        runBlocking {
            val list = MeetingServiceImpl.getAllComments(meetingWithComments.meetingId!!).first()
            assertThat(comments, everyItem(`is`(`in`(list))))
        }
    }

    @Test
    fun getAllCommentsReturnCorrectModifiedListUponModification() {

        val comment1 =
            Comment("-NU60mSlnoG8MmV2tq1d", author = "aaaaaa", body = "First comment body")
        val comment2 =
            Comment("-NU60mqEZbzxN256eLjS", author = "bbbbbb", body = "Second comment body")
        val comment3 =
            Comment("-NU60mqMgnLCnQwMP2Xh", author = "cccccc", body = "Third comment body")

        runTest {
            val channel = Channel<List<Comment>>(1)
            backgroundScope.launch {
                MeetingServiceImpl.getAllComments(meetingWithComments.meetingId!!).collect {
                    channel.send(it)
                }
            }

            // check initial meetings
            assertThat(
                listOf(comment1, comment2, comment3),
                everyItem(`is`(`in`(channel.receive())))
            )
            // modify first elem
            val modifiedComment1 = comment1.copy(
                body = "Modified first comment body"
            )
            MeetingServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                modifiedComment1.author!!,
                modifiedComment1
            )
            assertThat(
                listOf(modifiedComment1, comment2, comment3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify second elem
            val modifiedComment2 = comment2.copy(
                body = "Modified second comment body"
            )
            MeetingServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                modifiedComment2.author!!,
                modifiedComment2
            )
            assertThat(
                listOf(modifiedComment1, modifiedComment2, comment3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify third elem
            val modifiedComment3 = comment3.copy(
                body = "Modified third comment body"
            )
            MeetingServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                modifiedComment3.author!!,
                modifiedComment3
            )
            assertThat(
                listOf(modifiedComment1, modifiedComment2, modifiedComment3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // remove a comment
            MeetingServiceImpl.removeComment(
                meetingWithComments.meetingId!!,
                modifiedComment2,
                modifiedComment2.author!!
            )
            val listComments = channel.receive()
            assertThat(
                listOf(modifiedComment1, modifiedComment3),
                everyItem(`is`(`in`(listComments)))
            )
            assertThat(
                modifiedComment2,
                `is`(not(`in`(listComments)))
            )
        }
    }

    @Test
    fun removeCommentBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeComment(" ", commentToRemove, commentToRemove.author!!)
            }
        }
        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun removeCommentBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeComment(
                    meetingWithComments.meetingId!!,
                    commentToRemove,
                    " "
                )
            }
        }
        assertThat(exception.message, `is`(equalTo("The user id cannot be blank")))
    }

    @Test
    fun removeCommentBlankCommentIdThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeComment(
                    meetingWithComments.meetingId!!, commentToRemove.copy(
                        commentId = " "
                    ), commentToRemove.author!!
                )
            }
        }
        assertThat(exception.message, `is`(equalTo("The comment id cannot be blank or null")))
    }

    @Test
    fun removeCommentNullUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeComment(
                    meetingWithComments.meetingId!!, commentToRemove.copy(
                        commentId = null
                    ), commentToRemove.author!!
                )
            }
        }
        assertThat(exception.message, `is`(equalTo("The comment id cannot be blank or null")))
    }

    @Test
    fun addMeetingPhotoBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addMeetingsPhoto(
                    " ",
                    PhotoMetadata(),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun addMeetingPhotoBlankTakenByShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addMeetingsPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(takenBy = " "),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that took the photo cannot be blank or null"))
        )
    }

    @Test
    fun addMeetingPhotoNullTakenByShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.addMeetingsPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(takenBy = null),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user that took the photo cannot be blank or null"))
        )
    }

    @Test
    fun addMeetingPhotoCorrectlyAddPhotoToMeetingInDB() {
        runTest {
            val metadata = PhotoMetadata(
                null,
                "Coke can",
                takenBy = "aaaaaa",
                category = "aluminium",
                description = "Trash found along the river"
            )
            val metadataWithId = MeetingServiceImpl.addMeetingsPhoto(
                meetingWithPhotos.meetingId!!,
                metadata,
                Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
            )

            metadata.pictureId = metadataWithId.pictureId

            assertThat(metadataWithId, `is`(equalTo(metadata)))
        }
    }

    @Test
    fun getAllPhotoMetadataBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.getAllPhotosMetadata(" ")
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun getAllPhotoMetadataReturnCorrectModifiedListUponModification() {
        val metadata1 = PhotoMetadata(
            "-NU8AMWCpNWmjCRlNU7x",
            "Trash 1",
            takenBy = "aaaaaa",
            description = "Trash found along the river"
        )
        val metadata2 = PhotoMetadata(
            "-NU8AMzGOjxhfRySRlNm",
            "Trash 2",
            takenBy = "bbbbbb",
            description = "Trash found in the forest"
        )
        val metadata3 = PhotoMetadata(
            "-NU8AN2L5DiW1rfyAOR9",
            "Trash 3",
            takenBy = "cccccc",
            description = "Trash found on the street"
        )

        runTest {
            val channel = Channel<List<PhotoMetadata>>(1)
            backgroundScope.launch {
                MeetingServiceImpl.getAllPhotosMetadata(meetingWithPhotos.meetingId!!).collect {
                    channel.send(it)
                }
            }

            // check initial meetings
            assertThat(
                listOf(metadata1, metadata2, metadata3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // remove one of the three
            MeetingServiceImpl.removeMeetingPhoto(meetingWithPhotos.meetingId!!, metadata2)
            assertThat(
                listOf(metadata1, metadata3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // assert that when we get a photo it doesn't exist
            val exception = assertThrows(MeetingServiceException::class.java) {
                runBlocking {
                    MeetingServiceImpl.getPhoto(meetingWithPhotos.meetingId!!, metadata2)
                }
            }

            assertThat(
                exception.message,
                stringContainsInOrder("Error while getting picture bytes from storage")
            )
        }
    }

    @Test
    fun getPhotoBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.getPhoto(" ", PhotoMetadata())
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun getPhotoNullPictureIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.getPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(pictureId = null)
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The picture id cannot be blank or null")))
    }

    @Test
    fun getPhotoBlankPictureIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.getPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(pictureId = " ")
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The picture id cannot be blank or null")))
    }

    @Test
    fun getPhotoAfterHavingAddedItReturnSamePhoto() {
        val bitmap = Bitmap.createBitmap(123, 240, Bitmap.Config.ARGB_8888)
        runTest {
            val metadata =
                MeetingServiceImpl.addMeetingsPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(
                        null,
                        "Bin",
                        takenBy = "1234567890",
                        description = "Picture of a plastic bin"
                    ),
                    bitmap
                )
            val retrievedPhoto =
                MeetingServiceImpl.getPhoto(meetingWithPhotos.meetingId!!, metadata)
            assertThat(bitmap, equalsBitmap(retrievedPhoto!!))
        }
    }

    @Test
    fun removeMeetingPhotoBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeetingPhoto(" ", PhotoMetadata())
            }
        }

        assertThat(exception.message, `is`(equalTo("The meeting id cannot be blank")))
    }

    @Test
    fun removeMeetingPhotoNullPhotoIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeetingPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(pictureId = null)
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The picture id cannot be blank or null")))
    }

    @Test
    fun removeMeetingPhotoBlankPhotoIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingServiceImpl.removeMeetingPhoto(
                    meetingWithPhotos.meetingId!!,
                    PhotoMetadata(pictureId = " ")
                )
            }
        }

        assertThat(exception.message, `is`(equalTo("The picture id cannot be blank or null")))
    }
}