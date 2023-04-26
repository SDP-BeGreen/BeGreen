package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class that represents a waist collection meeting
 */
@Parcelize
data class Meeting(
    var meetingId: String? = null,
    var creator: String? = null,
    var title: String? = null,
    var description: String? = null,
    var comments: List<Comment>? = null,
    var startDateTime: Long? = null,
    var endDateTime: Long? = null,
    var startCoordinates: CustomLatLng? = null,
    var endCoordinates: CustomLatLng? = null,
    var intermediaryCoordinates: List<CustomLatLng>? = null,
    var participants: List<String>? = null,
    var meetingPhotos: List<PhotoMetadata>? = null,
    var itinerary: String? = null,
): Parcelable {


    override fun toString(): String {
        return "$title: $creator"
    }
}
