package com.github.sdp_begreen.begreen.models.event

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ContestParticipantTest {
    @Test
    fun contestParticipantToStringReturnExpectedString() {
        val contestParticipant = ContestParticipant("1", 156)
        assertThat(contestParticipant.toString(), `is`("Participant: 1, score: 156"))
    }

    @Test
    fun modifyContestParticipantIdCorrectlyModifiesIt() {
        val contestParticipant = ContestParticipant("1")
        assertThat(contestParticipant.id, `is`(equalTo("1")))
        contestParticipant.id = "newId"
        assertThat(contestParticipant.id, `is`(equalTo("newId")))
    }

    @Test
    fun modifyContestParticipantScoreCorrectlyModifiesIt() {
        val contestParticipant = ContestParticipant("1", 123)
        assertThat(contestParticipant.score, `is`(equalTo(123)))
        contestParticipant.score = 457
        assertThat(contestParticipant.score, `is`(equalTo(457)))
    }
}