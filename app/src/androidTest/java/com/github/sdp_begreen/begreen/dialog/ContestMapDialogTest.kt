package com.github.sdp_begreen.begreen.dialog

import android.Manifest
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.viewModels.ContestMapDialogViewModel
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Matchers.closeTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class ContestMapDialogTest {

    @get:Rule
    val locationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var scenario: FragmentScenario<ContestMapDialog>
    private lateinit var device: UiDevice
    private lateinit var mapId: String


    private val listenerChannel = Channel<Pair<CustomLatLng?, Double?>>(1)

    private val contestMapDialogListener = object : ContestMapDialog.ContestMapDialogListener {
        override fun onDialogApprove(location: CustomLatLng?, radius: Double?) {
            listenerChannel.trySend(location to radius)
        }
    }

    @Before
    fun initDialog() {
        val args = ContestMapDialog.newInstance(
            contestMapDialogListener,
        ).arguments
        scenario = launchFragmentInContainer(
            args,
            factory = ContestMapDialog.factory(contestMapDialogListener)
        )
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mapId = InstrumentationRegistry.getInstrumentation().targetContext.resources
            .getResourceName(R.id.create_contest_map)
    }

    @Test
    fun titleCorrectlyDisplayed() {
        onView(withId(R.id.create_contest_map_title))
            .check(matches(withText("Contest Location")))
    }

    @Test
    fun locationButtonCorrectlyDisplayed() {
        onView(withId(R.id.create_contest_location_button))
            .check(matches(withText("Location")))
    }

    @Test
    fun radiusButtonCorrectlyDisplayed() {
        onView(withId(R.id.create_contest_radius_button))
            .check(matches(withText("Radius")))
    }

    @Test
    fun mapIsCorrectlyDisplayed() {
        onView(withId(R.id.create_contest_map))
            .check(matches(isDisplayed()))
    }

    @Test
    fun cancelButtonCorrectlyDisplayed() {
        onView(withId(R.id.create_contest_map_cancel_button))
            .check(matches(withText("Cancel")))
    }

    @Test
    fun approveButtonCorrectlyDisplayed() {
        onView(withId(R.id.create_contest_map_approve_button))
            .check(matches(withText("Approve")))
    }

    @Test
    fun clickingApproveButtonCorrectlyNotifyListenerAndCloseDialog() {
        val location = CustomLatLng(46.518078, 6.561769)
        val radius = 1506.8
        val args = ContestMapDialog.newInstance(
            contestMapDialogListener,
            location,
            radius
        ).arguments
        launchFragmentInContainer<ContestMapDialog>(
            args,
            factory = ContestMapDialog.factory(contestMapDialogListener)
        )
        runTest {
            onView(withId(R.id.create_contest_map_approve_button))
                .check(matches(isDisplayed()))
                .perform(click())

            val receivedValues = listenerChannel.receive()

            assertThat(receivedValues.first, `is`(equalTo(location)))
            assertThat(receivedValues.second, `is`(closeTo(radius, 0.1)))

            onView(withId(R.id.create_contest_map_layout))
                .check(doesNotExist())
        }
    }

    @Test
    fun clickingCancelButtonCorrectlyCloseDialog() {
        onView(withId(R.id.create_contest_map_cancel_button))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.create_contest_map_layout))
            .check(doesNotExist())
    }

    @Test
    fun clickingOnMapAddLocationMarkerLocationButtonSelected() {
        val locationChannel = Channel<Marker?>(1)

        runTest {
            scenario.onFragment {
                val vm by it.viewModels<ContestMapDialogViewModel>()
                backgroundScope.launch {
                    vm.locationMarker.collect { marker ->
                        locationChannel.send(marker)
                    }
                }
            }

            // select location button
            onView(withId(R.id.create_contest_location_button))
                .check(matches(isDisplayed()))
                .perform(click())

            // first value should be null (initial value)
            assertThat(locationChannel.receive(), `is`(nullValue()))

            device.findObject(UiSelector().resourceId(mapId)).click()

            // second value should not be null
            assertThat(locationChannel.receive(), `is`(notNullValue()))
        }
    }

    @Test
    fun clickingOnMapAddRadiusMarkerRadiusButtonSelected() {
        val radiusChannel = Channel<Marker?>(1)

        runTest {
            scenario.onFragment {
                val vm by it.viewModels<ContestMapDialogViewModel>()
                backgroundScope.launch {
                    vm.radiusMarker.collect { marker ->
                        radiusChannel.send(marker)
                    }
                }
            }

            // first value should be null (initial value)
            assertThat(radiusChannel.receive(), `is`(nullValue()))

            // select radius button
            onView(withId(R.id.create_contest_radius_button))
                .check(matches(isDisplayed()))
                .perform(click())

            device.findObject(UiSelector().resourceId(mapId)).click()

            assertThat(radiusChannel.receive(), `is`(notNullValue()))
        }
    }

    @Test
    fun addingLocationAndRadiusMarkerShouldDrawCircle() {
        val circleChannel = Channel<Circle?>(1)

        runTest {
            scenario.onFragment {
                val vm by it.viewModels<ContestMapDialogViewModel>()
                backgroundScope.launch {
                    vm.drawnCircle.collect { circle ->
                        circleChannel.send(circle)
                    }
                }
            }

            assertThat(circleChannel.receive(), `is`(nullValue()))

            // place location marker
            device.findObject(UiSelector().resourceId(mapId)).click()

            // select radius button
            onView(withId(R.id.create_contest_radius_button))
                .check(matches(isDisplayed()))
                .perform(click())

            // place radius marker
            device.findObject(UiSelector().resourceId(mapId)).click()

            val circle1 = circleChannel.receive()
            assertThat(circle1, `is`(notNullValue()))
        }
    }

    @Test
    fun initialPassedLocationAndRadiusCorrectlyDrawCircleAndAddMarkers() {
        val args = ContestMapDialog.newInstance(
            contestMapDialogListener,
            CustomLatLng(46.518078, 6.561769),
            1506.8
        ).arguments
        val mapDialogScenario = launchFragmentInContainer<ContestMapDialog>(
            args,
            factory = ContestMapDialog.factory(contestMapDialogListener)
        )
        val locationChannel = Channel<Marker?>(1)
        val radiusChannel = Channel<Marker?>(1)
        val circleChannel = Channel<Circle?>(1)
        runTest {
            mapDialogScenario.onFragment {
                val vm by it.viewModels<ContestMapDialogViewModel>()
                backgroundScope.launch {
                    vm.locationMarker.collect { location ->
                        locationChannel.send(location)
                    }
                }
                backgroundScope.launch {
                    vm.radiusMarker.collect { radius ->
                        radiusChannel.send(radius)
                    }
                }
                backgroundScope.launch {
                    vm.drawnCircle.collect { circle ->
                        circleChannel.send(circle)
                    }
                }
            }

            // assert that should initially directly contains a value
            assertThat(locationChannel.receive(), `is`(notNullValue()))
            assertThat(radiusChannel.receive(), `is`(notNullValue()))
            assertThat(circleChannel.receive(), `is`(notNullValue()))
        }
    }

}