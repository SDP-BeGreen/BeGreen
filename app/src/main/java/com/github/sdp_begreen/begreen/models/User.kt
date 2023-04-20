package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

//Need to be Parcelable to be passed as an argument to a fragment
data class User (var id: String, var score: Int, val displayName: String? = null, var rating: Int = 0,
                 var description: String? = null, var phone: String? = null,
                 var email: String? = null, var progression: Int = 0, var followers: List<User>? = null,
                 var following: List<User>? = null, var profilePictureMetadata: PhotoMetadata? = null,
                 var posts: List<PhotoMetadata>? = null) : Parcelable, Comparable<User> {

    // Default constructor required to deserialized object retrieved from firebase
    constructor() : this("1",  1)

    constructor(parcel: Parcel) :this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readArrayList(User::class.java.classLoader) as List<User>?,
        parcel.readArrayList(User::class.java.classLoader) as List<User>?,
        parcel.readParcelable(PhotoMetadata::class.java.classLoader),
        parcel.readArrayList(PhotoMetadata::class.java.classLoader) as List<PhotoMetadata>?
    )

    suspend fun addFollower(follower: User) {
        //TODO : add the following to the database
    }

    override fun compareTo(other: User): Int {
        return this.score.compareTo(other.score)
    }

    override fun toString(): String = displayName ?: "Username"
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(score)
        parcel.writeString(displayName)
        parcel.writeInt(rating)
        parcel.writeString(description)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeInt(progression)
        parcel.writeList(followers)
        parcel.writeList(following)
        parcel.writeParcelable(profilePictureMetadata, 0)
        parcel.writeList(posts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        var currentUser: User = User("0",  0, "Test")

        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}