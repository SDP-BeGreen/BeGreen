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
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import junit.framework.TestCase.*
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
@LargeTest
class UserPhotoFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule()

    private lateinit var fragment: UserPhotoFragment
    private lateinit var photoList: List<PhotoMetadata>

    private val ARG_COLUMN_COUNT = "column-count"
    private val ARG_PHOTO_LIST = "photo-list"
    private val ARG_IS_FEED = "is-feed"

    @Before
    fun setup() {
        // Initialize test data
        val photos = listOf(
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique"),
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique"),
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique"),
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique"),
            PhotoMetadata("1","Look at me cleaning!", ParcelableDate(Date()), "0", "Organique")
        )
        photoList = photos

        // Create a new instance of the fragment with test arguments.
        fragment = UserPhotoFragment.newInstance(2, photos, true)
    }

    @Test
    fun onCreateViewFeedWithValidViewReturnsView() {
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_PHOTO_LIST, photoList.toCollection(ArrayList()))
            putBoolean(ARG_IS_FEED, true)
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserPhotoFragment::class.java, args)
        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view
            assertNotNull(recyclerView)
            assertTrue(recyclerView is RecyclerView)
        }
        scenario.close()
    }

    @Test
    fun onCreateViewWithValidViewReturnsView() {
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_PHOTO_LIST, photoList.toCollection(ArrayList()))
            putBoolean(ARG_IS_FEED, false)
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserPhotoFragment::class.java, args)
        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view
            assertNotNull(recyclerView)
            assertThat(recyclerView, instanceOf(RecyclerView::class.java))
        }
        scenario.close()
    }

    @Test
    fun onCreateViewWithListShowsList() {
        // Set up the inflater and container to create a valid view.
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_PHOTO_LIST, photoList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserPhotoFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val view = fragment.view
            // Check that the adapter shows the unsorted list of users.
            val adapter = (view as RecyclerView).adapter as UserPhotosViewAdapter
            assertEquals(photoList, adapter.photos)
        }
        scenario.close()
    }

    @Test
    fun testRecyclerViewAdapterOnOneColumn() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
            putParcelableArrayList(ARG_PHOTO_LIST, photoList.toCollection(ArrayList()))
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserPhotoFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.feed_list)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct layout manager
            assertThat(recyclerView?.layoutManager?.javaClass ?: "", equalTo(LinearLayoutManager(ApplicationProvider.getApplicationContext()).javaClass))

            // Verify that the RecyclerView has the correct number of items
            assertThat(recyclerView?.adapter?.itemCount, equalTo(photoList.size))
        }
        scenario.close()
    }

    @Test
    fun testRecyclerViewAdapterOnTwoColumns() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 2)
            putParcelableArrayList(ARG_PHOTO_LIST, photoList.toCollection(ArrayList()))
            putBoolean(ARG_IS_FEED, true)
        }

        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(UserPhotoFragment::class.java, args)

        // Wait for the fragment to be created
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.feed_list)

            // Verify that the RecyclerView is not null
            assertThat(recyclerView, CoreMatchers.notNullValue())

            // Verify that the RecyclerView has the correct layout manager
            assertThat(recyclerView?.layoutManager?.javaClass ?: "", equalTo(GridLayoutManager(ApplicationProvider.getApplicationContext(),1).javaClass))
        }
        scenario.close()
    }
}
