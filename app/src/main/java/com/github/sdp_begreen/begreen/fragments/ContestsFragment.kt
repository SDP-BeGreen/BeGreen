package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.util.Log
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
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A fragment representing a list of contests.
 */
class ContestsFragment : Fragment() {

    private val connectedUserViewModel:
            ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private val eventsFragmentViewModel by viewModels<EventsFragmentViewModel<Contest, ContestParticipant>> {
        EventsFragmentViewModel.factory(
            connectedUserViewModel.currentUser,
            RootPath.CONTESTS,
            Contest::class.java,
            ContestParticipant::class.java
        )
    }
    private val geocodingApi by inject<GeocodingService>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contests_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_contests_list)
        val addContestButton = view.findViewById<MaterialButton>(R.id.fragment_contests_add_contest)

        setUpAddContestButton(addContestButton)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)

            adapter = EventsListAdapter<Contest>(
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
        return view
    }

    /**
     * Helper function to setup the add contest button
     *
     * @param btn The button to setup
     */
    private fun setUpAddContestButton(btn: MaterialButton) {
        btn.setOnClickListener {
            // TODO link with given fragment once done implementing it, and remove log
            Log.d("Temp tag add contest button", "Button correctly pressed")
        }
    }


}