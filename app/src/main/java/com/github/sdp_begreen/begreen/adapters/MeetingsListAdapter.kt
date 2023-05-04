package com.github.sdp_begreen.begreen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.databinding.FragmentMeetingElemBinding
import com.github.sdp_begreen.begreen.models.Meeting
import java.text.DateFormat
import java.util.Calendar

/**
 * Adapter class to represent the meeting list
 *
 * @param meetingDataAdapterListeners An implementation of the interface [MeetingDataAdapterListeners]
 * that provide the required method for this adapter to correctly display the [Meeting] elements
 */
class MeetingsListAdapter(
    private val meetingDataAdapterListeners: MeetingDataAdapterListeners
) : ListAdapter<Meeting, MeetingsListAdapter.ViewHolder>(DiffMeeting) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentMeetingElemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meeting = getItem(position)
        holder.titleView.text = meeting.title
        meeting.startDateTime?.also {
            val calendarTime = Calendar.getInstance().apply {
                timeInMillis = it
            }
            holder.dateView.text =
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(calendarTime.time)
        }

        meeting.startCoordinates?.also {
            meetingDataAdapterListeners.setAddressToTextViewFromCoordinates(
                it, holder.locationView
            )
        }

        meeting.meetingId?.also {
            meetingDataAdapterListeners.setJoinButtonText(holder.joinButton, it)
            meetingDataAdapterListeners.setJoinButtonListener(holder.joinButton, it)
        }

    }

    /**
     * ViewHolder class for this ListAdapter
     */
    inner class ViewHolder(binding: FragmentMeetingElemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val titleView = binding.fragmentMeetingElemTitle
        val dateView = binding.fragmentMeetingElemDate
        val locationView = binding.fragmentMeetingElemLocation
        val joinButton = binding.fragmentMeetingElemJoinButton
    }

    /**
     * Private object to use to compute difference in meeting list for the list adapter
     */
    private object DiffMeeting : DiffUtil.ItemCallback<Meeting>() {
        override fun areItemsTheSame(oldItem: Meeting, newItem: Meeting) =
            oldItem.meetingId == newItem.meetingId

        override fun areContentsTheSame(oldItem: Meeting, newItem: Meeting) =
            oldItem == newItem
    }
}