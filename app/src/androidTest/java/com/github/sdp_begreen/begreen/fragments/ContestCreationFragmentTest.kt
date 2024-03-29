package com.github.sdp_begreen.begreen.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.ContestCreationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class ContestCreationFragmentTest {

    companion object {
        private val eventServiceApi: EventService = mock()
        private val geoServiceApi: GeocodingService = mock()
        private lateinit var ccvm: ContestCreationViewModel

        private val customLatLng = CustomLatLng(46.519653, 6.632273)

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @BeforeClass
        fun setUpEventService() {
            runTest {
                // Initial setup of getAllMeetings
                whenever(eventServiceApi.createEvent<Contest>(any())).thenReturn(null)

                whenever(geoServiceApi.getLongLat(any())).thenReturn(customLatLng)

            }
        }
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { eventServiceApi }
            single { geoServiceApi }
        })
    )
    private lateinit var fragmentScenario: FragmentScenario<ContestCreationFragment>

    @Before
    fun setup() {
        fragmentScenario =
            launchFragmentInContainer<ContestCreationFragment>(themeResId = R.style.Theme_BeGreen)
        ccvm = ContestCreationViewModel()
    }

    @Test
    fun createContestSetTitleIsDisplayed() {
        onView(withId(R.id.contest_creation_title)).perform(typeText("Test contest"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun createContestCheckboxeIsDisplayed() {
        onView(withId(R.id.private_contest_checkbox)).perform(click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun createContestExpandButtonWorks() {
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.contest_location_details_container)).check(matches(isDisplayed()))
    }

    @Test
    fun createContestExpandButtonWorks2() {
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.contest_location_details_container)).check(matches(isDisplayed()))
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.contest_location_details_container)).check(matches(not(isDisplayed())))
    }

    @Test
    fun createContestCityIsDisplayed() {
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))

        onView(withId(R.id.city_contest_creation)).perform(replaceText("Test city"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.contest_creation_location_expand)).perform(click())
    }

    @Test
    fun createContestCityIsChanging() {

        val name = "Testcity"

        runTest {
            fragmentScenario.onFragment {
                val vm by it.viewModels<ContestCreationViewModel>()

                assertTrue(vm.editCity(name + "q"))

            }
        }

        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.city_contest_creation)).perform(replaceText(name))

        onView(withId(R.id.postal_code_contest_creation)).perform(replaceText(name))
        onView(withId(R.id.city_contest_creation)).perform(replaceText(name))
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
        onView(withId(R.id.contest_creation_location_expand)).perform(click())

        onView(withId(R.id.city_contest_creation)).check(matches(withText(name)))

    }

    @Test
    fun createContestPostalCodeIsDisplayed() {
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))

        onView(withId(R.id.postal_code_contest_creation)).perform(replaceText("1000"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.contest_creation_location_expand)).perform(click())
    }

    @Test
    fun createContestPostalCodeIsChanging() {

        val number = "1322"

        runTest {
            fragmentScenario.onFragment {
                val vm by it.viewModels<ContestCreationViewModel>()

                assertTrue(vm.editPostalCode("1323"))

            }
        }

        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))

        onView(withId(R.id.postal_code_contest_creation)).perform(click())
        onView(withId(R.id.postal_code_contest_creation)).perform(replaceText(number))

        onView(withId(R.id.city_contest_creation)).perform(click())
        onView(withId(R.id.city_contest_creation)).perform(replaceText("Lausanne"))

        onView(withId(R.id.postal_code_contest_creation)).perform(click())
        onView(withId(R.id.postal_code_contest_creation)).perform(replaceText(number))
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
        onView(withId(R.id.contest_creation_location_expand)).perform(click())

        onView(withId(R.id.postal_code_contest_creation)).check(matches(withText(number)))

    }

    // This test seems to work on local but not on CI, if someone has an idea why, please tell me
    //@Test
    //fun createContestCountryPickerWorks() {
    //    onView(withId(R.id.contest_creation_location_expand)).perform(click())
    //        .check(matches(isDisplayed()))
//
    //    onView(withId(R.id.contest_creation_country_picker)).perform(click())
//
    //    assertTrue(ccvm.editCountry("France"))
    //}

    @Test
    fun createContestRadiusIsDisplayed() {
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))

        val radius = "100"

        onView(withId(R.id.radius_contest_creation)).perform(replaceText(radius))
            .check(matches(isDisplayed()))

        onView(withId(R.id.radius_contest_creation)).check(matches(withText(radius)))
    }

    @Test
    fun createContestRadiusIsChanging() {

        val number = 200.0

        runTest {
            fragmentScenario.onFragment {
                val vm by it.viewModels<ContestCreationViewModel>()
                assertTrue(vm.editRadius(100.0))
            }
        }

        onView(withId(R.id.contest_creation_location_expand)).perform(click())
            .check(matches(isDisplayed()))

        //onView(withId(R.id.radius_contest_creation)).perform(click())
        onView(withId(R.id.radius_contest_creation)).perform(replaceText(number.toString()))

        onView(withId(R.id.city_contest_creation)).perform(click())
        onView(withId(R.id.city_contest_creation)).perform(replaceText("Lausanne"))

        //onView(withId(R.id.radius_contest_creation)).perform(click())
        onView(withId(R.id.radius_contest_creation)).perform(replaceText(number.toString()))
        onView(withId(R.id.contest_creation_location_expand)).perform(click())
        onView(withId(R.id.contest_creation_location_expand)).perform(click())

        onView(withId(R.id.radius_contest_creation)).check(matches(withText(number.toString())))

    }

    // Doesn't check anything, need to search how to check if the spinner is displayed
    //@Test
    //fun createContestTimeZoneIsDisplayed() {
//
    //    onView(withId(R.id.contest_timezone_spinner)).perform(click()).check(matches(isDisplayed()))
//
    //}

    @Test
    fun createContestStartDateIsDisplayed() {
        val date = "25/04/2030"

        onView(withId(R.id.start_date_contest_text)).perform(replaceText(date))
        onView(withId(R.id.start_date_contest_text)).check(matches(withText(date)))
    }

    @Test
    fun createContestEndDateIsDisplayed() {
        val date = "25/04/2030"

        onView(withId(R.id.end_date_contest_text)).perform(replaceText(date))
        onView(withId(R.id.end_date_contest_text)).check(matches(withText(date)))
    }

    @Test
    fun createContestDateIsChanging() {

        val date = "25/04/2030"
        val date2 = "26/04/2030"
        val date3 = "27/04/2030"

        runTest {
            fragmentScenario.onFragment {
                val vm by it.viewModels<ContestCreationViewModel>()
            }
        }

        onView(withId(R.id.start_date_contest_text)).perform(click())
        onView(withId(R.id.start_date_contest_text)).perform(replaceText(date))
        onView(withId(R.id.start_date_contest_text)).check(matches(withText(date)))


        onView(withId(R.id.end_date_contest_text)).perform(click())
        onView(withId(R.id.end_date_contest_text)).perform(replaceText(date2))
        onView(withId(R.id.end_date_contest_text)).check(matches(withText(date2)))

        onView(withId(R.id.start_date_contest_text)).perform(click())
        onView(withId(R.id.start_date_contest_text)).perform(replaceText(date3))

        onView(withId(R.id.end_date_contest_text)).perform(click())
        onView(withId(R.id.end_date_contest_text)).perform(replaceText(date2))
        onView(withId(R.id.start_date_contest_text)).perform(click())
        onView(withId(R.id.start_date_contest_text)).check(matches(withText(date2)))

    }


    @Test
    fun createContestStartTimeIsDisplayed() {
        val time = "12:00"

        onView(withId(R.id.start_hour_contest_text)).perform(replaceText(time))
        onView(withId(R.id.start_hour_contest_text)).check(matches(withText(time)))
    }

    @Test
    fun createContestEndTimeIsDisplayed() {
        val time = "12:00"

        onView(withId(R.id.end_hour_contest_text)).perform(replaceText(time))
        onView(withId(R.id.end_hour_contest_text)).check(matches(withText(time)))
    }

    @Test
    fun createContestHourIsChanging() {

        val hour1 = "10:0"
        val hour2 = "15:40"
        val incorrect1 = "a"
        val incorrect2 = "27:00"

        runTest {
            fragmentScenario.onFragment {
                val vm by it.viewModels<ContestCreationViewModel>()
            }
        }

        onView(withId(R.id.start_hour_contest_text)).perform(click())
        onView(withId(R.id.start_hour_contest_text)).perform(replaceText(hour1))

        onView(withId(R.id.end_hour_contest_text)).perform(click())
        onView(withId(R.id.end_hour_contest_text)).perform(replaceText(hour2))

        onView(withId(R.id.start_hour_contest_text)).perform(click())
        onView(withId(R.id.start_hour_contest_text)).perform(replaceText(incorrect1))

        onView(withId(R.id.end_hour_contest_text)).perform(click())
        onView(withId(R.id.end_hour_contest_text)).perform(replaceText(incorrect2))

        onView(withId(R.id.start_hour_contest_text)).check(matches(withText(hour1)))
        onView(withId(R.id.start_hour_contest_text)).perform(click())
        onView(withId(R.id.start_hour_contest_text)).perform(replaceText(hour2))


        onView(withId(R.id.start_hour_contest_text)).check(matches(withText(hour2)))

    }

    @Test
    fun createContestDatePickerIsDisplaying() {
        onView(withId(R.id.date_period_contest)).perform(click())

        assertFalse(ccvm.editStartDate(100L))
    }

    @Test
    fun createContestStartHourPickerIsDisplaying() {
        onView(withId(R.id.start_hour_contest)).perform(click())

        assertTrue(ccvm.editStartHour(10))
    }

    @Test
    fun createContestEndHourPickerIsDisplaying() {
        onView(withId(R.id.end_hour_contest)).perform(click())

        assertTrue(ccvm.editEndHour(10))
    }

    @Test
    fun createContestMapButtonWorks() {
        onView(withId(R.id.contest_creation_location_map)).perform(click())

        onView(withId(R.id.create_contest_map_layout))
            .check(matches(isDisplayed()))

        onView(withId(R.id.create_contest_map_approve_button)).perform(click())

        onView(withId(R.id.contest_creation_location_map)).check(matches(isDisplayed()))

    }
}