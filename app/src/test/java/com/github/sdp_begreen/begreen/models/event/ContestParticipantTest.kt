package com.github.sdp_begreen.begreen.models.event

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ContestParticipantTest {
    @Test
    fun contestParticipantToStringReturnExpectedString() {
        val newContest = ContestParticipant("1", 156)
        assertThat(newContest.toString(), `is`("Participant: 1, score: 156"))
    }
}