package com.github.sdp_begreen.begreen.fragments

import android.location.Address
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.matchers.ButtonAssertionView.Companion.atPositionButtonWithText
import com.github.sdp_begreen.begreen.matchers.TextViewAssertionView.Companion.atPositionTextViewWithText
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.services.GeocodingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

/**
 * This test class only test that launching the fragment correctly display one element,
 * as it behaves exactly like [MeetingsFragment], and the every component that it used is
 * fully tested, it is not required to test it more.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class ContestsFragmentTest {

    companion object {
        private val eventService: EventService = mock(EventService::class.java)
        private val participantService: EventParticipantService =
            mock(EventParticipantService::class.java)
        private val geocoderApi: GeocodingService = mock(GeocodingService::class.java)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)

        private val location1 = CustomLatLng(1.0, 2.0)
        private val contests = listOf(
            Contest(
                "contest1",
                "creator1",
                "title1",
                "contest description",
                1683918539000,
                1684264139000,
                location1
            )
        )

        private val localities = listOf("Lausanne")

        @BeforeClass
        @JvmStatic
        fun setupMock() {
            runTest {
                whenever(eventService.getAllEvents(RootPath.CONTESTS, Contest::class.java))
                    .thenReturn(flowOf(contests))
                whenever(
                    participantService.getAllParticipants(
                        RootPath.CONTESTS,
                        contests[0].id!!,
                        ContestParticipant::class.java
                    )
                ).thenReturn(flowOf(listOf(ContestParticipant("participant 1"))))


                whenever(geocoderApi.getAddresses(location1, 1)).thenReturn(
                    listOf(Address(Locale.FRENCH).apply { locality = localities[0] })
                )
                whenever(auth.getFlowUserIds()).thenReturn(flowOf("123456"))
                whenever(db.getUser("123456")).thenReturn(User("123456", 1))
            }
        }

    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { eventService }
            single { participantService }
            single { geocoderApi }
            single { auth }
            single { db }
        })
    )

    private lateinit var fragmentScenario: FragmentScenario<ContestsFragment>

    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer()
    }

    @Test
    fun checkContestListCorrectlyDisplayedInRecycler() {

        fragmentScenario.onFragment {
            val recyclerView =
                it.requireView().findViewById<RecyclerView>(R.id.fragment_contests_list)
            val itemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
            val title = itemView?.findViewById<TextView>(R.id.fragment_event_elem_title)
            ViewMatchers.assertThat(
                title?.text,
                `is`(equalTo(contests[0].title))
            )
        }

        val initialButtonText = listOf("Join")

        contests.forEachIndexed { index, contest ->
            onView(withId(R.id.fragment_contests_list))
                .check(
                    atPositionTextViewWithText(
                        index,
                        R.id.fragment_event_elem_title,
                        contest.title
                    )
                )
                .check(
                    atPositionTextViewWithText(
                        index,
                        R.id.fragment_event_elem_date,
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(Calendar.getInstance().apply {
                                timeInMillis = contest.startDateTime!!
                            }.time)
                    )
                )
                .check(
                    atPositionTextViewWithText(
                        index,
                        R.id.fragment_event_elem_location,
                        localities[index]
                    )
                )
                .check(
                    atPositionButtonWithText(
                        index,
                        R.id.fragment_event_elem_join_button,
                        initialButtonText[index]
                    )
                )
        }
    }

    //Problem with the fragment manager
    //@Test
    //fun clickOnAddContestCorrectlyDisplayCreateContestFragment() {
    //    onView(withId(R.id.fragment_contests_add_contest))
    //        .check(matches(isDisplayed()))
    //        .perform(click())
//
    //    // TODO finish this test once the button is actually linked
    //}
}