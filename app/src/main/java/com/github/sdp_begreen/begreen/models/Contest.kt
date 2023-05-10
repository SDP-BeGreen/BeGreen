package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import com.github.sdp_begreen.begreen.firebase.RootPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contest(
    override var id: String? = null,
    override var creator: String? = null,
    override var title: String? = null,
    override var description: String? = null,
    override var startDateTime: Long? = null,
    override var endDateTime: Long? = null,
    override var startCoordinates: CustomLatLng? = null,
    var radius: Long = 0,
    var private: Boolean = false,
): Event<Contest>, Parcelable {

    @IgnoredOnParcel
    override val rootPath = RootPath.CONTESTS

    override fun copy(newId: String) = copy(id = newId)
}
