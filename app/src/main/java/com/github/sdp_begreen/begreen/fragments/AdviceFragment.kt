package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.sdp_begreen.begreen.R
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 * Use the [AdviceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdviceFragment : Fragment() {

    // The purpose of this method is to inflate the fragment layout, initialize some views,
    // and display a random piece of advice on the screen.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_advice, container, false)!!

        val adviceFragmentTextView = view.findViewById<TextView>(R.id.adviceFragmentTextView)

        // Sets the text of the adviceFragmentTextView to a random string from the adviceList variable.
        arguments?.also { it.getStringArrayList(QUOTES)?.also { list ->  adviceFragmentTextView.text =
            list[Random.nextInt(list.size)]
        } }

        return view
    }


    companion object {

        private const val QUOTES = "quotes"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param list list of quotes.
         * @return A new instance of fragment FavoriteFragment.
         */
        @JvmStatic
        fun newInstance(list: ArrayList<String>) =
            AdviceFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(QUOTES, list)
                }
            }
    }
}