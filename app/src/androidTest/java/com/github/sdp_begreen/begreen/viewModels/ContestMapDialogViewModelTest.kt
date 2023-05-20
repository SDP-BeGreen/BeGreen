package com.github.sdp_begreen.begreen.viewModels

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.viewModels.ContestMapDialogViewModel.SelectedButton
import com.google.android.gms.internal.maps.zzaa
import com.google.android.gms.internal.maps.zzl
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class ContestMapDialogViewModelTest {


    private lateinit var viewModel: ContestMapDialogViewModel

    // mock this zzaa and zzl classes whatever those are, to be able to instantiate a Marker and Circle
    val zzaa = mock(zzaa::class.java)
    val zzl = mock(zzl::class.java)

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @Before
    fun setupViewModel() {
        viewModel = ContestMapDialogViewModel()
    }

    @Test
    fun locationButtonShouldInitiallyBeSelected() {
        assertThat(viewModel.selectedButton.value, `is`(equalTo(SelectedButton.LOCATION_BUTTON)))
    }

    @Test
    fun locationMarkerShouldInitiallyBeNull() {
        assertThat(viewModel.locationMarker.value, `is`(nullValue()))
    }

    @Test
    fun radiusMarkerShouldInitiallyBeNull() {
        assertThat(viewModel.radiusMarker.value, `is`(nullValue()))
    }

    @Test
    fun drawnCircleShouldInitiallyBeNull() {
        assertThat(viewModel.drawnCircle.value, `is`(nullValue()))
    }

    @Test
    fun updateLocationButtonCorrectlyUpdateIt() {
        val channel = Channel<SelectedButton>(1)
        runTest {
            backgroundScope.launch {
                viewModel.selectedButton.collect {
                    channel.send(it)
                }
            }

            viewModel.selectButton(SelectedButton.LOCATION_BUTTON)
            assertThat(channel.receive(), `is`(equalTo(SelectedButton.LOCATION_BUTTON)))

            viewModel.selectButton(SelectedButton.RADIUS_BUTTON)
            assertThat(channel.receive(), `is`(equalTo(SelectedButton.RADIUS_BUTTON)))
        }
    }

    @Test
    fun updateLocationMarkerCorrectlyUpdateIt() {
        val channel = Channel<Marker?>(1)
        runTest {
            backgroundScope.launch {
                viewModel.locationMarker.collect {
                    channel.send(it)
                }
            }

            // simply mock a marker, I am juste interested to its reference
            val marker1 = Marker(zzaa)
            viewModel.newLocationMarker(marker1)
            assertThat(channel.receive(), sameInstance(marker1))

            val marker2 = Marker(zzaa)
            viewModel.newLocationMarker(marker2)
            assertThat(channel.receive(), sameInstance(marker2))
        }
    }

    @Test
    fun updateLocationMarkerNullValueKeepOldMarker() {
        val channel = Channel<Marker?>(1)
        runTest {
            backgroundScope.launch {
                viewModel.locationMarker.collect {
                    channel.send(it)
                }
            }

            // simply mock a marker, I am juste interested to its reference
            val marker1 = Marker(zzaa)
            viewModel.newLocationMarker(marker1)
            assertThat(channel.receive(), sameInstance(marker1))

            // as it keeps old marker, the flow does not send new value
            viewModel.newLocationMarker(null)
            assertTrue(channel.isEmpty)
        }
    }

    @Test
    fun updateRadiusMarkerCorrectlyUpdateIt() {
        val channel = Channel<Marker?>(1)
        runTest {
            backgroundScope.launch {
                viewModel.radiusMarker.collect {
                    channel.send(it)
                }
            }

            // simply mock a marker, I am juste interested to its reference
            val marker1 = Marker(zzaa)
            viewModel.newRadiusMarker(marker1)
            assertThat(channel.receive(), sameInstance(marker1))

            val marker2 = Marker(zzaa)
            viewModel.newRadiusMarker(marker2)
            assertThat(channel.receive(), sameInstance(marker2))
        }
    }

    @Test
    fun updateRadiusMarkerNullValueKeepOldMarker() {
        val channel = Channel<Marker?>(1)
        runTest {
            backgroundScope.launch {
                viewModel.radiusMarker.collect {
                    channel.send(it)
                }
            }

            // simply mock a marker, I am juste interested to its reference
            val marker1 = Marker(zzaa)
            viewModel.newRadiusMarker(marker1)
            assertThat(channel.receive(), sameInstance(marker1))

            // as it keeps old marker, the flow does not send new value
            viewModel.newRadiusMarker(null)
            assertTrue(channel.isEmpty)
        }
    }

    @Test
    fun updateDrawnCircleShouldCorrectlyUpdateIt() {
        val channel = Channel<Circle?>(1)
        runTest {
            backgroundScope.launch {
                viewModel.drawnCircle.collect {
                    channel.send(it)
                }
            }
            val circle = Circle(zzl)

            viewModel.newCircle(circle)
            assertThat(channel.receive(), sameInstance(circle))

            val circle2 = Circle(zzl)

            viewModel.newCircle(circle2)
            assertThat(channel.receive(), sameInstance(circle2))
        }
    }

    @Test
    fun updateDrawnCircleNullValueShouldKeepOldCircle() {
        val channel = Channel<Circle?>(1)
        runTest {
            backgroundScope.launch {
                viewModel.drawnCircle.collect {
                    channel.send(it)
                }
            }
            val circle = Circle(zzl)

            viewModel.newCircle(circle)
            assertThat(channel.receive(), sameInstance(circle))

            viewModel.newCircle(null)
            assertTrue(channel.isEmpty)
        }
    }
}