package com.github.sdp_begreen.begreen.firebase.meetingServices

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.matchers.ContainsWithSameOrder.Companion.inWithOrder
import com.github.sdp_begreen.begreen.models.Comment
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.`in`
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThrows
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class MeetingCommentServiceTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()

        private val meetingWithComments = Meeting(
            "-NU5gPoTl4hxHCywcdWs",
            "89898989",
            "Test comment meeting",
            "Meeting acting as a container to test comments"
        )

        private val modifiedComment =
            Comment("-NU5tkh5YDVxESHe3WC5", "1234512345", body = "The comment has been modified")

        private val commentToRemove =
            Comment("-NUB11733L2AbvOUZmai", "abcdefg", body = "Comment to test remove throws")
    }

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun addCommentBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.addComment(" ", Comment())
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id cannot be blank"))
        )
    }

    @Test
    fun addCommentBlankAuthorShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.addComment(
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
                MeetingCommentServiceImpl.addComment(
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
                MeetingCommentServiceImpl.addComment(
                    meetingWithComments.meetingId!!,
                    comment
                )
            val commentWithId = comment.copy(
                commentId = addedComment.commentId,
                writtenAt = addedComment.writtenAt,
                modifiedAt = addedComment.modifiedAt
            )
            assertThat(
                addedComment,
                `is`(equalTo(commentWithId))
            )
        }
    }

    @Test
    fun modifyCommentBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.modifyComment(
                    meetingWithComments.meetingId!!,
                    " ",
                    modifiedComment
                )
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The user id cannot be blank"))
        )
    }

    @Test
    fun modifyCommentBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.modifyComment(
                    " ",
                    modifiedComment.author!!,
                    modifiedComment
                )
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
                MeetingCommentServiceImpl.modifyComment(
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
                MeetingCommentServiceImpl.modifyComment(
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
                MeetingCommentServiceImpl.modifyComment(
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
                MeetingCommentServiceImpl.getAllComments(" ").collect()
            }
        }

        assertThat(
            exception.message,
            `is`(equalTo("The meeting id should not be blank"))
        )
    }


    @Test
    fun getAllCommentsForMeetingReturnCorrectListOfCommentsFromDB() {
        val comments = listOf(
            Comment("-NU6-oFySS4uNwdOEP6o", "1234", body = "The first comment"),
            Comment("-NU6-oGo8r5U3kgJEOw8", "2345", body = "The second comment"),
            Comment("-NU6-oGvGXDYuYnCR5L9", "3456", body = "The third comment")
        )

        runBlocking {
            val list =
                MeetingCommentServiceImpl.getAllComments(meetingWithComments.meetingId!!)
                    .first()
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
                MeetingCommentServiceImpl.getAllComments(meetingWithComments.meetingId!!)
                    .collect {
                        channel.send(it)
                    }
            }

            // check initial meetings
            assertThat(
                listOf(comment1, comment2, comment3),
                everyItem(`is`(`in`(channel.receive())))
            )
            // modify first elem
            var modifiedComment1 = comment1.copy(
                body = "Modified first comment body"
            )
            modifiedComment1 = MeetingCommentServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                modifiedComment1.author!!,
                modifiedComment1
            )
            assertThat(
                listOf(modifiedComment1, comment2, comment3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify second elem
            var modifiedComment2 = comment2.copy(
                body = "Modified second comment body"
            )
            modifiedComment2 = MeetingCommentServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                modifiedComment2.author!!,
                modifiedComment2
            )
            assertThat(
                listOf(modifiedComment1, modifiedComment2, comment3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // modify third elem
            var modifiedComment3 = comment3.copy(
                body = "Modified third comment body"
            )
            modifiedComment3 = MeetingCommentServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                modifiedComment3.author!!,
                modifiedComment3
            )
            assertThat(
                listOf(modifiedComment1, modifiedComment2, modifiedComment3),
                everyItem(`is`(`in`(channel.receive())))
            )

            // remove a comment
            MeetingCommentServiceImpl.removeComment(
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
    fun getAllCommentsShouldBeOrderedByModifiedDate() {
        val comment1 = Comment(null, "author1", body = "This is author1's comment")
        val comment2 = Comment(null, "author2", body = "This is author2's comment")
        val comment3 = Comment(null, "author3", body = "This is author3's comment")

        runTest {
            val newComment3 =
                MeetingCommentServiceImpl.addComment(meetingWithComments.meetingId!!, comment3)
            val newComment2 =
                MeetingCommentServiceImpl.addComment(meetingWithComments.meetingId!!, comment2)
            val newComment1 =
                MeetingCommentServiceImpl.addComment(meetingWithComments.meetingId!!, comment1)

            val channel = Channel<List<Comment>>(1)
            backgroundScope.launch {
                MeetingCommentServiceImpl.getAllComments(meetingWithComments.meetingId!!)
                    .collect {
                        channel.send(it)
                    }
            }

            assertThat(
                listOf(newComment1, newComment2, newComment3),
                `is`(inWithOrder(channel.receive()))
            )

            val modifiedComment2 = MeetingCommentServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                newComment2.author!!,
                newComment2.copy(body = "Modified comment2")
            )

            assertThat(
                listOf(modifiedComment2, newComment1, newComment3),
                `is`(inWithOrder(channel.receive()))
            )

            val modifiedComment3 = MeetingCommentServiceImpl.modifyComment(
                meetingWithComments.meetingId!!,
                newComment3.author!!,
                newComment3.copy(body = "Modified comment3")
            )

            assertThat(
                listOf(modifiedComment3, modifiedComment2, newComment1),
                `is`(inWithOrder(channel.receive()))
            )
        }
    }

    @Test
    fun removeCommentBlankMeetingIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.removeComment(
                    " ",
                    commentToRemove,
                    commentToRemove.author!!
                )
            }
        }
        assertThat(
            exception.message,
            `is`(equalTo("The meeting id cannot be blank"))
        )
    }

    @Test
    fun removeCommentBlankUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.removeComment(
                    meetingWithComments.meetingId!!,
                    commentToRemove,
                    " "
                )
            }
        }
        assertThat(
            exception.message,
            `is`(equalTo("The user id cannot be blank"))
        )
    }

    @Test
    fun removeCommentBlankCommentIdThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.removeComment(
                    meetingWithComments.meetingId!!,
                    commentToRemove.copy(
                        commentId = " "
                    ),
                    commentToRemove.author!!
                )
            }
        }
        assertThat(
            exception.message,
            `is`(equalTo("The comment id cannot be blank or null"))
        )
    }

    @Test
    fun removeCommentNullUserIdShouldThrowIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runTest {
                MeetingCommentServiceImpl.removeComment(
                    meetingWithComments.meetingId!!,
                    commentToRemove.copy(
                        commentId = null
                    ),
                    commentToRemove.author!!
                )
            }
        }
        assertThat(
            exception.message,
            `is`(equalTo("The comment id cannot be blank or null"))
        )
    }
}