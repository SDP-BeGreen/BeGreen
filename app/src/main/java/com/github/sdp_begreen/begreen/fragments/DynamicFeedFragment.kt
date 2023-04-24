package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.viewModels.FeedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * View to fetch the results from the remote api and directly shows in the recyclerview
 * with lazy pagination enabled
 */
@ExperimentalPagingApi
class DynamicFeedFragment : Fragment(R.layout.fragment_user_photo_list) {

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
        adapter = FeedViewAdapter()
    }

    /**
     * Sets up the views
     */
    private fun setUpViews(view: View) {
        rvFeedRemote = view.findViewById(R.id.feed_list)
        rvFeedRemote.layoutManager = GridLayoutManager(context, 2)
        rvFeedRemote.adapter = adapter
    }
}
