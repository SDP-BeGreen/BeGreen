package com.github.sdp_begreen.begreen.fragments
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.LinearLayoutManager
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UserFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule()

    private lateinit var fragment: UserFragment
    private lateinit var userList: List<User>

    val ARG_COLUMN_COUNT = "column-count"
    val ARG_IS_LIST_SORTED_BY_SCORE = "is-list-sorted-by-score"
    val ARG_USER_LIST = "user-list"

    @Before
    fun setup() {
        // Initialize test data
        userList = listOf(
            User("0", 10, "Alice"),
            User("1", 20, "Bob"),
            User("2", 15, "Charlie")
        )
        // Create a new instance of the fragment with test arguments.
        fragment = UserFragment.newInstance(2, listOf(User("0", 10, "John"), User("1", 8, "Jane")), true)
    }

    @Test
    fun onCreateViewWithValidViewReturnsView() {
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)
        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view
            assertNotNull(recyclerView)
            assertTrue(recyclerView is RecyclerView)
        }
        scenario.close()
    }

    @Test
    fun onCreateViewWithUnsortedListShowsUnsortedList() {
        // Set up the inflater and container to create a valid view.
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val view = fragment.view
            // Check that the adapter shows the unsorted list of users.
            val adapter = (view as RecyclerView).adapter as UserViewAdapter
            assertEquals(userList, adapter.users)
        }
        scenario.close()
    }
    @Test
    fun onCreateViewWithSortedListShowsSortedList() {
        // Set up the inflater and container to create a valid view.
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_USER_LIST, userList.toCollection(ArrayList()))
            putBoolean(ARG_IS_LIST_SORTED_BY_SCORE, true)
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val view = fragment.view
            // Check that the adapter shows the sorted list of users.
            val adapter = (view as RecyclerView).adapter as UserViewAdapter
            assertEquals(userList.sortedDescending(), adapter.users)
        }
        scenario.close()
    }
    @Test
    fun testRecyclerViewAdapterOnOneColumn() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.user_fragment)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct layout manager
            assertThat(recyclerView?.layoutManager?.javaClass ?: "", equalTo(LinearLayoutManager(ApplicationProvider.getApplicationContext()).javaClass))

            // Verify that the RecyclerView has the correct number of items
            assertThat(recyclerView?.adapter?.itemCount, equalTo(userList.size))
        }
        scenario.close()
    }

    @Test
    fun testRecyclerViewAdapterOnTwoColumns() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 2)
            putParcelableArrayList(ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.user_fragment)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct layout manager
            assertThat(recyclerView?.layoutManager?.javaClass ?: "", equalTo(GridLayoutManager(ApplicationProvider.getApplicationContext(),1).javaClass))
        }
        scenario.close()
    }

    @Test
    fun testSortByScore() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putBoolean(ARG_IS_LIST_SORTED_BY_SCORE, true)
            putParcelableArrayList(ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.user_fragment)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct number of items
            assertThat(recyclerView?.adapter?.itemCount, equalTo(userList.size))

            // Verify that the RecyclerView is sorted by score
            val sortedList = userList.sortedByDescending { it.score }
            for (i in 0 until recyclerView!!.adapter!!.itemCount) {
                assertThat((recyclerView.adapter as UserViewAdapter).users?.get(i) ?: 0, equalTo(sortedList[i]))
            }
        }
        scenario.close()
    }
}
