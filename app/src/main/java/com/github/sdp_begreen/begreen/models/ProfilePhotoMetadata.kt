package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

data class ProfilePhotoMetadata(
    var pictureId: String? = null,
    val takenOn: ParcelableDate? = null,
    val takenByUserId: String? = null,

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
        if (other !is ProfilePhotoMetadata) return false

        // ProfilePhotoMetadata is uniquely defined by the picture id because if we compare all fields, some of them
        // are obsolete in the database. Actually this was the case in our case.

        return pictureId == other.pictureId
    }

    override fun hashCode(): Int {
        return pictureId?.hashCode() ?: 0
    }
}