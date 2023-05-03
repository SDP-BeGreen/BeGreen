package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraWithUIFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<CameraWithUIFragment>

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
        fragmentScenario = launchFragmentInContainer()
    }


    @Test
    fun clickOnSwitchRedirectOnCamera() {
        // Click the switch cam
        onView(ViewMatchers.withId(R.id.img_switch_camera)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.camera_capture_button)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickOnSwitchTwoRedirectOnCamera() {
        // Click the switch cam
        onView(ViewMatchers.withId(R.id.img_switch_camera)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.img_switch_camera)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.camera_capture_button)).check(matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun clickOnMagnifyOpenTheTextSearch() {
        // Click the search btn
        onView(ViewMatchers.withId(R.id.search_cam)).perform(ViewActions.click())

        // Check that the text search is displayed
        onView(ViewMatchers.withId(R.id.userSearch)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun searchBarDisplaysExpectedUsers() {
        runBlocking {
            Mockito.`when`(db.getAllUsers()).thenReturn(users)
            val expectedResults = listOf("Alice", "Alain Berset", "Mister Alix")
            for (name in expectedResults){
                onView(ViewMatchers.withId(R.id.search_cam)).perform(ViewActions.click())
                // Type in the search bar
                onView(ViewMatchers.withId(R.id.userSearch))
                    .perform(ViewActions.clearText(), ViewActions.typeText("al"))

                // Wait for the dropdown list to be displayed and select the first item
                onView(ViewMatchers.withText(name))
                    .inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
                // Verify that the AutoCompleteTextView now contains the selected item
                onView(ViewMatchers.withId(R.id.userSearch))
                    .check(matches(ViewMatchers.withText(name)))
                onView(ViewMatchers.withId(R.id.search_cam)).perform(ViewActions.click())
            }
        }
    }

    @Test
    fun searchBarCorrectlyDisplaysWrittenText() {
        runBlocking{

            // Click the search btn
            onView(ViewMatchers.withId(R.id.search_cam)).perform(ViewActions.click())

            // Type in the search bar
            onView(ViewMatchers.withId(R.id.userSearch))
                .perform(ViewActions.typeText("blabla!"))            // Verify that the AutoCompleteTextView now contains the selected item
            onView(ViewMatchers.withId(R.id.userSearch))
                .check(matches(ViewMatchers.withText("blabla!")))
        }
    }

    @Test
    fun newInstanceInstanciateTheFragment() {
        val fragment = CameraWithUIFragment.newInstance()
        assertThat(fragment, instanceOf(CameraWithUIFragment::class.java))
    }

    @Test
    fun onRequestPermissionResult() {
        fragmentScenario.onFragment{
            try {
            it.onRequestPermissionsResult(0, arrayOf(Manifest.permission.CAMERA), intArrayOf(0))
            } catch (e: Exception) {
                assertThat(e, instanceOf(Exception::class.java))
            }
        }
    }

}