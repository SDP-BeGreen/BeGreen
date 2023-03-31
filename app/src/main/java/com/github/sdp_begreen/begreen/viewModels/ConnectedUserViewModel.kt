package com.github.sdp_begreen.begreen.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sdp_begreen.begreen.models.User

class ConnectedUserViewModel: ViewModel() {
    val currentUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val currentUserProfilePicture: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }
}