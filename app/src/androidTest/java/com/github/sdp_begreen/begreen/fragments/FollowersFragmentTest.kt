package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.fragments.FollowersFragment.Companion.ARG_COLUMN_COUNT
import com.github.sdp_begreen.begreen.fragments.FollowersFragment.Companion.ARG_USER_LIST
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FollowersFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule()

    private lateinit var followers: ArrayList<User>

    @Before
    fun setup() {
        // Initialize test data
        followers = arrayListOf(
            User("id1", 1, "User1"),
            User("id2", 2, "User2"),
            User("id3", 3, "User3"),
            User("id2", 4, "User4"),
            User("id2", 5, "User5")
        )
    }

    @Test
    fun onCreateViewWithValidViewReturnsView() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_USER_LIST, followers)
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)
        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view
            assertThat(recyclerView, notNullValue())
            assertThat(recyclerView, instanceOf(RecyclerView::class.java))
        }
        scenario.close()
    }

    @Test
    fun viewCorrectlyDisplaysFollowersAndInTheRightOrder() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_USER_LIST, followers)
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.user_fragment)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct number of items
            assertThat(
                recyclerView?.adapter?.itemCount,
                equalTo(followers.size)
            )

            // Verify that the RecyclerView contains all followers in the same order
            for (i in 0 until recyclerView!!.adapter!!.itemCount) {
                MatcherAssert.assertThat(
                    (recyclerView.adapter as UserViewAdapter).users?.get(i) ?: 0,
                    equalTo(followers[i])
                )
            }
        }
        scenario.close()
    }
}