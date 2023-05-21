package com.github.sdp_begreen.begreen.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.viewModels.ContestCreationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.services.GeocodingService
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any


@RunWith(AndroidJUnit4::class)
@LargeTest
class ContestCreationFragmentTest {

    companion object {
        private val eventServiceApi: EventService = mock()
        private val geoServiceApi: GeocodingService = mock()
        private lateinit var ccvm: ContestCreationViewModel

        @Before
        fun setUpVM() {
            ccvm = ContestCreationViewModel()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @BeforeClass
        fun setUpEventService(){
            runTest {
                // Initial setup of getAllMeetings
                `when`(eventServiceApi.createEvent<Contest>(any())).thenReturn(null)
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
        fragmentScenario = launchFragmentInContainer<ContestCreationFragment>(themeResId =R.style.Theme_BeGreen)
    }

    @Test
    fun testCreateContest() {
        onView(withId(R.id.contest_creation_title)).perform(typeText("Test contest"))
    }

}