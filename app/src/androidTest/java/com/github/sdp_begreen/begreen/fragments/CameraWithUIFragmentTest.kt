package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
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
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraWithUIFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<CameraWithUIFragment>

    //Companion object to mock the DB and Auth
    companion object {
        val user = User("test", 2, "test", 5, "test",
            "test", "test", 15, listOf("1", "3", "6"), listOf("2", "4"))
        private val db: DB = Mockito.mock(DB::class.java)
        private val auth: Auth = Mockito.mock(Auth::class.java)
        val users = listOf(
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
                `when`(db.getUser(user.id))
                    .thenReturn(user)
                // add a small delay, just to be sure that it is triggered after initialization
                // and arrive second, after the initial null value
                // user between tests, by simply pushing a new userId
                `when`(auth.getFlowUserIds())
                    .thenReturn(MutableStateFlow(user.id))
                `when`(auth.getConnectedUserId())
                    .thenReturn(user.id)
                `when`(db.getAllUsers())
                    .thenReturn(users)
                `when`(db.getFollowedIds(user.id))
                    .thenReturn(user.following)
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
        onView(withId(R.id.img_switch_camera)).perform(click())
        onView(withId(R.id.camera_capture_button)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnSwitchTwoRedirectOnCamera() {
        // Click the switch cam
        onView(withId(R.id.img_switch_camera)).perform(click())
        onView(withId(R.id.img_switch_camera)).perform(click())
        onView(withId(R.id.camera_capture_button)).check(matches(isDisplayed()))
    }


    @Test
    fun clickOnMagnifyOpenTheTextSearch() {
        // Click the search btn
        onView(withId(R.id.search_cam)).perform(click())

        // Check that the text search is displayed
        onView(withId(R.id.userSearch)).check(matches(isDisplayed()))
    }

    @Test
    fun searchBarCorrectlyDisplaysWrittenText() {
        runBlocking{

            // Click the search btn
            onView(withId(R.id.search_cam)).perform(click())

            // Type in the search bar
            onView(withId(R.id.userSearch))
                .perform(typeText("blabla!"))
            // Verify that the AutoCompleteTextView now contains the selected item
            onView(withId(R.id.userSearch))
                .check(matches(withText("blabla!")))
        }
    }

    /* Test not working, will try to fix soon
    @Test
    fun searchBarDisplaysExpectedUsers() {
        runBlocking {

            val expectedResults = listOf("Alice", "Alain Berset", "Mister Alix")
            for (name in expectedResults){
                onView(withId(R.id.search_cam)).perform(click())
                // Type in the search bar
                onView(withId(R.id.userSearch))
                    .perform(clearText(), typeText("al"))

                // Wait for the dropdown list to be displayed and select the first item
                onView(withText(name))
                    .inRoot(RootMatchers.isPlatformPopup()).perform(click())
                // Verify that the AutoCompleteTextView now contains the selected item
                onView(withId(R.id.userSearch))
                    .check(matches(withText(name)))
                onView(withId(R.id.search_cam)).perform(click())
            }
        }
    }*/

    /* Cant manage to make Mockito work with functions that take arguments, idk why
    @Test fun searchBarDisplaysExpectedUsers() {
        runBlocking {

            val user1 = User("1", 123, "Alice")
            val user2 = User("2", 0, "Bob Zeu bricoleur")
            val user3 = User("3", 14, "Charlie Chaplin")
            val user4 = User("4", 23, "David Pujadas")
            val user5 = User("5", 10492, "Euler")
            val user6 = User("6", 1234, "Alain Berset")
            val user7 = User("7", 1235, "Mister Alix")

            val users = listOf(user1, user2, user3, user4, user5, user6, user7)

            `when`(db.getAllUsers()).thenReturn(users)

            launchFragmentInContainer { fragment }

            val expectedResults = listOf(user1, user6, user7)
            for (user in expectedResults)
                `when`(db.follow("current user", user.id)).then{}
            for (user in expectedResults){

                // Type in the search bar
                onView(withId(R.id.userSearch))
                    .perform(clearText(), typeText("al"))

                // Wait for the dropdown list to be displayed and select the first item
                onView(allOf(withText(user.displayName), withId(R.id.item_button))).inRoot(RootMatchers.isPlatformPopup()).perform(click())
                // Verify that the AutoCompleteTextView now contains the selected item
                //verify(db).follow("current user", user.id)

            }
        }
    }*/

    @Test
    fun newInstanceInstantiateTheFragment() {
        val fragment = CameraWithUIFragment.newInstance()
        assertThat(fragment, instanceOf(CameraWithUIFragment::class.java))
    }
}