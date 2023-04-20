package com.github.sdp_begreen.begreen.viewModels

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.github.sdp_begreen.begreen.data.FeedRepository
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

@ExperimentalPagingApi
class FeedViewModel( private val currentUser: StateFlow<User?>,
                     val repository: FeedRepository = FeedRepository.getInstance()
) : ViewModel() {

    fun fetchFeed(): Flow<PagingData<PhotoMetadata>> {
        val feedPosts : List<PhotoMetadata> = currentUser.value?.let { cur_user ->
            cur_user.following?.flatMap { user ->
                user.posts ?: emptyList()
            }
        } ?: emptyList()

        return repository.letPhotoMetadataFlow(feedPosts.sorted())
    }
}