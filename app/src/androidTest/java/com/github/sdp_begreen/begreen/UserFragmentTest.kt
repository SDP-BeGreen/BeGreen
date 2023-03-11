package com.github.sdp_begreen.begreen
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.LinearLayoutManager
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.fragments.UserFragment
import com.github.sdp_begreen.begreen.fragments.UserViewAdapter
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
    val activityRule = ActivityScenarioRule(FragmentActivity::class.java)


    private lateinit var fragment: UserFragment
    private lateinit var userList: List<User>

    @Before
    fun setup() {
        // Initialize test data
        userList = listOf(
            User(0,"Alice", 10),
            User(1,"Bob", 20),
            User(2,"Charlie", 15)
        )
    }
    @Before
    fun setUp() {
        // Create a new instance of the fragment with test arguments.
        fragment = UserFragment.newInstance(2, listOf(User(0,"John", 10), User(1,"Jane", 8)), true)
    }
    @Test
    fun onCreateView_withValidView_returnsView() {
        // Set up the inflater and container to create a valid view.
        val inflater = LayoutInflater.from(ApplicationProvider.getApplicationContext())
        val container = FrameLayout(ApplicationProvider.getApplicationContext())
        val view = fragment.onCreateView(inflater, container, null)
        assertNotNull(view)
        assertTrue(view is RecyclerView)
    }

    @Test
    fun onCreateView_withUnsortedList_showsUnsortedList() {
        // Set up the inflater and container to create a valid view.
        val inflater = LayoutInflater.from(ApplicationProvider.getApplicationContext())
        val container = FrameLayout(ApplicationProvider.getApplicationContext())
        val view = fragment.onCreateView(inflater, container, null)
        // Check that the adapter shows the unsorted list of users.
        val adapter = (view as RecyclerView).adapter as UserViewAdapter
        assertEquals(listOf(User(0,"John", 10), User(1,"Jane", 8)), adapter.users)
    }
    @Test
    fun onCreateView_withSortedList_showsSortedList() {
        // Set up the inflater and container to create a valid view.
        val inflater = LayoutInflater.from(ApplicationProvider.getApplicationContext())
        val container = FrameLayout(ApplicationProvider.getApplicationContext())
        val view = fragment.onCreateView(inflater, container, null)
        // Check that the adapter shows the sorted list of users.
        val adapter = (view as RecyclerView).adapter as UserViewAdapter
        assertEquals(listOf(User(0,"John", 10), User(1,"Jane", 8)), adapter.users)
    }
    @Test
    fun testRecyclerViewAdapter() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(UserFragment.ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(UserFragment.ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.userlist)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct layout manager
            assertThat(recyclerView?.layoutManager?.javaClass ?: "", equalTo(LinearLayoutManager(ApplicationProvider.getApplicationContext()).javaClass))

            // Verify that the RecyclerView has the correct number of items
            assertThat(recyclerView?.adapter?.itemCount, equalTo(userList.size))
        }
    }

    @Test
    fun testRecyclerViewAdapterOnTwoColumns() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(UserFragment.ARG_COLUMN_COUNT, 2)
            putParcelableArrayList(UserFragment.ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.userlist)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct layout manager
            assertThat(recyclerView?.layoutManager?.javaClass ?: "", equalTo(GridLayoutManager(ApplicationProvider.getApplicationContext(),1).javaClass))
        }
    }

    @Test
    fun testSortByScore() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(UserFragment.ARG_COLUMN_COUNT, 1)
            putBoolean(UserFragment.ARG_IS_LIST_SORTED_BY_SCORE, true)
            putParcelableArrayList(UserFragment.ARG_USER_LIST, userList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.userlist)

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
    }
}
