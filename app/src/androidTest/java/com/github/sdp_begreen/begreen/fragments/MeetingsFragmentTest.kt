package com.github.sdp_begreen.begreen.fragments

import android.location.Address
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingParticipantService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingService
import com.github.sdp_begreen.begreen.matchers.ButtonAssertionView.Companion.atPositionButtonWithText
import com.github.sdp_begreen.begreen.matchers.ButtonClickAction.Companion.clickButtonIdAtPosition
import com.github.sdp_begreen.begreen.matchers.TextViewAssertionView.Companion.atPositionTextViewWithText
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.services.GeocodingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.IOException
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class MeetingsFragmentTest {

    companion object {
        private val meetingService: MeetingService = mock(MeetingService::class.java)
        private val participantService: MeetingParticipantService =
            mock(MeetingParticipantService::class.java)
        private val geocoderApi: GeocodingService = mock(GeocodingService::class.java)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)

        private val initiallyConnectedUser = User("123456", 10, "User 1")

        private val meetings = listOf(
            Meeting(
                "m1",
                "John",
                "Downtown Cleanup",
                "Let's meet up and clean up the downtown area",
                1718445600000,
                1718467200000,
                CustomLatLng(37.7749, -122.4194),
                CustomLatLng(37.7749, -122.4194)
            ),
            Meeting(
                "m2",
                "Sarah",
                "Park Cleanup",
                "Let's meet up and clean up the local park",
                1718186400000,
                1718208000000,
                CustomLatLng(37.7739, -122.4312),
                CustomLatLng(37.7739, -122.4312)
            ),
            Meeting(
                "m3",
                "Tom",
                "Beach Cleanup",
                "Let's meet up and clean up the local beach",
                1710237600000,
                1710252000000,
                CustomLatLng(46.517355, 6.628854),
                CustomLatLng(46.517355, 6.628854)
            ),
            Meeting(
                "m4",
                "Andree",
                "School Cleanup",
                "Let's meet up and clean up the local school",
                1710237600000,
                1710238600000,
                CustomLatLng(0.0, 0.0),
                CustomLatLng(0.0, 0.0),
            )
        )

        private val meetingsFlow = MutableStateFlow(meetings)
        private val localities = listOf("Paris", "Berlin", "London", "")

        val addRemoveParticipantChannel = Channel<String>(1)

        @BeforeClass
        @JvmStatic
        fun setUpAuth() {
            runTest {

                // Initial setup of getAllMeetings
                `when`(meetingService.getAllMeetings())
                    .thenReturn(meetingsFlow)

                // setup getAllParticipants for initials meetings
                `when`(participantService.getAllParticipants(meetings[0].meetingId!!))
                    .thenReturn(flowOf(listOf("aaaaaa", "bbbbbb", "cccccc")))
                `when`(participantService.getAllParticipants(meetings[1].meetingId!!))
                    .thenReturn(flowOf(listOf("dddddd")))
                `when`(participantService.getAllParticipants(meetings[2].meetingId!!))
                    .thenReturn(flowOf(listOf("aaaaaa", "123456")))
                `when`(participantService.getAllParticipants(meetings[3].meetingId!!))
                    .thenReturn(flowOf(listOf("iiiiii")))
                `when`(participantService.getAllParticipants("m5"))
                    .thenReturn(flowOf(listOf("jjjjjj")))

                // setup geocoding
                `when`(geocoderApi.getAddresses(CustomLatLng(37.7749, -122.4194), 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = localities[0]
                    }))
                `when`(geocoderApi.getAddresses(CustomLatLng(37.7739, -122.4312), 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = localities[1]
                    }))
                `when`(geocoderApi.getAddresses(CustomLatLng(46.517355, 6.628854), 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = localities[2]
                    }))
                `when`(geocoderApi.getAddresses(CustomLatLng(0.0, 0.0), 1))
                    .thenThrow(IOException()) // check that if exception thrown then empty string should be displayed
                `when`(geocoderApi.getAddresses(CustomLatLng(46.806832, 7.156354), 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = "Lausanne"
                    }))

                // setup connected user
                `when`(auth.getFlowUserIds())
                    .thenReturn(MutableStateFlow(initiallyConnectedUser.id))
                `when`(db.getUser(initiallyConnectedUser.id))
                    .thenReturn(initiallyConnectedUser)

                // setup participate and withdraw
                `when`(
                    participantService.addParticipant(
                        meetings[1].meetingId!!,
                        initiallyConnectedUser.id
                    )
                ).then {
                    addRemoveParticipantChannel.trySend("participate: ${it.arguments[1] as String}")
                }

                `when`(
                    participantService.removeParticipant(
                        meetings[1].meetingId!!,
                        initiallyConnectedUser.id
                    )
                ).then {
                    addRemoveParticipantChannel.trySend("withdraw: ${it.arguments[1] as String}")
                }
            }
        }
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { meetingService }
            single { participantService }
            single { geocoderApi }
            single { auth }
            single { db }
        })
    )

    private lateinit var fragmentScenario: FragmentScenario<MeetingsFragment>

    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer()
        meetingsFlow.tryEmit(meetings)
    }

    @Test
    fun checkRecyclerViewIsVisibleWhenFragmentLaunched() {
        onView(withId(R.id.fragment_meeting_list))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkMeetingListCorrectlyDisplayedInRecycler() {

        fragmentScenario.onFragment {
            val view = it.requireView().findViewById<RecyclerView>(R.id.fragment_meeting_list)
            val v = view.findViewHolderForAdapterPosition(0)?.itemView
            val title = v?.findViewById<TextView>(R.id.fragment_meeting_elem_title)
            assertThat(title?.text, `is`(equalTo(meetings[0].title)))
        }

        val initialButtonText = listOf("Join", "Join", "Withdraw", "Join")

        meetings.forEachIndexed { index, meeting ->
            onView(withId(R.id.fragment_meeting_list))
                .check(
                    atPositionTextViewWithText(
                        index,
                        R.id.fragment_meeting_elem_title,
                        meeting.title
                    )
                )
                .check(
                    atPositionTextViewWithText(
                        index,
                        R.id.fragment_meeting_elem_date,
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(Calendar.getInstance().apply {
                                timeInMillis = meeting.startDateTime!!
                            }.time)
                    )
                )
                .check(
                    atPositionTextViewWithText(
                        index,
                        R.id.fragment_meeting_elem_location,
                        localities[index]
                    )
                )
                .check(
                    atPositionButtonWithText(
                        index,
                        R.id.fragment_meeting_elem_join_button,
                        initialButtonText[index]
                    )
                )
        }
    }

    @Test
    fun clickJoinButtonOnMeetingElemCorrectlyJoinMeetingAndChangeDisplayedText() {
        onView(withId(R.id.fragment_meeting_list))
            .check(
                atPositionButtonWithText(
                    1,
                    R.id.fragment_meeting_elem_join_button,
                    "Join"
                )
            )
            .perform(clickButtonIdAtPosition(1, R.id.fragment_meeting_elem_join_button))
            .check(
                atPositionButtonWithText(
                    1,
                    R.id.fragment_meeting_elem_join_button,
                    "Withdraw"
                )
            )
    }

    @Test
    fun clickJoinButtonOnMeetingAddUserToDatabase() {
        runTest {
            // click to participate
            onView(withId(R.id.fragment_meeting_list))
                .perform(clickButtonIdAtPosition(1, R.id.fragment_meeting_elem_join_button))

            assertThat(
                addRemoveParticipantChannel.receive(),
                `is`(equalTo("participate: ${initiallyConnectedUser.id}"))
            )

            // click to withdraw
            onView(withId(R.id.fragment_meeting_list))
                .perform(clickButtonIdAtPosition(1, R.id.fragment_meeting_elem_join_button))

            assertThat(
                addRemoveParticipantChannel.receive(),
                `is`(equalTo("withdraw: ${initiallyConnectedUser.id}"))
            )
        }
    }

    @Test
    fun addNewMeetingCorrectlyDisplayIt() {
        runTest {
            val newMeeting = Meeting(
                "m5",
                "Alex",
                "Forest Cleanup",
                "Let's meet up and clean up the local forest",
                1731139200000,
                1731150000000,
                CustomLatLng(46.806832, 7.156354),
                CustomLatLng(46.806832, 7.156354)
            )

            val channel = Channel<Boolean>(1)

            fragmentScenario.onFragment {
                val recyclerView = it.view as RecyclerView
                val adapter = recyclerView.adapter as ListAdapter<Meeting, *>

                // assert initial list
                assertThat(adapter.currentList, `is`(equalTo(meetings)))

                adapter.submitList(meetings + newMeeting) {
                    assertThat(adapter.currentList, `is`(equalTo(meetings + newMeeting)))
                    // once the list has been added send it through channel
                    channel.trySend(true)
                }
            }

            // block until we receive that the list has been committed
            assertTrue(channel.receive())

            // assert the newly added element
            onView(withId(R.id.fragment_meeting_list))
                .check(
                    atPositionTextViewWithText(
                        4,
                        R.id.fragment_meeting_elem_title,
                        newMeeting.title
                    )
                )
                .check(
                    atPositionTextViewWithText(
                        4,
                        R.id.fragment_meeting_elem_date,
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(Calendar.getInstance().apply {
                                timeInMillis = newMeeting.startDateTime!!
                            }.time)
                    )
                )
                .check(
                    atPositionTextViewWithText(
                        4,
                        R.id.fragment_meeting_elem_location,
                        "Lausanne"
                    )
                )
                .check(
                    atPositionButtonWithText(
                        4,
                        R.id.fragment_meeting_elem_join_button,
                        "Join"
                    )
                )
        }

    }
}