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
}