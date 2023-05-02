package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


abstract class PhotoMetadata(
    var pictureId: String? = null,
    val takenOn: ParcelableDate? = null,
    val takenByUserId: String? = null
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeString(takenByUserId)
    }

    override fun describeContents(): Int {
        return 0
    }
}
