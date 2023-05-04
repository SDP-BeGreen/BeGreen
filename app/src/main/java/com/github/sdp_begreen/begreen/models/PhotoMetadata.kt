package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


abstract class PhotoMetadata(
    open var pictureId: String? = null,
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

    // The equals() and hashcode() methods should be overrided by concrete class and use only the pictureId.
    // Since the concrete classes are data classes, they must override all field of this (parent) class.
    // It implies that overriding the equals() and hashcode() in this abstract class doesn't affect the subclasses.
}
