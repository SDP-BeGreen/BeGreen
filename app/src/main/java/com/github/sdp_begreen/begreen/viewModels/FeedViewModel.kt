package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.github.sdp_begreen.begreen.data.FeedRepository
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import javax.inject.Inject

/**
 * ViewModel that will be used to retrieve the feed of the current user
 */
@ExperimentalPagingApi
class FeedViewModel(private val currentUser: StateFlow<User?> = ConnectedUserViewModel().currentUser,
                    private val repository: FeedRepository = FeedRepository.getInstance()
) : ViewModel() {

    private val db  by inject<DB>(FirebaseDB::class.java)

    /** Function to retrieve the feed of the current user */
    fun fetchFeed(): Flow<PagingData<PhotoMetadata>> {
        var feedPosts: List<PhotoMetadata> = listOf()
        runBlocking {
            viewModelScope.launch {
                feedPosts = currentUser.value?.let { cur_user ->
                    cur_user.following?.flatMap { id ->
                        getUserPosts(id) ?: emptyList()
                    }
                } ?: emptyList()
            }
        }
        //sort it by date
        return repository.letPhotoMetadataFlow(feedPosts.sortedBy { it.takenOn})
    }


    suspend private fun getUserPosts(user : String) : List<PhotoMetadata> {
        return db.getUser(user).let { us ->
            us?.posts ?: emptyList()
        }
    }
}