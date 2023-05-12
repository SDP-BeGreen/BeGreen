package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.adapters.EventDataAdapterListenersImpl
import com.github.sdp_begreen.begreen.adapters.EventsListAdapter
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.models.event.MeetingParticipant
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A fragment representing a list of meetings.
 */
class MeetingsFragment : Fragment() {

    private val connectedUserViewModel:
            ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private val eventsFragmentViewModel by viewModels<EventsFragmentViewModel<Meeting, MeetingParticipant>> {
        EventsFragmentViewModel.factory(
            connectedUserViewModel.currentUser,
            RootPath.MEETINGS,
            Meeting::class.java,
            MeetingParticipant::class.java
        )
    }
    private val geocodingApi by inject<GeocodingService>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meetings_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)

                adapter = EventsListAdapter<Meeting>(
                    EventDataAdapterListenersImpl(
                        lifecycleScope,
                        eventsFragmentViewModel,
                        geocodingApi
                    ) { getString(it) }).apply {
                    lifecycleScope.launch {
                        eventsFragmentViewModel
                            .allEvents
                            .flowWithLifecycle(
                                viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
                            ).collect {
                                submitList(it)
                            }
                    }
                }
            }
        }
        return view
    }
}