package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


data class PhotoMetadata(val pictureId: String? = null,val title: String? = null, val takenOn: ParcelableDate? = null, val takenBy: User? = null, val category: String? = null, val description: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureId)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeParcelable(takenBy, flags)
        parcel.writeString(category)
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