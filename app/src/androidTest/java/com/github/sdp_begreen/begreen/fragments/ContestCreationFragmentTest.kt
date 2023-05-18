package com.github.sdp_begreen.begreen.fragments

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.viewModels.ContestCreationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


@RunWith(AndroidJUnit4::class)
@LargeTest
class ContestCreationFragmentTest {

    companion object {
        private val eventServiceApi: EventService = mock()
        private lateinit var ccvm: ContestCreationViewModel

        @Before
        fun setUpVM() {
            ccvm = ContestCreationViewModel()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @BeforeClass
        fun setUpEventService(): Unit {
            //runTest {
            //    // Initial setup of getAllMeetings
            //    `when`(eventServiceApi.createEvent<Contest>>(any())).thenReturn(null)
            //}
        }
    }
}