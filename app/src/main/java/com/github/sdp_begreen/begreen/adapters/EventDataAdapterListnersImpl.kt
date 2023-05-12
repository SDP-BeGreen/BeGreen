package com.github.sdp_begreen.begreen.adapters

import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Class that implement [EventDataAdapterListeners] to be passed to the [EventsListAdapter]
 */
class EventDataAdapterListenersImpl<T : Event<T>, K : EventParticipant>(
    private val lifecycleScope: CoroutineScope,
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

    override fun setJoinButtonListener(button: Button, eventId: String) {
        button.setOnClickListener {
            lifecycleScope.launch {
                if (eventsFragmentViewModel.participationMap.value[eventId] == true) {
                    eventsFragmentViewModel.withdraw(eventId)
                } else {

                    eventsFragmentViewModel.participate(eventId)
                }
                setJoinButtonText(button, eventId)
            }
        }
    }

    override fun setJoinButtonText(button: Button, eventId: String) {
        lifecycleScope.launch {
            // wait until the map has been retrieved to assign text
            val map = eventsFragmentViewModel.participationMap.dropWhile {
                it.isEmpty() && eventsFragmentViewModel.allEvents.value.isNotEmpty()
            }.first()

            button.text =
                if (map[eventId] == true) {
                    getString(R.string.meeting_list_join_button_withdraw)
                } else {
                    getString(R.string.meeting_list_join_button_join)
                }
        }
    }
}