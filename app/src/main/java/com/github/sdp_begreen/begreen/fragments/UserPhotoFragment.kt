package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import kotlin.collections.ArrayList

/**
 * A fragment representing a list of Items.
 */
class UserPhotoFragment : Fragment() {

    private var columnCount = 1
    private var photoList: List<TrashPhotoMetadata>? = null
    private var isFeed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set args
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            photoList = it.getParcelableArrayList(ARG_PHOTO_LIST)
            isFeed = it.getBoolean(ARG_IS_FEED)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_photo_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    //Display the photos in a grid if there are more than one
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = UserPhotosViewAdapter(photoList?: listOf(), isFeed)
            }
            if(!isFeed){
                //If not in the feed display horizontally
                view.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }
        return view
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_PHOTO_LIST = "photo-list"
        const val ARG_IS_FEED = "is-feed"

        @JvmStatic
        fun newInstance(columnCount: Int, photoList: List<TrashPhotoMetadata>?, isFeed: Boolean) =
            //Put args
            UserPhotoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putParcelableArrayList(ARG_PHOTO_LIST, photoList?.let { ArrayList(it) })
                    putBoolean(ARG_IS_FEED, isFeed)
                }
            }
    }
}