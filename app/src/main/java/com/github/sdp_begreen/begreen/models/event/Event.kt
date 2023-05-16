package com.github.sdp_begreen.begreen.models.event

import android.os.Parcelable
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.utils.CopyableWithId
import com.github.sdp_begreen.begreen.models.CustomLatLng

/**
 * Interface that define the base attribute that an Event should have
 */
interface Event<T> : CopyableWithId<T>, Parcelable {
    var creator: String?
    var title: String?
    var description: String?
    var startCoordinates: CustomLatLng?
    var startDateTime: Long?
    var endDateTime: Long?

    /**
     * The root path is used to know where to store the event on the database
     */
    val rootPath: RootPath

    override fun equals(other: Any?): Boolean
}