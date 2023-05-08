package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


abstract class PhotoMetadata: Parcelable {

    abstract var pictureId: String?
    abstract val takenOn: ParcelableDate?
    abstract val takenBy: String?

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeString(takenBy)
    }

    override fun describeContents(): Int {
        return 0
    }
}
