package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

class TrashPhotoMetadata(
    pictureId: String? = null,
    takenOn: ParcelableDate? = null,
    takenByUserId: String? = null,
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
        if (other == null || javaClass != other.javaClass) return false

        val metadata = other as TrashPhotoMetadata
        return pictureId == metadata.pictureId &&
                takenOn == metadata.takenOn &&
                takenByUserId == metadata.takenByUserId &&
                caption == metadata.caption &&
                trashCategory == metadata.trashCategory
    }

    override fun hashCode(): Int {
        var result = pictureId?.hashCode() ?: 0
        result = 31 * result + (takenOn?.hashCode() ?: 0)
        result = 31 * result + (takenByUserId?.hashCode() ?: 0)
        result = 31 * result + (caption?.hashCode() ?: 0)
        result = 31 * result + (trashCategory?.hashCode() ?: 0)
        return result
    }
}
