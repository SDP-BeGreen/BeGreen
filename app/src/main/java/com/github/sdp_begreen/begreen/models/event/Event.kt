package com.github.sdp_begreen.begreen.models.event

import android.os.Parcelable
import android.util.Log
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.utils.CopyableWithId
import com.github.sdp_begreen.begreen.models.CustomLatLng
import java.util.*

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
     * Function to tell whether the event is currently active
     * @return true if the event has started and is not finished yet
     */
    fun isActive(): Boolean {
        Log.d("SendPostFragment", "Start date time: " +startDateTime.toString())
        Log.d("SendPostFragment", "End date time: " +endDateTime.toString())
        Log.d("SendPostFragment", "Current time: " +Date().time)
        startDateTime?.also{ Log.d("SendPostFragment", "Has started: " + (it < Date().time)) }
        endDateTime?.also { Log.d("SendPostFragment", "Has finished: " + (it < Date().time)) }

        return startDateTime?.let { it < Date().time } ?: false
            && endDateTime?.let { Date().time < it } ?: false }
}