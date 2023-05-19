package com.github.sdp_begreen.begreen.models.event

import android.location.Location
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.google.firebase.database.Exclude
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Class that represents a waste collection contest
 */
@Parcelize
data class Contest(
    override var id: String? = null,
    override var creator: String? = null,
    override var title: String? = null,
    override var description: String? = null,
    override var startDateTime: Long? = null,
    override var endDateTime: Long? = null,
    override var startCoordinates: CustomLatLng? = null,
    var radius: Double = 0.0,
    var private: Boolean = false,
) : Event<Contest> {

    @get:Exclude
    @IgnoredOnParcel
    override val rootPath = RootPath.CONTESTS

    override fun copyWithNewId(newId: String) = copy(id = newId)

    override fun toString(): String {
        return "$title: $creator"
    }

    /**
     * Tell whether the given [location] is in range of this contest
     * @param location the location for which we want to check if they fall within the range of this contest
     * @return true if the given [location] is in range of this contest, false if it is not in range,
     * and null if the startCoordinates of this Contest are null
     */
    fun isInRange(location: Location) =
        startCoordinates?.let { it.distanceFrom(location)?.let { dist -> dist <= radius } }
}
