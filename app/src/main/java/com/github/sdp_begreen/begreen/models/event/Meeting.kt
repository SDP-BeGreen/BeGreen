package com.github.sdp_begreen.begreen.models.event

import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.google.firebase.database.Exclude
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Class that represents a waste collection meeting
 */
@Parcelize
data class Meeting(
    override var id: String? = null,
    override var creator: String? = null,
    override var title: String? = null,
    override var description: String? = null,
    override var startDateTime: Long? = null,
    override var endDateTime: Long? = null,
    override var startCoordinates: CustomLatLng? = null,
    var endCoordinates: CustomLatLng? = null,
    var intermediaryCoordinates: List<CustomLatLng>? = null,
    // TODO add the itinerary to store the computed way
    //  add it later when calculating the itinerary to see how to best store it
    //var itinerary: String? = null,
) : Event<Meeting> {

    @get:Exclude
    @IgnoredOnParcel
    override val rootPath = RootPath.MEETINGS

    override fun copyWithNewId(newId: String) = copy(id = newId)

    override fun toString(): String {
        return "$title: $creator"
    }
}
