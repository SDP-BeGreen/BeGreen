package com.github.sdp_begreen.begreen.utils

import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.models.event.MeetingParticipant
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Test

class PreconditionTest {

    @Test
    fun checkArgumentThrowForIncorrectArgument() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkArgument(false, "some message")
        }

        assertThat(error.message, `is`("some message"))
    }

    @Test
    fun checkArgumentDefaultEmptyMessage() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkArgument(false)
        }

        assertThat(error.message, `is`(equalTo("")))
    }

    @Test
    fun checkArgumentDoNotThrowForCorrectArgument() {
        try {
            checkArgument(true, "Should not be thrown")
        } catch (e: IllegalArgumentException) {
            fail("Should not catch any exception")
        }
    }

    @Test
    fun checkRootPathMatchEventClassImplThrowForIncoherentMeetingRootPath() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkRootPathMatchEventClassImpl(RootPath.MEETINGS, Contest::class.java)
        }

        assertThat(
            error.message,
            `is`(equalTo("The root path is of type ${RootPath.MEETINGS.name} but the expected event type is Contest"))
        )
    }

    @Test
    fun checkRootPathMatchEventClassImplThrowForIncoherentContestRootPath() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkRootPathMatchEventClassImpl(RootPath.CONTESTS, Meeting::class.java)
        }

        assertThat(
            error.message,
            `is`(equalTo("The root path is of type ${RootPath.CONTESTS.name} but the expected event type is Meeting"))
        )
    }

    @Test
    fun checkRootPathMatchEventClassImplDoesNotThrowForCoherentMeetingRootPath() {
        try {
            checkRootPathMatchEventClassImpl(RootPath.MEETINGS, Meeting::class.java)
        } catch (e: IllegalArgumentException) {
            fail("Should not catch any exception")
        }
    }

    @Test
    fun checkRootPathMatchEventClassImplDoesNotThrowForCoherentContestRootPath() {
        try {
            checkRootPathMatchEventClassImpl(RootPath.CONTESTS, Contest::class.java)
        } catch (e: IllegalArgumentException) {
            fail("Should not catch any exception")
        }
    }

    @Test
    fun checkRootPathMatchParticipantClassImplThrowForIncoherentMeetingRootPath() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkRootPathMatchParticipantClassImpl(
                RootPath.MEETINGS,
                ContestParticipant::class.java
            )
        }

        assertThat(
            error.message,
            `is`(equalTo("The root path is of type ${RootPath.MEETINGS.name} but the expected participant type is ContestParticipant"))
        )
    }

    @Test
    fun checkRootPathMatchParticipantClassImplThrowForIncoherentContestRootPath() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkRootPathMatchParticipantClassImpl(
                RootPath.CONTESTS,
                MeetingParticipant::class.java
            )
        }

        assertThat(
            error.message,
            `is`(equalTo("The root path is of type ${RootPath.CONTESTS.name} but the expected participant type is MeetingParticipant"))
        )
    }

    @Test
    fun checkRootPathMatchParticipantClassImplDoesNotThrowForCoherentMeetingRootPath() {
        try {
            checkRootPathMatchParticipantClassImpl(
                RootPath.MEETINGS,
                MeetingParticipant::class.java
            )
        } catch (e: IllegalArgumentException) {
            fail("Should not catch any exception")
        }
    }

    @Test
    fun checkRootPathMatchParticipantClassImplDoesNotThrowForCoherentContestRootPath() {
        try {
            checkRootPathMatchParticipantClassImpl(
                RootPath.CONTESTS,
                ContestParticipant::class.java
            )
        } catch (e: IllegalArgumentException) {
            fail("Should not catch any exception")
        }
    }
}