package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Need to be Parcelable to be passed as an argument to a fragment

@Parcelize
data class User (val id: String, var score: Int, val displayName: String? = null, var rating: Int = 0,
                 var description: String? = null, var phone: String? = null,
                 var email: String? = null, var progression: Int = 0, var followers: List<String>? = null,
                 var following: List<String>? = null, var profilePictureMetadata: ProfilePhotoMetadata? = null,
                 var trashPhotosMetadatasList: List<TrashPhotoMetadata>? = null) : Parcelable, Comparable<User> {

    suspend fun addFollower(follower: User) {
        //TODO : add the following to the database
    }

    override fun compareTo(other: User): Int {
        return this.score.compareTo(other.score)
    }

    override fun toString(): String = displayName ?: "Username"

    fun addPhotoMetadata(metadata: TrashPhotoMetadata) {
        trashPhotosMetadatasList = trashPhotosMetadatasList?.let { it + metadata } ?: listOf(metadata)
    }

    companion object {

        var currentUser = User("0", 2)
    }
}
