package com.github.sdp_begreen.begreen.adapters

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import kotlinx.coroutines.launch

/**
 * Extension function to [RecyclerView].
 *
 * This function will add an easy way to setup the [EventsListAdapter] in the [RecyclerView].
 *
 * @param eventsFragmentViewModel The view model to use to access the events to pass as list of data to the adapter
 * @param lifecycle The lifecycle of the caller, to launch coroutine and add lifecycle to flow collection
 * @param geocodingService The geocoding service to use to instantiate the [EventDataAdapterListeners]
 * @param context The context to use to setup the [LinearLayoutManager]
 * @param getString The function to use to get the string from the resources
 */
fun <T : Event<T>, P : EventParticipant> RecyclerView.setUpEventListAdapter(
    eventsFragmentViewModel: EventsFragmentViewModel<T, P>,
    lifecycle: Lifecycle,
    geocodingService: GeocodingService,
    context: Context?,
    getString: (Int) -> String
) {
    this.layoutManager = LinearLayoutManager(context)

    this.adapter = EventsListAdapter<T>(
        EventDataAdapterListenersImpl(
            lifecycle.coroutineScope,
            eventsFragmentViewModel,
            geocodingService,
            getString
        )
    ).apply {
        lifecycle.coroutineScope.launch {
            eventsFragmentViewModel.allEvents.flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            ).collect { submitList(it) }
        }
    }
}