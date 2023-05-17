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

    /**
     * Function to tell whether the event has started
     * @return true if the event has started, null if the starting date is null and false otherwise
     */
    fun isStarted(): Boolean? {
        return startDateTime?.let { it < System.currentTimeMillis() }
    }

    /**
     * Function to tell whether the event is finished
     * @return true if the event is finished, null if the ending date is null and false otherwise
     */
    fun isFinished(): Boolean? {
        return endDateTime?.let { it < System.currentTimeMillis() }
    }

    /**
     * Function to tell whether the event is currently active
     * @return true if the event has started and is not finished yet,
     * null if the starting date or the ending date is null and false otherwise
     */
    fun isActive(): Boolean? {
        return isStarted()?.let { started -> isFinished()?.let { finished -> started && !finished } }
    }
}