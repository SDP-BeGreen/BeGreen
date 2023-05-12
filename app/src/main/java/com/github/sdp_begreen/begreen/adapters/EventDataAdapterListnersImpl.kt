package com.github.sdp_begreen.begreen.adapters

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Class that implement [EventDataAdapterListeners] to pass to the [EventsListAdapter]
 */
class EventDataAdapterListenersImpl<T : Event<T>, K : EventParticipant>(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val eventsFragmentViewModel: EventsFragmentViewModel<T, K>,
    private val geocodingApi: GeocodingService,
    private val getString: (Int) -> String
) :
    EventDataAdapterListeners {
    // We have to use the synchronous `getFromLocation` as we support older API,
    // so launch it in a coroutine, to avoid blocking the main thread.
    override fun setAddressToTextViewFromCoordinates(
        coordinates: CustomLatLng,
        textView: TextView
    ) {
        lifecycleScope.launch {
            try {
                val addresses = geocodingApi.getAddresses(coordinates, 1)
                textView.text = addresses?.first()?.locality
            } catch (ioException: IOException) {
                Log.d(
                    "Meetings Recycler view",
                    "Error while trying to find the address form location " +
                            ioException.message.orEmpty()
                )
            }
        }
    }

    override fun setJoinButtonListener(button: MaterialButton, meetingId: String) {
        button.setOnClickListener {
            lifecycleScope.launch {
                if (eventsFragmentViewModel.participationMap.value[meetingId] == true) {
                    eventsFragmentViewModel.withdraw(meetingId)
                } else {
                    eventsFragmentViewModel.participate(meetingId)
                }
                setJoinButtonText(button, meetingId)
            }
        }
    }

    override fun setJoinButtonText(button: MaterialButton, meetingId: String) {
        lifecycleScope.launch {
            // wait until the map has been retrieved to assign text
            val map = eventsFragmentViewModel.participationMap.dropWhile {
                it.isEmpty() && eventsFragmentViewModel.allEvents.value.isNotEmpty()
            }.first()

            button.text =
                if (map[meetingId] == true) {
                    getString(R.string.meeting_list_join_button_withdraw)
                } else {
                    getString(R.string.meeting_list_join_button_join)
                }
        }
    }
}