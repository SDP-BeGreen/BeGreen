package com.github.sdp_begreen.begreen.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.User

/**
 * A simple [Fragment] subclass that displays the list of followers
 * Use the [FollowersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowersFragment : Fragment() {

    // The number of columns to display in the RecyclerView grid.
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the number of columns from the Fragment arguments.
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View? = inflater.inflate(R.layout.fragment_user_list, container, false)
        // Set the adapter of the RecyclerView to a new instance of UserViewAdapter.
        if (view is RecyclerView) {
            with(view) {
                // Set the layout manager of the RecyclerView based on the number of columns.
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val followers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arguments?.getParcelableArrayList(ARG_USER_LIST, User::class.java)
                }else {
                    arguments?.getParcelableArrayList(ARG_USER_LIST)
                }

                // Set the adapter of the RecyclerView to a new instance of UserViewAdapter,
                // passing the list of users from the Fragment arguments as a parameter.
                adapter = UserViewAdapter(followers, parentFragmentManager, lifecycleScope, resources)
            }
        }

        return view
    }

    companion object {

        // The names of the parameters in the Fragment arguments.
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_USER_LIST = "user-list"

        @JvmStatic
        fun newInstance(columnCount: Int, followers: List<User>?) =
            FollowersFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putParcelableArrayList(ARG_USER_LIST, followers?.toCollection(ArrayList()))
                }
            }
    }
}