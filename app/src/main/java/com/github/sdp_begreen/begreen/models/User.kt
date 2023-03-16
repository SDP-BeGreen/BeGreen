package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable

//Need to be Parcelable to be passed as an argument to a fragment
data class User (val id: Int, val name: String, val score: Int) : Parcelable, Comparable<User> {
    var rating: Int = 0
    var img: Photo? = null
    var description: String = ""
    var phone: String = ""
    var email: String = ""
    var progression: Int = 0
    var followers: List<User> = listOf()
    var following: List<User> = listOf()

    constructor(id: Int, name: String, score: Int, rating: Int, img: Photo?, description: String, phone: String, email: String, progression: Int, followers: List<User>?, following: List<User>?) : this(id, name, score) {
        this.rating = rating
        this.img = img
        this.description = description
        this.phone = phone
        this.email = email
        this.progression = progression
        this.followers = followers ?: listOf()
        this.following = followers ?: listOf()
    }
    //constructor(parcel: Parcel) : this(
    //    parcel.readInt(),
    //    parcel.readString().toString(),
    //    parcel.readInt()
    //)


    constructor(parcel: Parcel) :this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(Photo::class.java.classLoader),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readArrayList(User::class.java.classLoader) as List<User>,
        parcel.readArrayList(User::class.java.classLoader) as List<User>
    )

    suspend fun addFollower(follower: User) {
        //TODO : add the following to the database
    }

    override fun compareTo(other: User): Int {
        return this.score.compareTo(other.score)
    }

    override fun toString(): String = name
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(score)
        parcel.writeInt(rating)
        parcel.writeParcelable(img, 0)
        parcel.writeString(description)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeInt(progression)
        parcel.writeList(followers)
        parcel.writeList(following)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        var currentUser: User = User(0, "Default", 0)

        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}