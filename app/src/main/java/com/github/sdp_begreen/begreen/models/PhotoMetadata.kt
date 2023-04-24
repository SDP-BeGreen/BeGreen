package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


data class PhotoMetadata(var pictureId: String? = null, val title: String? = null,
                         val takenOn: ParcelableDate? = null, val takenBy: String? = null,
                         val category: String? = null, val description: String? = null)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeString(title)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeString(takenBy)
        parcel.writeString(category)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoMetadata

        if (pictureId != other.pictureId) return false
        if (title != other.title) return false
        if (takenOn != other.takenOn) return false
        if (takenBy != other.takenBy) return false
        if (category != other.category) return false
        if (description != other.description) return false

        return true
    }


    companion object CREATOR : Parcelable.Creator<PhotoMetadata> {
        override fun createFromParcel(parcel: Parcel): PhotoMetadata {
            return PhotoMetadata(parcel)
        }

        override fun newArray(size: Int): Array<PhotoMetadata?> {
            return arrayOfNulls(size)
        }
    }



}