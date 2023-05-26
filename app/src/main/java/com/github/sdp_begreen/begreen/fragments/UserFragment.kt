package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


/**
 * A fragment representing a list of Items.
 */
class UserFragment : Fragment() {

    // The number of columns to display in the RecyclerView grid.
    private var columnCount = 1

    private val db by inject<DB>()

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
        // Inflate the Fragment layout.
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)
        // Set the adapter of the RecyclerView to a new instance of UserViewAdapter.
        if (view is RecyclerView) {
            with(view) {
                // Set the layout manager of the RecyclerView based on the number of columns.
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                lifecycleScope.launch {
                    var userList: List<User> = db.getAllUsers()

                    if (arguments?.getBoolean(ARG_IS_LIST_SORTED_BY_SCORE) == true) {
                        userList = userList.sortedByDescending { it.score }
                    }
                    // Set the adapter of the RecyclerView to a new instance of UserViewAdapter,
                    // passing the list of users from the Fragment arguments as a parameter.
                    adapter =
                        UserViewAdapter(userList, parentFragmentManager, lifecycleScope, resources)
                }
            }
        }
        return view
    }

    companion object {

        // The names of the parameters in the Fragment arguments.
        private const val ARG_COLUMN_COUNT = "column-count"
        private const val ARG_IS_LIST_SORTED_BY_SCORE = "is-list-sorted-by-score"

        // Create a new instance of the Fragment with the given parameters.
        @JvmStatic
        fun newInstance(columnCount: Int, isSortedByScore: Boolean) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putBoolean(ARG_IS_LIST_SORTED_BY_SCORE, isSortedByScore)
                }
            }
    }
}
