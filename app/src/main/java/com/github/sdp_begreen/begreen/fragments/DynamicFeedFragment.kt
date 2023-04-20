package com.github.sdp_begreen.begreen.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

    lateinit var rvDoggoRemote: RecyclerView
    lateinit var remoteViewModel: FeedViewModel
    lateinit var adapter: FeedViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        setUpViews(view)
        fetchDoggoImages()
    }

    private fun fetchDoggoImages() {
        lifecycleScope.launch {
            remoteViewModel.fetchFeed().distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun initMembers() {
        remoteViewModel = defaultViewModelProviderFactory.create(FeedViewModel::class.java)
        adapter = FeedViewAdapter()
    }

    private fun setUpViews(view: View) {
        rvDoggoRemote = view.findViewById(R.id.feed_list)
        rvDoggoRemote.layoutManager = GridLayoutManager(context, 2)
        rvDoggoRemote.adapter = adapter
    }
}
