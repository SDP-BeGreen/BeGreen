package com.github.sdp_begreen.begreen.models

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MeetingTest {

    @Test
    fun meetingToStringReturnExpectedString() {
        val newMeeting = Meeting("1", "2", "Test meeting")
        assertThat(newMeeting.toString(), `is`("Test meeting: 2"))
    }
}