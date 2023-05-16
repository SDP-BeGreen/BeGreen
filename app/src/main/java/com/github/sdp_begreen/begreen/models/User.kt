package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.Meeting
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
    var meetingIdsList: List<String>? = null,
    var contestIdsList: List<String>? = null
) : Parcelable, Comparable<User> {

    suspend fun addFollower(follower: User) {
        //TODO : add the following to the database
    }

    /**
     * TODO: comments
     */
    fun <T: Event<T>> copyWithNewEvent(eventId: String, eventImplType: Class<T>): User {
        return when(eventImplType) {
            Meeting::class.java -> copy(meetingIdsList = meetingIdsList?.let { it + eventId } ?: listOf(eventId))
            Contest::class.java -> copy(contestIdsList = contestIdsList?.let { it + eventId } ?: listOf(eventId))
            else -> {throw java.lang.IllegalArgumentException("Illegal event class")}
        }
    }

    fun <T: Event<T>> copyWithRemovedEvent(eventId: String, eventImplType: Class<T>): User {
        return when(eventImplType) {
            Meeting::class.java -> copy(meetingIdsList = meetingIdsList?.let { it - eventId } ?: listOf(eventId))
            Contest::class.java -> copy(contestIdsList = contestIdsList?.let { it - eventId } ?: listOf(eventId))
            else -> {throw java.lang.IllegalArgumentException("Illegal event class")}
        }
    }

    override fun compareTo(other: User): Int {
        return this.score.compareTo(other.score)
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
