package com.github.sdp_begreen.begreen.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class ConnectedUserViewModel: ViewModel() {

    private val mutableCurrentUser = MutableStateFlow<User?>(null)
    private val mutableCurrentUserProfilePicture = MutableStateFlow<Bitmap?>(null)

    //TODO return a flow from the future AUTH interface

    /**
     * Flow that dynamically retrieve authenticated user from firebase upon any modification
     */
    private val userFromAuth = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.uid) }
        Firebase.auth.addAuthStateListener(listener)

        // Unregister the listener to avoid memory leak upon flow deletion
        awaitClose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }.map {
        it?.let { FirebaseDB.getUser(it) }
    }.onEach {
        // each time a new connection is done, directly fetch the user profile picture
        // and update the currentUserProfilePicture value
        it?.also { user ->
            user.profilePictureMetadata?.let { photo ->
                FirebaseDB.getUserProfilePicture(photo, user.id)
            }?.also { photo -> mutableCurrentUserProfilePicture.value = photo }
        }
    }

    /**
     * Merge both flows, mutableCurrentUser and userFromAuth, so that we always get
     * the latest value either from the user updated one, or if an modification in the authenticated
     * user arrives
     */
    @OptIn(FlowPreview::class)
    val currentUser: StateFlow<User?> = flowOf(userFromAuth, mutableCurrentUser)
        .flattenMerge()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentUserProfilePicture = mutableCurrentUserProfilePicture.asStateFlow()

    /**
     * Function to modify the current user
     *
     * Changing the current user will reset the current profile picture (i.e. set its value to null)
     *
     * @param user The new current user
     */
    fun setCurrentUser(user: User) {
        Firebase.auth.addAuthStateListener {  }
        mutableCurrentUser.value = user
        mutableCurrentUserProfilePicture.value = null
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