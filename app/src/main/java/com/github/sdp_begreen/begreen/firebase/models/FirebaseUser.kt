package com.github.sdp_begreen.begreen.firebase.models

import com.github.sdp_begreen.begreen.models.ProfilePhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User

/**
 * This class adapts the class User such that it can be effectively stored to Firebase
 */
data class FirebaseUser(
    val id: String,
    var score: Int,
    val displayName: String? = null,
    var description: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var progression: Int = 0,
    var followers: Map<String, Boolean>? = null,
    var following: Map<String, Boolean>? = null,
    var profilePictureMetadata: ProfilePhotoMetadata? = null,
    var trashPhotosMetadatasList: List<TrashPhotoMetadata>? = null,
) {

    // Default constructor required to deserialized object retrieved from firebase
    constructor() : this("1", 1)

    constructor(user: User) : this(
        user.id,
        user.score,
        user.displayName,
        user.description,
        user.phone,
        user.email,
        user.progression,
        user.followers?.associate { it to true },
        user.following?.associate { it to true },
        user.profilePictureMetadata,
        user.trashPhotosMetadatasList,
    )

    fun toUser(): User {
        return User(
            id,
            score,
            displayName,
            description,
            phone,
            email,
            progression,
            followers?.keys?.toList(),
            following?.keys?.toList(),
            profilePictureMetadata,
            trashPhotosMetadatasList,
        )
    }
}
