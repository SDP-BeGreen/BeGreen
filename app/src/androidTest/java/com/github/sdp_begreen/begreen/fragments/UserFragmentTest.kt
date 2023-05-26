package com.github.sdp_begreen.begreen.fragments
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class UserFragmentTest {

    val ARG_COLUMN_COUNT = "column-count"
    val ARG_IS_LIST_SORTED_BY_SCORE = "is-list-sorted-by-score"

    companion object {

        private val db: DB = Mockito.mock(DB::class.java)
        // Initialize test data
        private val userList = listOf(
            User("0", 10, "Alice"),
            User("1", 20, "Bob"),
            User("2", 15, "Charlie")
        )

        @BeforeClass
        @JvmStatic
        fun setUp() {
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                whenever(db.getAllUsers()).thenReturn(userList)
            }
        }
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
        })
    )

    @Test
    fun onCreateViewWithValidViewReturnsView() {
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
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
