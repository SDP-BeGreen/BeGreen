package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.fragments.FollowersFragment.Companion.ARG_COLUMN_COUNT
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
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
class FollowersFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<FollowersFragment>

    companion object {

        private val db: DB = Mockito.mock(DB::class.java)
        private val auth: Auth = Mockito.mock(Auth::class.java)
        val followers = arrayListOf(
            User("id1", 1, "User1"),
            User("id2", 2, "User2"),
            User("id3", 3, "User3"),
            User("id2", 4, "User4"),
            User("id2", 5, "User5")
        )

        @BeforeClass
        @JvmStatic
        fun setUp() {
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                whenever(auth.getConnectedUserId()).thenReturn("1")
                whenever(db.getFollowers("1")).thenReturn(followers)
            }
        }
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
        })
    )

    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer { FollowersFragment.newInstance(1) }
    }

    @Test
    fun onCreateViewWithValidViewReturnsView() {
        // Set up fragment arguments
        val args = Bundle().apply {
            putInt(ARG_COLUMN_COUNT, 1)
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

        runTest {

            // Wait for the fragment to be created
            fragmentScenario.onFragment { fragment ->
                val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.user_fragment)

                // Verify that the RecyclerView is not null
                assertThat(recyclerView, notNullValue())

                // Verify that the RecyclerView has the correct number of items
                assertThat(
                    recyclerView?.adapter?.itemCount,
                    equalTo(followers.size)
                )

                // Verify that the RecyclerView contains all followers in the same order
                for (i in 0 until recyclerView!!.adapter!!.itemCount) {
                    assertThat(
                        (recyclerView.adapter as UserViewAdapter).users?.get(i) ?: 0,
                        equalTo(followers[i])
                    )
                }
            }
        }
    }
}