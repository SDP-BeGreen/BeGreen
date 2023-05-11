package com.github.sdp_begreen.begreen.models.event

import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.CustomLatLng
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
        val copy = contest.copyWithNewId("4")

        assertThat(contest.copy(id = "4"), `is`(equalTo(copy)))
    }

    @Test
    fun meetingRootPathIsRootPathMEETINGS() {
        val contest = Contest()
        assertThat(contest.rootPath, `is`(equalTo(RootPath.CONTESTS)))
    }

    @Test
    fun changingEndDateTimeCorrectlyUpdatesContest(){
        val contest = Contest(endDateTime = 1)
        contest.endDateTime = 123
        assertThat(contest.endDateTime, `is`(equalTo(123)))
    }
    @Test
    fun changingStartCoordinatesCorrectlyUpdatesContest(){
        val contest = Contest(startCoordinates = CustomLatLng(1.0, 2.0))
        assertThat(contest.startCoordinates, `is`(equalTo(CustomLatLng(1.0, 2.0))))
        contest.startCoordinates = CustomLatLng(12.0,34.0)
        assertThat(contest.startCoordinates, `is`(equalTo(CustomLatLng(12.0, 34.0))))
    }
}