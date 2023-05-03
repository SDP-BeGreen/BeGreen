package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

class ProfilePhotoMetadata(
    pictureId: String? = null,
    takenOn: ParcelableDate? = null,
    takenByUserId: String? = null,

    ) : PhotoMetadata(pictureId, takenOn, takenByUserId) {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readString()
    )

    companion object CREATOR : Parcelable.Creator<ProfilePhotoMetadata> {
        override fun createFromParcel(parcel: Parcel): ProfilePhotoMetadata {
            return ProfilePhotoMetadata(parcel)
        }

        override fun newArray(size: Int): Array<ProfilePhotoMetadata?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val metadata = other as ProfilePhotoMetadata
        return pictureId == metadata.pictureId &&
                takenOn == metadata.takenOn &&
                takenByUserId == metadata.takenByUserId
    }

    override fun hashCode(): Int {
        var result = pictureId?.hashCode() ?: 0
        result = 31 * result + (takenOn?.hashCode() ?: 0)
        result = 31 * result + (takenByUserId?.hashCode() ?: 0)
        return result
    }
}