package com.github.sdp_begreen.begreen.models.event

import com.github.sdp_begreen.begreen.firebase.RootPath
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MeetingTest {

    @Test
    fun meetingToStringReturnExpectedString() {
        val newMeeting = Meeting("1", "2", "Test meeting")
        assertThat(newMeeting.toString(), `is`("Test meeting: 2"))
    }

    @Test
    fun metingCopyCorrectlyCopyMeetingWithNewId() {
        val meeting = Meeting("1", "2", "Meeting to copy")
        val copy = meeting.copyWithNewId("4")

        assertThat(meeting.copy(id = "4"), `is`(equalTo(copy)))
    }

    @Test
    fun meetingRootPathIsRootPathMEETINGS() {
        val meeting = Meeting()
        assertThat(meeting.rootPath, `is`(equalTo(RootPath.MEETINGS)))
    }
}