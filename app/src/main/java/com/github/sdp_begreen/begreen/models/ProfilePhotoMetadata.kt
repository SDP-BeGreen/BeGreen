package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

data class ProfilePhotoMetadata(
    override var pictureId: String? = null,
    override val takenOn: ParcelableDate? = null,
    override val takenByUserId: String? = null,

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