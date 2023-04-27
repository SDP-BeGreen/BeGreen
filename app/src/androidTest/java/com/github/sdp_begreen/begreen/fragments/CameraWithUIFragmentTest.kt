package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso
import androidx.test.espresso.EspressoException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.activities.MainActivityTest
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.scope.newScope
import org.koin.dsl.module
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraWithUIFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<CameraWithUIFragment>

    companion object {
        private val user = User("test", 2, "test", 5, null, "test", "test", "test", 15)
        private val db: DB = Mockito.mock(DB::class.java)
        private val auth: Auth = Mockito.mock(Auth::class.java)

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
            }
        }
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
            single { auth }
        })
    )

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)



    @Before
    fun setup() {

        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        fragmentScenario = launchFragmentInContainer()
    }

    @Test
    fun clickOnTakePhotoRedirectOnSharePost() {
        // Click the add new post button
        Espresso.onView(ViewMatchers.withId(R.id.camera_capture_button)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.post_background)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickOnSendPostRedirectOnCamera() {
        //fragmentScenario.onFragment {
        //    val connectedUserViewModel
        //            by it.viewModels<ConnectedUserViewModel>(ownerProducer = { it.requireActivity() })
        //    connectedUserViewModel.setCurrentUser(user)
        //}
        // Click the add new post button
        Espresso.onView(ViewMatchers.withId(R.id.camera_capture_button)).check(matches(ViewMatchers.isDisplayed())).perform(ViewActions.click()).check(matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.post_background)).check(matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.send_post)).check(matches(ViewMatchers.isDisplayed())).perform(ViewActions.click()).check(matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.camera_capture_button)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickOnSwitchRedirectOnCamera() {
        // Click the add new post button
        Espresso.onView(ViewMatchers.withId(R.id.img_switch_camera)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.camera_capture_button)).check(matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun clickOnMagnifyOpenTheTextSearch() {
        // Click the add new post button
        Espresso.onView(ViewMatchers.withId(R.id.search_cam)).perform(ViewActions.click())

        // Check that the text search is displayed
        Espresso.onView(ViewMatchers.withId(R.id.userSearch)).check(matches(ViewMatchers.isDisplayed()))
    }

//
    @Test
    fun clickOnProfileRedirectOnProfileDetails() {
        fragmentScenario.onFragment {
            val connectedUserViewModel
                    by it.viewModels<ConnectedUserViewModel>(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
            val fragmentManager = it.requireActivity().supportFragmentManager
            val supportFragmentManager = it.childFragmentManager
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CameraWithUIFragment>(R.id.mainFragmentContainer)
            }
            val fra = fragmentManager.findFragmentById(R.id.mainFragmentContainer)!!
            fragmentManager.beginTransaction().add(fra, "mainFragmentContainer").commit()
        }

        Espresso.onView(ViewMatchers.withId(R.id.profile_cam)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.fragment_profile_details)).check(matches(ViewMatchers.isDisplayed()))
    }
}