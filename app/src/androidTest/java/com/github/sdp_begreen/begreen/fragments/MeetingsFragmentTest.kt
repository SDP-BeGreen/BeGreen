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
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.firebase.meetingServices.EventParticipantService
import com.github.sdp_begreen.begreen.matchers.ButtonAssertionView.Companion.atPositionButtonWithText
import com.github.sdp_begreen.begreen.matchers.ButtonClickAction.Companion.clickButtonIdAtPosition
import com.github.sdp_begreen.begreen.matchers.TextViewAssertionView.Companion.atPositionTextViewWithText
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.models.event.MeetingParticipant
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


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class MeetingsFragmentTest {

    companion object {
        private val eventService: EventService = mock(EventService::class.java)
        private val participantService: EventParticipantService =
            mock(EventParticipantService::class.java)
        private val geocoderApi: GeocodingService = mock(GeocodingService::class.java)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)

        private val initiallyConnectedUser = User("123456", 10, "User 1")

        private val latLngMeeting1 = CustomLatLng(37.7749, -122.4194)
        private val latLngMeeting2 = CustomLatLng(37.7739, -122.4312)
        private val latLngMeeting3 = CustomLatLng(46.517355, 6.628854)
        private val latLngMeeting4 = CustomLatLng(0.0, 0.0)
        private val latLngNewMeeting = CustomLatLng(46.806832, 7.156354)


        private val meetings = listOf(
            Meeting(
                "m1",
                "John",
                "Downtown Cleanup",
                "Let's meet up and clean up the downtown area",
                1718445600000,
                1718467200000,
                latLngMeeting1,
                latLngMeeting1
            ),
            Meeting(
                "m2",
                "Sarah",
                "Park Cleanup",
                "Let's meet up and clean up the local park",
                1718186400000,
                1718208000000,
                latLngMeeting2,
                latLngMeeting2
            ),
            Meeting(
                "m3",
                "Tom",
                "Beach Cleanup",
                "Let's meet up and clean up the local beach",
                1710237600000,
                1710252000000,
                latLngMeeting3,
                latLngMeeting3
            ),
            Meeting(
                "m4",
                "Andree",
                "School Cleanup",
                "Let's meet up and clean up the local school",
                1710237600000,
                1710238600000,
                latLngMeeting4,
                latLngMeeting4
            )
        )

        private val meetingsFlow = MutableStateFlow(meetings)
        private val localities = listOf("Paris", "Berlin", "London", "")

        // supposed to represent a Channel where both add participant and
        // remove participant can be send through
        val addRemoveParticipantChannel = Channel<String>(1)

        @BeforeClass
        @JvmStatic
        fun setUpAuth() {
            runTest {

                // Initial setup of getAllMeetings
                `when`(eventService.getAllEvents(RootPath.MEETINGS, Meeting::class.java))
                    .thenReturn(meetingsFlow)

                // setup getAllParticipants for initials meetings
                `when`(
                    participantService.getAllParticipants(
                        meetings[0].id!!,
                        MeetingParticipant::class.java
                    )
                )
                    .thenReturn(
                        flowOf(
                            listOf(
                                MeetingParticipant("aaaaaa"),
                                MeetingParticipant("bbbbbb"),
                                MeetingParticipant("cccccc")
                            )
                        )
                    )
                `when`(
                    participantService.getAllParticipants(
                        meetings[1].id!!,
                        MeetingParticipant::class.java
                    )
                ).thenReturn(flowOf(listOf(MeetingParticipant("dddddd"))))
                `when`(
                    participantService.getAllParticipants(
                        meetings[2].id!!,
                        MeetingParticipant::class.java
                    )
                ).thenReturn(
                    flowOf(
                        listOf(
                            MeetingParticipant("aaaaaa"),
                            MeetingParticipant("123456")
                        )
                    )
                )
                `when`(
                    participantService.getAllParticipants(
                        meetings[3].id!!,
                        MeetingParticipant::class.java
                    )
                )
                    .thenReturn(flowOf(listOf(MeetingParticipant("iiiiii"))))
                `when`(participantService.getAllParticipants("m5", MeetingParticipant::class.java))
                    .thenReturn(flowOf(listOf(MeetingParticipant("jjjjjj"))))

                // setup geocoding
                `when`(geocoderApi.getAddresses(latLngMeeting1, 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = localities[0]
                    }))
                `when`(geocoderApi.getAddresses(latLngMeeting2, 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = localities[1]
                    }))
                `when`(geocoderApi.getAddresses(latLngMeeting3, 1))
                    .thenReturn(mutableListOf(Address(Locale.FRENCH).apply {
                        locality = localities[2]
                    }))
                `when`(geocoderApi.getAddresses(latLngMeeting4, 1))
                    .thenThrow(IOException()) // check that if exception thrown then empty string should be displayed
                `when`(geocoderApi.getAddresses(latLngNewMeeting, 1))
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
                        meetings[1].id!!,
                        MeetingParticipant(initiallyConnectedUser.id)
                    )
                ).then {
                    addRemoveParticipantChannel.trySend("participate: ${it.arguments[1] as MeetingParticipant}")
                }

                `when`(
                    participantService.removeParticipant(
                        meetings[1].id!!,
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
            single { eventService }
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
            val recyclerView =
                it.requireView().findViewById<RecyclerView>(R.id.fragment_meeting_list)
            val itemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
            val title = itemView?.findViewById<TextView>(R.id.fragment_meeting_elem_title)
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
                `is`(equalTo("participate: ${MeetingParticipant(initiallyConnectedUser.id)}"))
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
    fun adapterContainsCorrectListWhenAddingNewList() {
        runTest {
            val newMeeting = Meeting(
                "m5",
                "Alex",
                "Forest Cleanup",
                "Let's meet up and clean up the local forest",
                1731139200000,
                1731150000000,
                latLngNewMeeting,
                latLngNewMeeting
            )

            fragmentScenario.onFragment {
                val recyclerView = it.view as RecyclerView
                val adapter = recyclerView.adapter as ListAdapter<Meeting, *>

                // assert initial list
                assertThat(adapter.currentList, `is`(equalTo(meetings)))

                adapter.submitList(meetings + newMeeting) {
                    assertThat(adapter.currentList, `is`(equalTo(meetings + newMeeting)))
                }
            }
        }

    }
}