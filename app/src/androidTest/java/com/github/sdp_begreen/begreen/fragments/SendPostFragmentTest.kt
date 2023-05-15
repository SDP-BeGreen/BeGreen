package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.fragment.app.commit
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class SendPostFragmentTest {
    private lateinit var fragmentScenario: FragmentScenario<CameraContainer>

    //Companion object to mock the DB and Auth
    companion object {
        val user = User("test", 2, "test", 5, "test", "test", "test", 15)
        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)
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
                `when`(db.getAllUsers()).thenReturn(users)
                `when`(db.getFollowedIds(any(), any())).thenReturn(emptyList())
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
        fragmentScenario = launchFragmentInContainer { CameraContainer.newInstance() }
            .onFragment {
                it.parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.mainCameraFragmentContainer, SendPostFragment.newInstance(user.id))
                }
            }
    }

    @Test
    fun categoryIsDisplayed() {
        // Check if the category input is displayed
        onView(withId(R.id.post_category)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun previewImageIsDisplayed() {
        // Check if the description input is displayed
        onView(withId(R.id.preview)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun descriptionIsDisplayed() {
        // Check if the description input is displayed
        onView(withId(R.id.post_description)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun sendPostBtnIsDisplayed() {
        // Check if the category input is displayed
        onView(withId(R.id.send_post)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun typeInCategoryWorks() {
        // Check the category input
        onView(withId(R.id.post_category)).perform(typeText("test"))
        onView(withId(R.id.post_category)).check(matches(withText("test")))
    }

    @Test
    fun typeInDescriptionWorks() {
        // Check the description input
        onView(withId(R.id.post_description)).perform(typeText("test"))
        onView(withId(R.id.post_description)).check(matches(withText("test")))
    }

    @Test
    fun postPhotoDoesNotUpdateUserWhenDatabaseFailsToStoreImage() {

        runTest {

            // Mock the db to pretend it could not store the image
            `when`(db.addTrashPhoto(any(), any())).thenReturn(null)

            // Image of the post
            val postImage = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)

            // Set the image in the ImageView
            fragmentScenario.onFragment { fragment ->
                val rootView = fragment.view
                val imageView = rootView!!.rootView.findViewById<ImageView>(R.id.preview)
                imageView.setImageBitmap(postImage)
            }

            onView(withId(R.id.post_category))
                .perform(typeText("category"), closeSoftKeyboard())
            onView(withId(R.id.post_description))
                .perform(typeText("description"), closeSoftKeyboard())
            // Click to send the photo
            onView(withId(R.id.send_post)).perform(click())

            // Check that we correctly tried to post the photo
            verify(db, times(1)).addTrashPhoto(eq(postImage), any())
            // Check that we did update anything on the database, since we could not post the image
            verify(db, never()).addUser(any(), any())
        }

    }

    @Test
    fun postPhotoUpdateUserCorrectlyWhenDatabaseSucceedsToStoreImage() {

        runTest {

            var updatedScore = user.score
            var newTrashPhotoMetadata: TrashPhotoMetadata? = null

            // Mock the database to pretend it correctly stored the image
            `when`(db.addTrashPhoto(any(), any())).then {
                newTrashPhotoMetadata = it.arguments[1] as TrashPhotoMetadata
                updatedScore += newTrashPhotoMetadata!!.trashCategory!!.value
                newTrashPhotoMetadata
            }

            // Image of the post
            val postImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

            // Set the image in the ImageView
            fragmentScenario.onFragment { fragment ->
                val rootView = fragment.view
                val imageView = rootView!!.rootView.findViewById<ImageView>(R.id.preview)
                imageView.setImageBitmap(postImage)
            }

            onView(withId(R.id.post_category))
                .perform(typeText("category"), closeSoftKeyboard())
            onView(withId(R.id.post_description))
                .perform(typeText("description"), closeSoftKeyboard())
            // Click to send the photo
            onView(withId(R.id.send_post)).perform(click())

            // Check that we correctly tried to post the photo
            verify(db, times(1)).addTrashPhoto(eq(postImage), any())
            // Check that we updated the user with its new score and new trashPhotoMetadata list
            verify(db, times(1)).addUser(
                user.copy(
                    score = updatedScore,
                    trashPhotosMetadatasList = listOf(newTrashPhotoMetadata!!)
                ),
                user.id
            )
        }

    }

}