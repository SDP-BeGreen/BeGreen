package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.adapters.ExpandableListAdapter
import com.github.sdp_begreen.begreen.models.Group
import com.github.sdp_begreen.begreen.models.Item

/**
 * A simple [Fragment] subclass.
 * Use the [AdviceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdviceFragment : Fragment() {

    // The purpose of this method is to inflate the fragment layout, initialize some views,
    // and display a random piece of advice on the screen.
    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: ExpandableListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_advice, container, false)!!

        /*val adviceFragmentTextView = view.findViewById<TextView>(R.id.adviceFragmentTextView)

        // Sets the text of the adviceFragmentTextView to a random string from the adviceList variable.
        arguments?.also { it.getStringArrayList(QUOTES)?.also { list ->  adviceFragmentTextView.text =
            list[Random.nextInt(list.size)]
        } }*/

        expandableListView = view.findViewById(R.id.expandable_list1)

        val groups = getGroups() // Replace this with your data source

        adapter = ExpandableListAdapter(requireContext(), groups)
        expandableListView.setAdapter(adapter)

        return view
    }

    private fun getGroups(): List<Group> {
        // Replace this with your data source
        val group1 = Group(R.drawable.baseline_fastfood_24, "Food", listOf(Item("1.\t\"Eat food. Not too much. Mostly plants.\" - Michael Pollan"),
            Item("2.\t\"The greatest change we need to make is from consumption to production, even if on a small scale, in our own gardens.\" - Bill Mollison"),
            Item("3.\t\"The solution to climate change is right beneath our feet.\" - Dr. Vandana Shiva"),
            Item("4.\t\"Our food choices directly impact the environment.\" - Laurie David"),
            Item("5.\t\"To truly address climate change, we must transform our food systems.\" - Gunhild Stordalen"),
            Item("6.\t\"Organic farming is the key to both environmental and food sustainability.\" - Robert Rodale"),
            Item("7.\t\"A nation that destroys its soils destroys itself.\" - Franklin D. Roosevelt"),
            Item("8.\t\"Real food doesn't have ingredients; real food is ingredients.\" - Jamie Oliver"),
            Item("9.\t\"Agriculture is our wisest pursuit because it will, in the end, contribute most to real wealth, good morals, and happiness.\" - Thomas Jefferson"),
            Item("10.\t\"The future of food lies in regenerative agriculture.\" - Charles Massy")
        ))
        val group2 = Group(R.drawable.baseline_water_drop_24, "Water", listOf(Item("1.\t\"Thousands have lived without love, not one without water.\" - W. H. Auden"),
            Item("2.\t\"We never know the worth of water till the well is dry.\" - Thomas Fuller"),
            Item("3.\t\"Water is life's matter and matrix, mother and medium. There is no life without water.\" - Albert Szent-Gyorgyi"),
            Item("4.\t\"By means of water, we give life to everything.\" - Koran"),
            Item("5.\t\"Water is the most critical resource issue of our lifetime and our children's lifetime. The health of our waters is the principal measure of how we live on the land.\" - Luna Leopold"),
            Item("6.\t\"We forget that the water cycle and the life cycle are one.\" - Jacques Yves Cousteau"),
            Item("7.\t\"When the well's dry, we know the worth of water.\" - Benjamin Franklin"),
            Item("8.\t\"Water and air, the two essential fluids on which all life depends, have become global garbage cans.\" - Jacques Yves Cousteau"),
            Item("9.\t\"In the world, there is nothing more submissive and weak than water. Yet for attacking that which is hard and strong, nothing can surpass it.\" - Lao Tzu"),
            Item("10.\t\"A river is more than an amenity; it is a treasure.\" - Oliver Wendell Holmes")
        ))
        val group3 = Group(R.drawable.baseline_monetization_on_24, "Income", listOf(Item("1.\t\"Economic growth without environmental damage is possible.\" - Indira Gandhi"),
            Item("2.\t\"It is not the environment that must adapt to the economy, but the economy that must adapt to the environment.\" - David Suzuki"),
            Item("3.\t\"Economic progress should not be at the expense of the environment.\" - Prakash Javadekar"),
            Item("4.\t\"The more clearly we can focus our attention on the wonders and realities of the universe about us, the less taste we shall have for destruction.\" - Rachel Carson"),
            Item("5.\t\"We must connect the dots between climate change, water scarcity, energy shortages, global health, food security, and women's empowerment. Solutions to one problem must be solutions for all.\" - Ban Ki-moon"),
            Item("6.\t\"Sustainable development is the pathway to the future we want for all.\" - Ban Ki-moon"),
            Item("7.\t\"Green is the new gold. Investing in the environment is investing in our future.\" - David Suzuki"),
            Item("8.\t\"We need to promote a new kind of prosperity that is in harmony with nature.\" - Jane Goodall"),
            Item("9.\t\"There can be no sustainable development without social equity.\" - Gro Harlem Brundtland"),
            Item("10.\t\"Economic growth and environmental protection are not mutually exclusive. They can and should go hand in hand.\" - Christine Lagarde")
        ))
        return listOf(group1, group2, group3)
    }

    companion object {

        private const val QUOTES = "quotes"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param list of quotes.
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