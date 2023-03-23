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
    // TODO: Rename and change types of parameters
    private lateinit var adviceFragmentTextView: TextView

    // This will hold the pieces of advice that will be displayed in the TextView
    private var adviceList= ArrayList<String>()

    // The purpose of this method is to inflate the fragment layout, initialize some views,
    // and display a random piece of advice on the screen.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_advice, container, false)!!

        adviceFragmentTextView = view.findViewById(R.id.adviceFragmentTextView)

        addData()

        // Sets the text of the adviceFragmentTextView to a random string from the adviceList variable.
        adviceFragmentTextView.text = adviceList.get(Random.nextInt(adviceList.size))

        return view
    }
    // Helper method to add some advice strings to the adviceList variable
    // TODO: insert a random generator of advices if possible
    private fun addData() {
        adviceList.add("Ecology helps us to understand how our actions affect the environment. It shows the individuals the extent of damage we cause to the environment.")
        adviceList.add("With the knowledge of ecology, we are able to know which resources are necessary for the survival of different organisms. Lack of ecological knowledge has led to scarcity and deprivation of these resources, leading to competition.")
        adviceList.add("All organisms require energy for their growth and development. Lack of ecological understanding leads to the over-exploitation of energy resources such as light, nutrition and radiation, leading to its depletion.")
        adviceList.add("Ecology encourages harmonious living within the species and the adoption of a lifestyle that protects the ecology of life.")
        adviceList.add("It focuses on the relationship between humans and the environment. It emphasizes the impact human beings have on the environment and gives knowledge on how we can improve ourselves for the betterment of humans and the environment.")
        adviceList.add("It deals with the study of how organisms alter the environment for the benefit of themselves and other living beings. For eg, termites create a 6 feet tall mound and at the same time feed and protect their entire population.")
        adviceList.add("Ecology plays a significant role in forming new species and modifying the existing ones. Natural selection is one of the many factors that influences evolutionary change.")
        adviceList.add("Ecology was first devised by Ernst Haeckel, a German Zoologist. However, ecology has its origins in other sciences such as geology, biology, and evolution among others.")
        adviceList.add("Habitat ecology is the type of natural environment in which a particular species of an organism live, characterized by both physical and biological features.")
        adviceList.add("An organism free from the interference of other species and can use a full range of biotic and abiotic resources in which it can survive and reproduce is known as its fundamental niche.")
    }
}