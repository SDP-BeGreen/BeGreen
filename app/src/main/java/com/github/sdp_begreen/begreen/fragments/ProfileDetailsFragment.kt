package com.github.sdp_begreen.begreen.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.User
import com.google.android.material.appbar.MaterialToolbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_USER = "USER"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileDetailsFragment : Fragment() {
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_USER, User::class.java)
            }else {
                it.getParcelable(ARG_USER)
            }
        }
        val name: TextView = view?.findViewById(R.id.profile_name) as TextView
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_details, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user user to show details.
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        @JvmStatic
        fun newInstance(user: User) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}