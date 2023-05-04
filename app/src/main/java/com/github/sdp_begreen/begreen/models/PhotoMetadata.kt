package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


abstract class PhotoMetadata(
    var pictureId_: String? = null,
    val takenOn_: ParcelableDate? = null,
    val takenByUserId_: String? = null
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId_)
        parcel.writeParcelable(takenOn_, flags)
        parcel.writeString(takenByUserId_)
    }

    override fun describeContents(): Int {
        return 0
    }
}
