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

        val groups = getGroups()

        adapter = ExpandableListAdapter(requireContext(), groups)
        expandableListView.setAdapter(adapter)

        return view
    }

    private fun getGroups(): List<Group> {
        val groupFood = Group(R.drawable.baseline_fastfood_24, "Food", listOf(Item("1.\t\"Eat food. Not too much. Mostly plants.\" - Michael Pollan"),
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
        val groupWater = Group(R.drawable.baseline_water_drop_24, "Water", listOf(Item("1.\t\"Thousands have lived without love, not one without water.\" - W. H. Auden"),
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
        val groupMoney = Group(R.drawable.baseline_monetization_on_24, "Income", listOf(Item("1.\t\"Economic growth without environmental damage is possible.\" - Indira Gandhi"),
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
        val groupEducation = Group(R.drawable.baseline_school_24, "Education", listOf(Item("1.\t\"An understanding of the natural world and what's in it is a source of not only a great curiosity but great fulfillment.\" - David Attenborough"),
            Item("2.\t\"Teaching children about the natural world should be seen as one of the most important events in their lives.\" - Thomas Berry"),
            Item("3.\t\"In the end, we will conserve only what we love; we will love only what we understand, and we will understand only what we are taught.\" - Baba Dioum"),
            Item("4.\t\"Environmental education is the key to empowering future generations to create a sustainable world.\" - David Orr"),
            Item("5.\t\"The fate of the living planet is the most important issue facing mankind. Solutions to one problem must be solutions for all.\" - Gaylord Nelson"),
            Item("6.\t\"Education is the most powerful weapon which you can use to change the world.\" - Nelson Mandela"),
            Item("7.\t\"If a child is to keep alive his inborn sense of wonder, he needs the companionship of at least one adult who can share it, rediscovering with him the joy, excitement, and mystery of the world we live in.\" - Rachel Carson"),
            Item("8.\t\"The best time to plant a tree was 20 years ago. The second best time is now.\" - Chinese Proverb"),
            Item("9.\t\"The environment is not separate from ourselves; we are inside it and it is inside us. We make it and it makes us.\" - Davi Kopenawa"),
            Item("10.\t\"Knowledge of the environment must be integrated into all levels of education to create environmentally conscious citizens.\" - Gro Harlem Brundtland")
        ))
        val groupResilience = Group(R.drawable.baseline_sports_martial_arts_24, "Resilience", listOf(
            Item("1.\t\"It is not the strongest of the species that survives, nor the most intelligent that survives. It is the one that is the most adaptable to change.\" - Charles Darwin"),
            Item("2.\t\"In nature, nothing is perfect, and everything is perfect. Trees can be contorted, bent in weird ways, and they're still beautiful.\" - Alice Walker"),
            Item("3.\t\"The environment is in us, not outside of us. The trees are our lungs, the rivers our bloodstream. We are all interconnected, and what you do to the environment, ultimately, you do to yourself.\" - Ian Somerhalder"),
            Item("4.\t\"Look deep into nature, and then you will understand everything better.\" - Albert Einstein"),
            Item("5.\t\"We don't inherit the earth from our ancestors; we borrow it from our children.\" - Native American Proverb"),
            Item("6.\t\"Nature is not a place to visit. It is home.\" - Gary Snyder"),
            Item("7.\t\"The environment is everything that isn't me.\" - Albert Einstein"),
            Item("8.\t\"In every walk with nature, one receives far more than he seeks.\" - John Muir"),
            Item("9.\t\"When we heal the earth, we heal ourselves.\" - David Orr"),
            Item("10.\t\"Adopt the pace of nature: her secret is patience.\" - Ralph Waldo Emerson")
        ))
        val groupVoice = Group(R.drawable.baseline_record_voice_over_24, "Voice", listOf(
            Item("1.\t\"Never doubt that a small group of thoughtful, committed citizens can change the world; indeed, it's the only thing that ever has.\" - Margaret Mead"),
            Item("2.\t\"The earth is what we all have in common.\" - Wendell Berry"),
            Item("3.\t\"The only way forward, if we are going to improve the quality of the environment, is to get everybody involved.\" - Richard Rogers"),
            Item("4.\t\"I am only one, but still I am one. I cannot do everything, but still, I can do something.\" - Edward Everett Hale"),
            Item("5.\t\"One individual cannot possibly make a difference, alone. It is individual efforts, collectively, that makes a noticeable difference—all the difference in the world!\" - Dr. Jane Goodall"),
            Item("6.\t\"We are the environment. The world is literally one biological process. The trees are our lungs. Look after the environment, and you look after yourself.\" - Jonathon Porritt"),
            Item("7.\t\"The environment is not a political issue; it's a matter of survival.\" - Sylvia Earle"),
            Item("8.\t\"It's the little things citizens do. That's what will make the difference. My little thing is planting trees.\" - Wangari Maathai"),
            Item("9.\t\"When one tugs at a single thing in nature, he finds it attached to the rest of the world.\" - John Muir"),
            Item("10.\t\"The power of the people is greater than the people in power.\" - Wael Ghonim")
        ))
        val groupJobs = Group(R.drawable.baseline_work_24, "Jobs", listOf(
            Item("1.\t\"A nation that destroys its soils destroys itself. Forests are the lungs of our land, purifying the air and giving fresh strength to our people.\" - Franklin D. Roosevelt"),
            Item("2.\t\"Sustainable development means ensuring that economic growth goes hand-in-hand with the protection of the environment and the well-being of society.\" - Gro Harlem Brundtland"),
            Item("3.\t\"Green jobs are not only desirable; they are also essential if we are to build a sustainable future.\" - Ban Ki-moon"),
            Item("4.\t\"Investing in green jobs will create opportunities for millions of people around the world.\" - Kofi Annan"),
            Item("5.\t\"The transition to a green economy offers a new engine of growth and employment while protecting our planet.\" - Achim Steiner"),
            Item("6.\t\"When we invest in the environment, we invest in jobs, health, and a better quality of life.\" - Antonio Guterres"),
            Item("7.\t\"We can create a sustainable, clean energy economy and solve the climate crisis while creating millions of jobs that can't be outsourced.\" - Al Gore"),
            Item("8.\t\"Green jobs are the jobs of the future.\" - Arnold Schwarzenegger"),
            Item("9.\t\"A green economy is about more than just green jobs; it's about building a just and equitable society.\" - Van Jones"),
            Item("10.\t\"The green jobs revolution will provide the opportunity for everyone to participate in building a more sustainable future.\" - Robert Redford")
        ))
        val groupEnergy = Group(R.drawable.baseline_electrical_services_24, "Energy", listOf(
            Item("1.\t\"Solar power is the last energy resource that isn't owned yet—nobody taxes the sun yet.\" - Bonnie Raitt"),
            Item("2.\t\"Energy and the environment are the two great challenges of the 21st century.\" - Steven Chu"),
            Item("3.\t\"We have the technology to harness the sun and wind, and we have the ability to create a clean energy future.\" - Al Gore"),
            Item("4.\t\"The use of renewable energy sources is essential for the health of our planet and the well-being of future generations.\" - Wangari Maathai"),
            Item("5.\t\"Transitioning to renewable energy is not only an investment in the future, but also an investment in our health, economy, and environment.\" - David Suzuki"),
            Item("6.\t\"The sun, the wind, and the tides are the energy of the future.\" - John F. Kennedy"),
            Item("7.\t\"We need a massive effort to harness the power of the sun, the wind, and the tides, and to develop clean, renewable sources of energy.\" - Barack Obama"),
            Item("8.\t\"Renewable energy is not just an alternative, it is our destiny.\" - David Attenborough"),
            Item("9.\t\"The future is green energy, sustainability, renewable energy.\" - Arnold Schwarzenegger"),
            Item("10.\t\"The world is moving toward renewable energy, and it's time we join the race.\" - Ban Ki-moon")
        ))
        val groupSocialEquity = Group(R.drawable.baseline_equalizer_24, "Social equity", listOf(
            Item("1.\t\"We must learn to live together as brothers or perish together as fools.\" - Martin Luther King Jr."),
            Item("2.\t\"The environment and social equity are interdependent. We cannot achieve one without the other.\" - Wangari Maathai"),
            Item("3.\t\"Social equity and environmental protection go hand in hand. We cannot achieve one without the other.\" - Gro Harlem Brundtland"),
            Item("4.\t\"In a world of increasing inequality, the legitimacy of institutions that give precedence to the property rights of 'the Haves' over the human rights of 'the Have Nots' is inevitably called into serious question.\" - Kofi Annan"),
            Item("5.\t\"Justice and sustainability both demand that we do not use more resources than we need.\" - Vandana Shiva"),
            Item("6.\t\"A healthy environment is essential for social equity and well-being.\" - Ban Ki-moon"),
            Item("7.\t\"We must recognize that environmental issues are social issues, deeply rooted in our systems of power and privilege.\" - David Suzuki"),
            Item("8.\t\"The struggle for environmental justice is also the struggle for social and economic justice.\" - Robert Bullard"),
            Item("9.\t\"The only sustainable future is one that includes everyone.\" - Julia Butterfly Hill"),
            Item("10.\t\"Environmental justice means that no community should be saddled with more environmental burdens and less environmental benefits than any other.\" - Majora Carter")
        ))
        val groupGenderEquality = Group(R.drawable.baseline_transgender_24, "Gender equality", listOf(
            Item("1.\t\"An understanding of the natural world and what's in it is a source of not only a great curiosity but great fulfillment.\" - David Attenborough"),
            Item("1.\t\"Women and the environment are closely interlinked, especially in developing countries. Empowering women is crucial to achieving sustainable development.\" - Gro Harlem Brundtland"),
            Item("2.\t\"When women are empowered and their rights are fulfilled, they become effective agents of change in protecting the environment.\" - Michelle Bachelet"),
            Item("3.\t\"To achieve both gender equality and environmental sustainability, we must recognize and address the linkages between the two.\" - Vandana Shiva"),
            Item("4.\t\"Women are the backbone of rural economies, and their access to resources and decision-making power is essential for environmental conservation.\" - Wangari Maathai"),
            Item("5.\t\"The environment is in crisis, and so are women's rights. We cannot solve one without solving the other.\" - Annie Leonard"),
            Item("6.\t\"Women's rights and the environment are two sides of the same coin.\" - Sylvia Earle"),
            Item("7.\t\"Gender equality is not a women's issue. It's a human issue. It affects us all.\" - Emma Watson"),
            Item("8.\t\"The health of the planet and the rights of women are interconnected. We can't have one without the other.\" - Jane Goodall"),
            Item("9.\t\"Empower a woman, and you empower a community. Empower a community, and you empower the environment.\" - Oprah Winfrey"),
            Item("10.\t\"When we empower women, we can change the world.\" - Melinda Gates")
        ))
        val groupHealth = Group(R.drawable.baseline_health_and_safety_24, "Health", listOf(
            Item("1.\t\"Our health is inextricably linked to the health of the environment.\" - Sylvia Earle"),
            Item("2.\t\"The health of the planet and the health of its people are one and the same.\" - David Suzuki"),
            Item("3.\t\"The environment is not separate from ourselves; we are inside it, and it is inside us. We make it, and it makes us.\" - Davi Kopenawa"),
            Item("4.\t\"We all have a responsibility to protect our environment for the sake of our own health and well-being, and that of future generations.\" - Gro Harlem Brundtland"),
            Item("5.\t\"It is health that is real wealth and not pieces of gold and silver.\" - Mahatma Gandhi"),
            Item("6.\t\"The environment is not just about saving the planet; it's about saving ourselves.\" - Paul Hawken"),
            Item("7.\t\"The health of the environment and the health of the human species are intimately interconnected. We cannot separate the two.\" - Vandana Shiva"),
            Item("8.\t\"Environmental health is a critical aspect of human health, and we must recognize its importance in order to protect the well-being of our communities.\" - Wangari Maathai"),
            Item("9.\t\"The greatest wealth is health, and a healthy environment is essential for a healthy life.\" - Virgil"),
            Item("10.\t\"Clean air, clean water, and a healthy environment are fundamental human rights.\" - Leonardo DiCaprio")
        ))
        return listOf(groupFood, groupWater, groupMoney, groupEducation, groupResilience, groupVoice, groupJobs, groupEnergy, groupSocialEquity, groupGenderEquality, groupHealth)
    }

    companion object {

        private const val QUOTES = "quotes"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param list of quotes.
         * @return A new instance of fragment AdviceFragment.
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