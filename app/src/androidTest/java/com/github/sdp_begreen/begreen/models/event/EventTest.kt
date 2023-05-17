package com.github.sdp_begreen.begreen.models.event

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class EventTest {

    @Test
    fun isStartedReturnsNullWhenStartDateTimeIsNull() {
        val event = Meeting(startDateTime = null)
        assertThat(event.isStarted(), `is`(nullValue()))
    }

    @Test
    fun isStartedReturnsTrueWhenCurrentTimeIsBiggerThanStartDateTime() {
        val event = Meeting(startDateTime = System.currentTimeMillis() - 10000)
        assertThat(event.isStarted(), `is`(equalTo(true)))
    }

    @Test
    fun isStartedReturnsFalseWhenCurrentTimeIsSmallerThanStartDateTime() {
        val event = Meeting(startDateTime = System.currentTimeMillis() + 10000)
        assertThat(event.isStarted(), `is`(equalTo(false)))
    }

    @Test
    fun isFinishedReturnsNullWhenStartDateTimeIsNull() {
        val event = Meeting(endDateTime = null)
        assertThat(event.isFinished(), `is`(nullValue()))
    }

    @Test
    fun isFinishedReturnsTrueWhenCurrentTimeIsBiggerThanStartDateTime() {
        val event = Meeting(endDateTime = System.currentTimeMillis() - 10000)
        assertThat(event.isFinished(), `is`(equalTo(true)))
    }

    @Test
    fun isFinishedReturnsFalseWhenCurrentTimeIsSmallerThanStartDateTime() {
        val event = Meeting(endDateTime = System.currentTimeMillis() + 10000)
        assertThat(event.isFinished(), `is`(equalTo(false)))
    }

    @Test
    fun isActiveReturnsNullWhenStartDateTimeIsNull() {
        val event = Meeting(startDateTime = null)
        assertThat(event.isActive(), `is`(nullValue()))
    }

    @Test
    fun isActiveReturnsNullWhenEndDateTimeIsNull() {
        val event = Meeting(endDateTime = null)
        assertThat(event.isActive(), `is`(nullValue()))
    }

    @Test
    fun isActiveReturnsTrueWhenCurrentTimeIsInBetweenStartDateTimeAndEndDateTime() {
        val event = Meeting(startDateTime = System.currentTimeMillis() - 10000, endDateTime = System.currentTimeMillis() + 10000)
        assertThat(event.isActive(), `is`(equalTo(true)))
    }

    @Test
    fun isActiveReturnsFalseWhenCurrentTimeIsSmallerThanStartDateTime() {
        val event = Meeting(startDateTime = System.currentTimeMillis() + 10000, endDateTime = System.currentTimeMillis() + 50000)
        assertThat(event.isActive(), `is`(equalTo(false)))
    }

    @Test
    fun isActiveReturnsFalseWhenCurrentTimeIsBiggerThanEndDateTime() {
        val event = Meeting(startDateTime = System.currentTimeMillis() - 50000, endDateTime = System.currentTimeMillis() - 10000)
        assertThat(event.isActive(), `is`(equalTo(false)))
    }
}