package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.util.*


class ParcelableDate() : Parcelable {
    var date: Date? = null

    constructor(parcel: Parcel) : this() {
        val tmpDate: Long = parcel.readLong()
        date = if (tmpDate == -1L) null else Date(tmpDate)
    }

    constructor(date: Date?) : this() {
        this.date = date
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(date?.time ?: -1)
    }

    override fun toString(): String {
        val dateStr = this.date.toString()
        //Take a substring of the date to remove the day, seconds and gmt part
        return dateStr.substring(4,16)
    }

    companion object CREATOR : Creator<ParcelableDate> {
        override fun createFromParcel(parcel: Parcel): ParcelableDate {
            return ParcelableDate(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableDate?> {
            return arrayOfNulls(size)
        }
    }

}