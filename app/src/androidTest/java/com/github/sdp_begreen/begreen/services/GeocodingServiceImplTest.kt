package com.github.sdp_begreen.begreen.services

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.models.CustomLatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class GeocodingServiceImplTest {

    private val context = getApplicationContext<Context>()
    private val geocodingService = GeocodingServiceImpl(context)

    @Test
    fun geocodingImplementationWorksAsExpectedByReturningCorrectAddress() {
        runTest {
            assertThat(
                geocodingService.getAddresses(CustomLatLng(37.7749, -122.4194), 1)
                    ?.first()?.locality,
                `is`(equalTo("San Francisco"))
            )
        }
    }

    @Test
    fun geocodingNullLatitudeReturnsEmptyList() {
        runTest {
            assertThat(
                geocodingService.getAddresses(CustomLatLng(longitude = 1.2), 1),
                `is`(emptyList())
            )
        }
    }

    @Test
    fun geocodingNullLongitudeReturnsEmptyList() {
        runTest {
            assertThat(
                geocodingService.getAddresses(CustomLatLng(latitude = 1.2), 1),
                `is`(emptyList())
            )
        }
    }

    @Test
    fun getLongLatReturnsNullWhenGivenInvalidAddress() {
        runTest {
            assertThat(
                geocodingService.getLongLat("Invalid address"),
                `is`(nullValue())
            )
        }
    }

    @Test
    fun getLongLatReturnsExpectedLongLatWhenGivenValidAddress() {
        runTest {
            assertThat(
                geocodingService.getLongLat("1600 Pennsylvania Avenue NW, Washington, DC"),
                `is`(equalTo(CustomLatLng(38.8976633, -77.0365739)))
            )
        }
    }

}