package com.github.sdp_begreen.begreen.adapters

import android.widget.TextView
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.google.android.material.button.MaterialButton

/**
 * Interface to implement to help the [MeetingsListAdapter] to properly display
 * each element in its view. And setup the button listeners.
 */
interface MeetingDataAdapterListeners {

    /**
     * Function to get the address corresponding to the received coordinates and set it to
     * the text view.
     *
     * @param coordinates The location coordinates
     * @param textView The text view in which to display the address
     */
    fun setAddressToTextViewFromCoordinates(coordinates: CustomLatLng, textView: TextView)

    /**
     * Function to setup the listener for the button to either join or withdraw from a
     * meeting
     *
     * @param button The button on which to set the listener
     * @param meetingId The id of the meeting to which this button is related to
     */
    fun setJoinButtonListener(button: MaterialButton, meetingId: String)

    /**
     * Function to setup the button with the text "join" or "withdraw" given if the connected
     * user is already taking part to the meeting or not
     *
     * @param button The button for which to set the name
     * @param meetingId The id of the meeting to which this button is related to
     */
    fun setJoinButtonText(button: MaterialButton, meetingId: String)
}