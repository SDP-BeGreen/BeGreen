package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import android.location.Address
import android.widget.Button
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.Locale
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class EventDataAdapterListenerImplTest {

    companion object {
        private val geocodingService: GeocodingService = mock(GeocodingService::class.java)
        private val eventService: EventService = mock(EventService::class.java)
        private val participantService: EventParticipantService =
            mock(EventParticipantService::class.java)
        private val coordinates = CustomLatLng(1.0, 2.0)
        private val initialUser = User("123456", 10, "User 1")
        private val connectedUser = MutableStateFlow(initialUser)

        @BeforeClass
        @JvmStatic
        fun setUpMock() {
            runTest {
                whenever(geocodingService.getAddresses(coordinates, 1)).thenReturn(
                    listOf(Address(Locale.FRENCH).apply { locality = "Lausanne" })
                )

                whenever(eventService.getAllEvents(RootPath.CONTESTS, Contest::class.java))
                    .thenReturn(
                        flowOf(
                            listOf(
                                Contest(
                                    "contest1",
                                    "creator1",
                                    "first contest",
                                    "some description",
                                    1683885151000,
                                    1686563551000,
                                    coordinates,
                                    1000,
                                    false
                                )
                            )
                        )
                    )
                whenever(
                    participantService.getAllParticipants(
                        RootPath.CONTESTS,
                        "contest1",
                        ContestParticipant::class.java
                    )
                ).thenReturn(flowOf(listOf(ContestParticipant("participant 1", 10))))
            }
        }
    }

    @get:Rule
    val coroutineRules = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { geocodingService }
            single { eventService }
            single { participantService }
        })
    )

    private lateinit var eventDataAdapterListenersImpl: EventDataAdapterListenersImpl<Contest, ContestParticipant>
    private lateinit var eventsFragmentViewModel: EventsFragmentViewModel<Contest, ContestParticipant>
    private lateinit var context: Context

    private val scope = TestScope()

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        connectedUser.tryEmit(initialUser)
        eventsFragmentViewModel = EventsFragmentViewModel(
            connectedUser,
            RootPath.CONTESTS,
            Contest::class.java,
            ContestParticipant::class.java
        )
        eventDataAdapterListenersImpl = EventDataAdapterListenersImpl(
            scope,
            eventsFragmentViewModel,
            geocodingService
        ) { if (it == R.string.event_list_join_button_withdraw) "Withdraw" else "Join" }
    }

    @Test
    fun setAddressToTextViewFromCoordinatesCorrectlySetText() {
        val textViewModel = TextView(context)

        eventDataAdapterListenersImpl.setAddressToTextViewFromCoordinates(
            coordinates,
            textViewModel
        )
        scope.advanceUntilIdle()
        assertThat(textViewModel.text.toString(), `is`(equalTo("Lausanne")))
    }

    @Test
    fun setJoinButtonListenerCorrectlySetListener() {
        val button = Button(context)

        assertFalse(button.hasOnClickListeners())
        eventDataAdapterListenersImpl.setJoinButtonListener(button, "contest1")
        scope.advanceUntilIdle()
        assertTrue(button.hasOnClickListeners())
    }

    @Test
    fun setJoinButtonTextCorrectlySetTextNotYetJoinedContest() {
        val button = Button(context)

        runTest {
            eventDataAdapterListenersImpl.setJoinButtonText(button, "contest1")
            scope.advanceUntilIdle()

            assertThat(button.text.toString(), `is`(equalTo("Join")))
        }
    }

    @Test
    fun setJoinButtonTextCorrectlySetTextJoinedContest() {
        val button = Button(context)

        connectedUser.tryEmit(User("participant 1", 1, "p1"))

        eventDataAdapterListenersImpl.setJoinButtonText(button, "contest1")
        scope.advanceUntilIdle()

        assertThat(button.text.toString(), `is`(equalTo("Withdraw")))
    }
}