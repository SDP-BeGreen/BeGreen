package com.github.sdp_begreen.begreen.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.java.KoinJavaComponent.inject

class ConnectedUserViewModel : ViewModel() {

    private val db by inject<DB>(DB::class.java)
    private val auth by inject<Auth>(Auth::class.java)

    private val mutableCurrentUser = MutableStateFlow<User?>(null)
    private val mutableCurrentUserProfilePicture = MutableStateFlow<Bitmap?>(null)

    /**
     * Flow that dynamically retrieve authenticated user from firebase upon any modification
     */
    private val userFromAuth = auth.getFlowUserIds().map {
        it?.let { db.getUser(it) }
    }.onEach {
        // upon new connection, start by resetting the profile picture to be sure to not keep an
        // old picture, if the new authenticated person doesn't have a profile picture for example
        mutableCurrentUserProfilePicture.value = null

        // each time a new connection is done, directly fetch the user profile picture
        // and update the currentUserProfilePicture value
        it?.also { user ->
            user.profilePictureMetadata?.let { photo ->
                db.getUserProfilePicture(photo, user.id)
            }?.also { photo -> mutableCurrentUserProfilePicture.value = photo }
        }
    }

    /**
     * Merge both flows, mutableCurrentUser and userFromAuth, so that we always get
     * the latest value either from the user updated one, or if an modification in the authenticated
     * user arrives
     */
    @OptIn(FlowPreview::class)
    val currentUser: StateFlow<User?> = flowOf(mutableCurrentUser, userFromAuth)
        .flattenMerge()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentUserProfilePicture = mutableCurrentUserProfilePicture.asStateFlow()

    /**
     * Function to modify the current user
     *
     * @param user The new current user
     * @param keepCurrentPicture Boolean to tell whether when changing the user we wish to keep
     *  the profile picture that is already there if there is one. False by default (i.e. will
     *  reset the current profile picture)
     */
    fun setCurrentUser(user: User, keepCurrentPicture: Boolean = false) {
        mutableCurrentUser.value = user
        if (!keepCurrentPicture) mutableCurrentUserProfilePicture.value = null
    }

    /**
     * Function to set assign a new profile picture
     *
     * @param bitmap The new profile picture to add to the flow
     * @param userId The id of the user for whom we want to modify the profile picture.
     * It has to be the id of the current user
     * @throws IllegalArgumentException
     */
    fun setCurrentUserProfilePicture(bitmap: Bitmap, userId: String?) {

        if (userId == null || userId != currentUser.value?.id) {
            throw IllegalArgumentException("Trying to modify profile picture of another user")
        }
        mutableCurrentUserProfilePicture.value = bitmap
    }
}