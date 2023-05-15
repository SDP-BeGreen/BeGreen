package com.github.sdp_begreen.begreen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.databinding.FragmentEventListElemBinding
import com.github.sdp_begreen.begreen.models.event.Event
import java.text.DateFormat
import java.util.Calendar

/**
 * Adapter class to represent the event list
 *
 * @param eventDataAdapterListeners An implementation of the interface [EventDataAdapterListeners]
 * that provide the required method for this adapter to correctly display the [Event] elements
 */
class EventsListAdapter<T: Event<T>>(
    private val eventDataAdapterListeners: EventDataAdapterListeners,
) : ListAdapter<T, EventsListAdapter<T>.ViewHolder>(DiffEvent<T>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentEventListElemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.titleView.text = event.title
        event.startDateTime?.also {
            val calendarTime = Calendar.getInstance().apply {
                timeInMillis = it
            }
            holder.dateView.text =
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(calendarTime.time)
        }

        event.startCoordinates?.also {
            eventDataAdapterListeners.setAddressToTextViewFromCoordinates(
                it, holder.locationView
            )
        }

        event.id?.also {
            eventDataAdapterListeners.setJoinButtonText(holder.joinButton, it)
            eventDataAdapterListeners.setJoinButtonListener(holder.joinButton, it)
        }

    }

    /**
     * ViewHolder class for this ListAdapter
     */
    inner class ViewHolder(binding: FragmentEventListElemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val titleView = binding.fragmentEventElemTitle
        val dateView = binding.fragmentEventElemDate
        val locationView = binding.fragmentEventElemLocation
        val joinButton = binding.fragmentEventElemJoinButton
    }

    /**
     * private class to tell how to compare events to compute diff
     */
    private class DiffEvent<T: Event<T>> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: T, newItem: T) =
            oldItem == newItem
    }
}