package com.github.sdp_begreen.begreen.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.adapters.FeedViewAdapter
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.User.CREATOR.currentUser
import com.github.sdp_begreen.begreen.viewModels.FeedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

/**
 * View to fetch the results from the remote api and directly shows in the recyclerview
 * with lazy pagination enabled
 */
@ExperimentalPagingApi
class DynamicFeedFragment(private val isFeed: Boolean) : Fragment(R.layout.fragment_user_photo_list) {

    private val db by KoinJavaComponent.inject<DB>(FirebaseDB::class.java)

    lateinit var rvFeedRemote: RecyclerView
    lateinit var remoteViewModel: FeedViewModel
    lateinit var adapter: FeedViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        setUpViews(view)
        fetchFeedImages()
    }

    /**
     * Fetches the feed images from the remote api
     */
    private fun fetchFeedImages() {
        lifecycleScope.launch {
            remoteViewModel.fetchFeed().distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    /**
     * Initializes the members
     */
    private fun initMembers() {
        remoteViewModel = defaultViewModelProviderFactory.create(FeedViewModel::class.java)
        adapter = FeedViewAdapter(isFeed, lifecycleScope)
    }

    /**
     * Sets up the views
     */
    private fun setUpViews(view: View) {
        rvFeedRemote = view.findViewById(R.id.feed_list)
        if (isFeed) {
            rvFeedRemote.layoutManager = LinearLayoutManager(context)
        } else{
            rvFeedRemote.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        rvFeedRemote.layoutManager = GridLayoutManager(context, 2)
        rvFeedRemote.adapter = adapter
    }
}
