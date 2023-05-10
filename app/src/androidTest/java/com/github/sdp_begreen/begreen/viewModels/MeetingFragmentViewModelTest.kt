package com.github.sdp_begreen.begreen.viewModels

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.Meeting
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTestRule
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class MeetingFragmentViewModelTest {

    companion object {
        private val eventService: EventService = mock(EventService::class.java)
        private val participantService: MeetingParticipantService =
            mock(MeetingParticipantService::class.java)

        private val initiallyConnectedUser = User("123456", 10, "User 1")
        private val currentUser: MutableStateFlow<User?> = MutableStateFlow(initiallyConnectedUser)

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
        )

        private val meetingsFlow = MutableStateFlow(meetings)

        // supposed to represent a Channel where both add participant and
        // remove participant can be send through
        val addRemoveParticipantChannel = Channel<String>(1)

        @BeforeClass
        @JvmStatic
        fun setUpAuth() {
            runTest {

                // Initial setup of getAllMeetings
                `when`(eventService.getAllEvents(RootPath.MEETINGS, Meeting::class.java)).thenReturn(meetingsFlow)

                // setup getAllParticipants for initials meetings
                `when`(participantService.getAllParticipants(meetings[0].id!!)).thenReturn(
                    flowOf(listOf("aaaaaa", "bbbbbb", "cccccc"))
                )
                `when`(participantService.getAllParticipants(meetings[1].id!!)).thenReturn(
                    flowOf(listOf("dddddd"))
                )
                `when`(participantService.getAllParticipants(meetings[2].id!!)).thenReturn(
                    flowOf(listOf("aaaaaa", "123456"))
                )

                `when`(
                    participantService.addParticipant(
                        meetings[1].id!!,
                        initiallyConnectedUser.id
                    )
                ).then {
                    addRemoveParticipantChannel.trySend(it.arguments[1] as String)
                }

                `when`(
                    participantService.removeParticipant(
                        meetings[2].id!!,
                        initiallyConnectedUser.id
                    )
                ).then {
                    addRemoveParticipantChannel.trySend(it.arguments[1] as String)
                }
            }
        }
    }

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single { eventService }
            single { participantService }
        })
    }

    @Before
    fun setUpVM() {
        // reset user meetings and user
        meetingsFlow.tryEmit(meetings)
        currentUser.tryEmit(initiallyConnectedUser)
        meetingFragmentViewModel = MeetingFragmentViewModel(currentUser)
    }

    private lateinit var meetingFragmentViewModel: MeetingFragmentViewModel


    @Test
    fun allMeetingsReturnCorrectFlowOfMeetings() {
        runTest {
            val channel = Channel<List<Meeting>>(1)
            backgroundScope.launch {
                meetingFragmentViewModel.allMeetings.collect {
                    channel.send(it)
                }
            }

            // assert correct meetings initially
            assertThat(channel.receive(), `is`(equalTo(meetings)))

            val newMeetings = listOf(
                meetings[0],
                meetings[1].copy(description = "Modified second meeting", title = "Clean School"),
                meetings[2]
            )
            // modify meeting
            meetingsFlow.emit(newMeetings)

            assertThat(channel.receive(), `is`(equalTo(newMeetings)))
        }
    }

    @Test
    fun participationMapCorrectlyUpdateUponCurrentUserChange() {
        runTest {
            val channel = Channel<Map<String, Boolean>>(1)
            backgroundScope.launch {
                meetingFragmentViewModel.participationMap.collect {
                    channel.send(it)
                }
            }

            // assert that it should initially be
            val initialMap = mapOf(
                meetings[0].id!! to false,
                meetings[1].id!! to false,
                meetings[2].id!! to true,
            )
            assertThat(channel.receive(), `is`(equalTo(initialMap)))

            // modify connected user
            currentUser.emit(User("aaaaaa", 12, "User 2"))
            val map2 = mapOf(
                meetings[0].id!! to true,
                meetings[1].id!! to false,
                meetings[2].id!! to true,
            )
            assertThat(channel.receive(), `is`(equalTo(map2)))

            // modify connected user
            currentUser.emit(User("dddddd", 16, "User 3"))
            val map3 = mapOf(
                meetings[0].id!! to false,
                meetings[1].id!! to true,
                meetings[2].id!! to false,
            )
            assertThat(channel.receive(), `is`(equalTo(map3)))
        }
    }

    @Test
    fun participationMapCorrectlyUpdateUponMeetingsChange() {
        val newMeeting1 = meetings + Meeting(
            "m4",
            "Kim",
            "River Cleanup",
            "Let's meet up and clean up the local river",
            1723464000000,
            1723485600000,
            CustomLatLng(46.519075, 6.561628),
            CustomLatLng(46.519075, 6.561628)
        )

        val newMeeting2 = newMeeting1 - meetings[2]

        val meetingFragmentViewModel = MeetingFragmentViewModel(currentUser)

        runTest {
            // mock new meeting
            backgroundScope.launch {
                `when`(participantService.getAllParticipants(newMeeting1[3].id!!)).thenReturn(
                    flowOf(listOf("cccccc", "123456"))
                )
            }

            val channel = Channel<Map<String, Boolean>>(1)
            backgroundScope.launch {
                meetingFragmentViewModel.participationMap.collect {
                    channel.send(it)
                }
            }

            // assert that it should initially be
            val initialMap = mapOf(
                meetings[0].id!! to false,
                meetings[1].id!! to false,
                meetings[2].id!! to true,
            )
            assertThat(channel.receive(), `is`(equalTo(initialMap)))

            meetingsFlow.emit(newMeeting1)
            val map2 = mapOf(
                newMeeting1[0].id!! to false,
                newMeeting1[1].id!! to false,
                newMeeting1[2].id!! to true,
                newMeeting1[3].id!! to true,
            )
            assertThat(channel.receive(), `is`(equalTo(map2)))

            meetingsFlow.emit(newMeeting2)
            val map3 = mapOf(
                newMeeting2[0].id!! to false,
                newMeeting2[1].id!! to false,
                newMeeting2[2].id!! to true,
            )
            assertThat(channel.receive(), `is`(equalTo(map3)))

        }
    }

    @Test
    fun addParticipantCorrectlyAddParticipantAndUpdateMap() {
        runTest {

            val channel = Channel<Map<String, Boolean>>(1)

            backgroundScope.launch {
                meetingFragmentViewModel.participationMap.collect {
                    channel.send(it)
                }
            }

            assertThat(
                meetingFragmentViewModel.participate(meetings[1].id!!),
                `is`(equalTo(meetings[1].id!!))
            )

            assertThat(
                addRemoveParticipantChannel.receive(),
                `is`(equalTo(initiallyConnectedUser.id))
            )

            val mapAfterAddParticipation = mapOf(
                meetings[0].id!! to false,
                meetings[1].id!! to true,
                meetings[2].id!! to true,
            )

            assertThat(channel.receive(), `is`(equalTo(mapAfterAddParticipation)))
        }
    }

    @Test
    fun removeParticipantCorrectlyRemoveParticipantAndUpdateMap() {
        runTest {

            val channel = Channel<Map<String, Boolean>>(1)

            backgroundScope.launch {
                meetingFragmentViewModel.participationMap.collect {
                    channel.send(it)
                }
            }

            assertThat(meetingFragmentViewModel.withdraw(meetings[2].id!!), `is`(equalTo(meetings[2].id!!)))

            assertThat(
                addRemoveParticipantChannel.receive(),
                `is`(equalTo(initiallyConnectedUser.id))
            )

            val mapAfterRemoveParticipation = mapOf(
                meetings[0].id!! to false,
                meetings[1].id!! to false,
                meetings[2].id!! to false,
            )

            assertThat(channel.receive(), `is`(equalTo(mapAfterRemoveParticipation)))
        }
    }

    @Test
    fun addParticipantNonConnectedUserShouldReturnNull() {
        runTest {

            currentUser.emit(null) // simulate no connected user
            assertThat(meetingFragmentViewModel.participate(meetings[1].id!!), `is`(nullValue()))
        }
    }

    @Test
    fun removeParticipantNonConnectedUserShouldReturnNull() {
        runTest {

            currentUser.emit(null) // simulate no connected user
            assertThat(meetingFragmentViewModel.withdraw(meetings[1].id!!), `is`(nullValue()))
        }
    }

    private fun registerCallbackAndSendThroughChannelWhenReceiveNewValue() {
        // TODO continue
    }

}