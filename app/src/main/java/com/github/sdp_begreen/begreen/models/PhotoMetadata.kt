package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


abstract class PhotoMetadata(
    open val pictureId: String? = null,
    open val takenOn: ParcelableDate? = null,
    open val takenByUserId: String? = null
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
