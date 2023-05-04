package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

data class TrashPhotoMetadata(
    override var pictureId: String? = null,
    override val takenOn: ParcelableDate? = null,
    override val takenBy: String? = null,
    val caption: String? = null,
    val trashCategory: TrashCategory? = null

) : PhotoMetadata(pictureId, takenOn, takenBy) {

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
        parcel.writeString(takenBy)
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
}
