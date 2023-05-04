package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


abstract class PhotoMetadata(
    open var pictureId: String? = null,
    open val takenOn: ParcelableDate? = null,
    open val takenBy: String? = null
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeString(takenBy)
    }

    override fun describeContents(): Int {
        return 0
    }
}
