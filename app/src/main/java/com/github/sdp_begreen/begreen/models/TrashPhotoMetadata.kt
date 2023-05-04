package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

data class TrashPhotoMetadata(
    var pictureId: String? = null,
    val takenOn: ParcelableDate? = null,
    val takenByUserId: String? = null,
    val caption: String? = null,
    val trashCategory: TrashCategory? = null

) : PhotoMetadata(pictureId, takenOn, takenByUserId) {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(TrashCategory::class.java.classLoader),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeString(takenByUserId)
        parcel.writeString(caption)
        parcel.writeParcelable(trashCategory, flags)
    }

    companion object CREATOR : Parcelable.Creator<TrashPhotoMetadata> {
        override fun createFromParcel(parcel: Parcel): TrashPhotoMetadata {
            return TrashPhotoMetadata(parcel)
        }

        override fun newArray(size: Int): Array<TrashPhotoMetadata?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrashPhotoMetadata) return false

        return pictureId == other.pictureId
    }

    override fun hashCode(): Int {
        return pictureId?.hashCode() ?: 0
    }
}
