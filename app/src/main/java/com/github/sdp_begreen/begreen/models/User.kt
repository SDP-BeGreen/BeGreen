package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable
import com.github.sdp_begreen.begreen.firebase.DB
import org.koin.java.KoinJavaComponent

//Need to be Parcelable to be passed as an argument to a fragment
data class User (var id: String, var score: Int, val displayName: String? = null, var rating: Int = 0,
                 var description: String? = null, var phone: String? = null,
                 var email: String? = null, var progression: Int = 0, var followers: Set<String>? = null,
                 var following: Set<String>? = null, var profilePictureMetadata: PhotoMetadata? = null,
                 var posts: List<PhotoMetadata>? = null) : Parcelable, Comparable<User> {

    private val db by KoinJavaComponent.inject<DB>(DB::class.java)
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
        parcel.readArrayList(String::class.java.classLoader) as Set<String>?,
        parcel.readArrayList(String::class.java.classLoader) as Set<String>?,
        parcel.readParcelable(PhotoMetadata::class.java.classLoader),
        parcel.readArrayList(PhotoMetadata::class.java.classLoader) as List<PhotoMetadata>?
    )

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
        //parcel.writeList(followers)
        //parcel.writeList(following)
        parcel.writeStringList(following?.toList()) as Set<String>?
        parcel.writeStringList(followers?.toList()) as Set<String>?
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