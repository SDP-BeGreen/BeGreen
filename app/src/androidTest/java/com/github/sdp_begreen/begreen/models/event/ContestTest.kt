package com.github.sdp_begreen.begreen.models.event

import android.location.Location
import android.location.LocationManager
import com.github.sdp_begreen.begreen.models.CustomLatLng
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ContestTest {

    @Test
    fun isInRangeReturnsNullWhenStartCoordinatesIsNull() {

        val contest = Contest(startCoordinates = null)
        val location = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 10.11
            longitude = -9.8
        }
        assertThat(contest.isInRange(location), `is`(nullValue()))
    }

    @Test
    fun isInRangeReturnsTrueWhenDistanceBetweenContestAndLocationIsSmallerThanRadius() {
        testIsInRange(10000.0, true)
    }

    @Test
    fun isInRangeReturnsFalseWhenDistanceBetweenContestAndLocationIsBiggerThanRadius() {
        testIsInRange(1000.0, false)
    }

    private fun testIsInRange(contestRadius: Double, shouldBeInRange: Boolean) {
        val contestLat = 4.32
        val contestLong = -6.32
        val contestLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = contestLat
            longitude = contestLong
        }
        val contest = Contest(
            startCoordinates = CustomLatLng(contestLat, contestLong),
            radius = contestRadius
        )

        val userLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 4.31
            longitude = -6.30
        }

        // Check that the userLocation is indeed close enough to the contest
        assertThat(
            contestLocation.distanceTo(userLocation) <= contestRadius,
            `is`(equalTo(shouldBeInRange))
        )

        assertThat(contest.isInRange(userLocation), `is`(equalTo(shouldBeInRange)))
    }

}