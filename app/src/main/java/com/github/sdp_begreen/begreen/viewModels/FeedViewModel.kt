package com.github.sdp_begreen.begreen.viewModels

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.koin.java.KoinJavaComponent

class FeedViewModel( private val currentUser: StateFlow<User?>) : ViewModel() {

    private val db by KoinJavaComponent.inject<DB>(DB::class.java)
    private val auth by KoinJavaComponent.inject<Auth>(Auth::class.java)

    private val feedPosts : MutableList<PhotoMetadata> = mutableListOf()

    //Need to be cached
    val sortedFeedPosts: List<PhotoMetadata?> = feedPosts.also {
        getFeedPosts(); orderFeedPosts()
    }


    private fun getFeedPosts() {
        currentUser.value?.let { cur_user ->
            cur_user.followers?.onEach { user ->
                user.posts?.let { feedPosts.addAll(it) }
            }
        }
    }

    private fun orderFeedPosts() {
        feedPosts.sort()
    }
}