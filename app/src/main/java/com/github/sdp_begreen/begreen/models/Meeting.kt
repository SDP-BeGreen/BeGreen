package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import com.github.sdp_begreen.begreen.models.CustomLatLng
import kotlinx.parcelize.Parcelize

/**
 * Class that represents a waste collection meeting
 */
@Parcelize
data class Meeting(
    var meetingId: String? = null,
    var creator: String? = null,
    var title: String? = null,
    var description: String? = null,
    var startDateTime: Long? = null,
    var endDateTime: Long? = null,
    var startCoordinates: CustomLatLng? = null,
    var endCoordinates: CustomLatLng? = null,
    var intermediaryCoordinates: List<CustomLatLng>? = null,
    // TODO add the itinerary to store the computed way
    //  add it later when calculating the itinerary to see how to best store it
    //var itinerary: String? = null,
) : Parcelable {


    override fun toString(): String {
        return "$title: $creator"
    }
}
