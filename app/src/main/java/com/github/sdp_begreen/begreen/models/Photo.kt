package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


data class Photo(val key: String?, val takenOn: ParcelableDate?, val takenBy: User?, val category: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString()
    ) {
    }

    fun getPhotoFromDataBase() : Bitmap? {
        //TODO : get the photo from the database and maybe cache?
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeParcelable(takenBy, flags)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }



    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}