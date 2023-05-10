package com.github.sdp_begreen.begreen.models.event

import com.github.sdp_begreen.begreen.firebase.RootPath
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ContestTest {
    @Test
    fun contestToStringReturnExpectedString() {
        val newContest = Contest("1", "2", "Test contest")
        assertThat(newContest.toString(), `is`("Test contest: 2"))
    }

    @Test
    fun metingCopyCorrectlyCopyMeetingWithNewId() {
        val contest = Contest("1", "2", "Meeting to copy")
        val copy = contest.copy("4")

        assertThat(contest.copy(id = "4"), `is`(equalTo(copy)))
    }

    @Test
    fun meetingRootPathIsRootPathMEETINGS() {
        val contest = Contest()
        assertThat(contest.rootPath, `is`(equalTo(RootPath.CONTESTS)))
    }
}