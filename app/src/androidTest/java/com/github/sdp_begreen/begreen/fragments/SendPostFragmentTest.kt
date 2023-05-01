package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@LargeTest
class SendPostFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<SendPostFragment>

    //Companion object to mock the DB and Auth
    companion object {
        val user = User("test", 2, "test", 5, null, "test", "test", "test", 15)
        private val db: DB = Mockito.mock(DB::class.java)
        private val auth: Auth = Mockito.mock(Auth::class.java)
        val users = listOf<User>(
            User("1", 123, "Alice"),
            User("2", 0, "Bob Zeu bricoleur"),
            User("3", 14, "Charlie Chaplin"),
            User("4", 23, "David Pujadas"),
            User("5", 10492, "Euler"),
            User("6", 1234, "Alain Berset"),
            User("7", 1235, "Mister Alix")
        )

        // Setup the mock
        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUp() {
            runTest {
                // setup basic get user and getProfilePicture use in multiple tests
                Mockito.`when`(db.getUser(user.id))
                    .thenReturn(user)
                // add a small delay, just to be sure that it is triggered after initialization
                // and arrive second, after the initial null value
                // user between tests, by simply pushing a new userId
                Mockito.`when`(auth.getFlowUserIds())
                    .thenReturn(MutableStateFlow(user.id))
                Mockito.`when`(auth.getConnectedUserId())
                    .thenReturn(user.id)
                Mockito.`when`(db.getAllUsers()).thenReturn(users)
            }
        }
    }

    //Setup the activity rule
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    //Setup the koin test rule
    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
        })
    )

    //Grant permission to use the camera
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)


    //Setup the scenario
    @Before
    fun setup() {
        fragmentScenario = launchFragmentInContainer(
            fragmentArgs = Bundle().apply {
                putString("uri", user.id)
            }
        )
    }

    @Test
    fun categoryIsDisplayed() {
        // Check if the category input is displayed
        onView(withId(R.id.post_category)).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun DescriptionIsDisplayed() {
        // Check if the description input is displayed
        onView(withId(R.id.post_description)).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun clickCategoryWorks() {
        // Check the category input
        onView(withId(R.id.post_category)).perform(typeText("test"))
        onView(withId(R.id.post_category)).check(matches(withText("test")))
    }
    @Test
    fun clickDescriptionWorks() {
        // Check the description input
        onView(withId(R.id.post_description)).perform(typeText("test"))
        onView(withId(R.id.post_description)).check(matches(withText("test")))
    }

}