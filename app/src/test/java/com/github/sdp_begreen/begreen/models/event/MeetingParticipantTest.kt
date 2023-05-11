package com.github.sdp_begreen.begreen.models.event

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MeetingParticipantTest {
    @Test
    fun meetingParticipantToStringReturnExpectedString() {
        val newContest = MeetingParticipant("1")
        assertThat(
            newContest.toString(),
            `is`("Participant: 1")
        )
    }
}