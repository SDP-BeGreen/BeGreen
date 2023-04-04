package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 * @param testCallback A callback received from tests, it expects the set retrieved
 *  from the database as parameter. It is null by default, should only be non null from tests.
 */
class AdviceFragment : Fragment() {

    // Get the db instance
    private val db by inject<DB>()

    // The purpose of this method is to inflate the fragment layout, initialize some views,
    // and display a random piece of advice on the screen.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_advice, container, false)!!

        val adviceFragmentTextView = view.findViewById<TextView>(R.id.adviceFragmentTextView)

        // Sets the text of the adviceFragmentTextView to a random string from the adviceSet fetched from the DB.
        lifecycleScope.launch {
            val advicesSet: Set<String> = db.getAdvices()
            if (advicesSet.isNotEmpty()) {
                adviceFragmentTextView.text = advicesSet.random()
            }
        }

        return view
    }
}