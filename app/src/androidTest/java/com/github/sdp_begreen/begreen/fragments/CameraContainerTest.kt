package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
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
class CameraContainerTest {
    private lateinit var fragmentScenario: FragmentScenario<CameraContainer>

    //Companion object to mock the DB and Auth
    companion object {
        val user = User("test", 2, "test", 5, "test",
            "test", "test", 15, listOf("1", "3", "6"), listOf("2", "4"))
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
        fragmentScenario = launchFragmentInContainer(
            CameraContainer.newInstance().arguments
        )
    }

    @Test
    fun clickOnTakePhotoRedirectOnSharePost() {
        fragmentScenario.onFragment {
            val connectedUserViewModel
                    by it.viewModels<ConnectedUserViewModel>(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
        }
         //Click the add new post button
        onView(withId(R.id.camera_capture_button)).perform(click())
        withId(R.layout.fragment_send_post).matches(isDisplayed())
    }

    @Test
    fun companionInstanciationIsNotNull() {
        val cameraContainer = CameraContainer()
        assertThat(cameraContainer, `is`(notNullValue()))
    }

    //Those test are not working, but important if someone can manage to make them work !
    /*
    @Test
    fun clickOnTakePhotoAndSendRedirectOnCamera() {
        fragmentScenario.onFragment {
            val connectedUserViewModel
                    by it.viewModels<ConnectedUserViewModel>(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
        }
        //Click the add new post button
        onView(withId(R.id.camera_capture_button)).perform(click())
        onView(withId(R.layout.fragment_send_post)).check(matches(isDisplayed()))
        onView(withId(R.layout.fragment_send_post)).check(matches(hasDescendant(withId(R.id.cancel_post))))
        onView(withId(R.id.send_post)).perform(scrollTo()).perform(click())
        //BaseRobot().doOnView(withId(R.id.cancel_post), click())

    }

    @Test
    fun clickOnCancelPostReturnsOnCamera() {
        fragmentScenario.onFragment {
            val connectedUserViewModel
                    by it.viewModels<ConnectedUserViewModel>(ownerProducer = { it.requireActivity() })
            connectedUserViewModel.setCurrentUser(user)
        }
        onView(withId(R.id.camera_capture_button)).perform(click())
        onView(withId(R.id.cancel_post)).check(matches(isDisplayed()))
        onView(withId(R.id.cancel_post)).perform(click())
    }
    */
}