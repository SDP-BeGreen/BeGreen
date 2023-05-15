package com.github.sdp_begreen.begreen.adapters

import android.widget.Button
import android.widget.TextView
import com.github.sdp_begreen.begreen.models.CustomLatLng

/**
 * Interface to implement to help the [EventsListAdapter] to properly display
 * each element in its view. And setup the button listeners.
 */
interface EventDataAdapterListeners {

    /**
     * Function to get the address corresponding to the received coordinates and set it to
     * the text view.
     *
     * @param coordinates The location coordinates
     * @param textView The text view in which to display the address
     */
    fun setAddressToTextViewFromCoordinates(coordinates: CustomLatLng, textView: TextView)

    /**
     * Function to setup the listener for the button to either join or withdraw from an
     * event
     *
     * @param button The button on which to set the listener
     * @param eventId The id of the event to which this button is related to
     */
    fun setJoinButtonListener(button: Button, eventId: String)

    /**
     * Function to setup the button with the text "join" or "withdraw" given if the connected
     * user is already taking part to the event or not
     *
     * @param button The button for which to set the name
     * @param eventId The id of the event to which this button is related to
     */
    fun setJoinButtonText(button: Button, eventId: String)
}