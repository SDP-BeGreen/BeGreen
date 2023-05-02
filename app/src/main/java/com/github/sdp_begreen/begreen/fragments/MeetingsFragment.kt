package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.adapters.MeetingDataAdapterListeners
import com.github.sdp_begreen.begreen.adapters.MeetingsListAdapter
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.MeetingFragmentViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.IOException

/**
 * A fragment representing a list of Items.
 */
class MeetingsFragment : Fragment() {

    private val connectedUserViewModel:
            ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private val meetingFragmentViewModel by viewModels<MeetingFragmentViewModel> {
        MeetingFragmentViewModel.factory(connectedUserViewModel.currentUser)
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

                adapter = MeetingsListAdapter(MeetingDataAdapterListenersImpl()).apply {
                    lifecycleScope.launch {
                        meetingFragmentViewModel
                            .allMeetings
                            .flowWithLifecycle(
                                viewLifecycleOwner.lifecycle,
                                Lifecycle.State.STARTED
                            )
                            .collect {
                                Log.d(
                                    "Print test participation view model",
                                    "collect new list of meetings"
                                )
                                submitList(it)
                            }
                    }
                }
            }
        }
        return view
    }

    /**
     * Inner class that implement [MeetingDataAdapterListeners] to pass the the
     * [MeetingsListAdapter]
     */
    private inner class MeetingDataAdapterListenersImpl : MeetingDataAdapterListeners {
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
                    Log.d("Print test participation view model", "Clicked on button: $meetingId")
                    if (meetingFragmentViewModel.participationMap.value[meetingId] == true) {
                        meetingFragmentViewModel.withdraw(meetingId)
                    } else {
                        meetingFragmentViewModel.participate(meetingId)
                    }
                    setJoinButtonText(button, meetingId)
                }
            }
        }

        override fun setJoinButtonText(button: MaterialButton, meetingId: String) {
            Log.d("Print test participation view model", "set text for button: $meetingId")

            lifecycleScope.launch {
                // wait until the map has been retrieved to assign text
                val map = meetingFragmentViewModel.participationMap.dropWhile {
                    it.isEmpty() && meetingFragmentViewModel.allMeetings.value.isNotEmpty()
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
}