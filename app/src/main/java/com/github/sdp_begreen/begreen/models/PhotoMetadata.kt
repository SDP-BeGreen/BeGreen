package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable


data class PhotoMetadata(var pictureId: String? = null,
                         val caption: String? = null,
                         val takenOn: ParcelableDate? = null,
                         val takenByUserId: String? = null,
                         val binTypeId: String? = null)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeString(caption)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeString(takenByUserId)
        parcel.writeString(binTypeId)
    }

    override fun describeContents(): Int {
        return 0
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