package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.parcelize.Parcelize

// Need to be Parcelable to be passed as an argument to a fragment

@Parcelize
data class User(
    val id: String,
    var score: Int,
    val displayName: String? = null,
    var rating: Int = 0,
    var description: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var progression: Int = 0,
    var followers: List<String>? = null,
    var following: List<String>? = null,
    var profilePictureMetadata: ProfilePhotoMetadata? = null,
    var trashPhotosMetadatasList: List<TrashPhotoMetadata>? = null,
) : Parcelable, Comparable<User> {

    fun follow(userId: String) {

        checkArgument(userId.isNotBlank(), "The userId cannot be blank")

        following = following?.let { it + userId } ?: listOf(userId)
    }

    fun unfollow(userId: String) {

        checkArgument(userId.isNotBlank(), "The userId cannot be blank")

        following = following?.let { it.filter { id -> id != userId }}
    }

    override fun compareTo(other: User): Int {
        return score.compareTo(other.score)
    }

    override fun toString(): String = displayName ?: "Username"

    fun addPhotoMetadata(metadata: TrashPhotoMetadata) {
        trashPhotosMetadatasList =
            trashPhotosMetadatasList?.let { it + metadata } ?: listOf(metadata)
    }

    companion object {
        var currentUser = User("0", 2)
    }
}
