package com.github.sdp_begreen.begreen

import android.os.Parcel
import android.os.Parcelable

//Need to be Parcelable to be passed as an argument to a fragment
data class User (val id: Int, val name: String, val score: Int) : Parcelable, Comparable<User> {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt()
    )

    override fun compareTo(other: User): Int {
        return this.score.compareTo(other.score)
    }

    override fun toString(): String = name
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}