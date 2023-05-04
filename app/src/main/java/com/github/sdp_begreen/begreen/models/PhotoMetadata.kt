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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhotoMetadata) return false

        // A PhotoMetadata is uniquely defined by the pictureId because if we compare all fields, some of them
        // are obsolete in the database. Actually we encountered this issue during the testing, because the PhotoMetadata
        // constructor has been a bit modified, so the class didn't match the stored one in the database
        // even if the pictureId was the same.

        return pictureId == other.pictureId
    }

    override fun hashCode(): Int {
        return pictureId?.hashCode() ?: 0
    }
}
