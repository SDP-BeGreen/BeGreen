package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sdp_begreen.begreen.R


/**
 * A simple [Fragment] subclass.
 * Use the [ContestCreationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContestCreationFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contest_creation, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ContestCreationFragment()
    }
}