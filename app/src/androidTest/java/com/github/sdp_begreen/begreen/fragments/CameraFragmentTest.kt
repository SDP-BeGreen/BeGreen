package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.SharePostActivity
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraFragmentTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single {db}
            single {auth}
        })
    )

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    companion object {

        private val db: DB = mock(DB::class.java)
        private val auth: Auth = mock(Auth::class.java)

        @BeforeClass
        @JvmStatic
        fun setUpMockito() {
            runTest {
                `when`(db.getAllUsers()).thenReturn(listOf())
                `when`(db.getFollowedIds("current user")).thenReturn(listOf())

                `when`(auth.getConnectedUserId()).thenReturn("current user")
            }
        }
    }

    private val fragment = CameraFragment.newInstance()
    private val image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private val correctCameraResponseIntent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
        this.putExtra("data", image)
    }


    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun startCameraIntentIfCameraPermissionGrantedWhenAddNewPostButtonClicked() {

        // @get:Rule granted the CAMERA permission. Unfortunately, it is impossible to revoke or clear the permissions
        // in a secure way, so the other path cannot be tested. The only alternative to clear permission is to execute
        // shell command, which is a trick that doesn't always work. So we will test only the granted path.

        launchFragmentInContainer { fragment }

        // Click the add new post button
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Check if the camera intent is opened
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @Test
    fun startShareActivityAfterTakingPhotoWithCameraAndGettingResultOK() {

        launchFragmentInContainer { fragment }

        // Define a correct result that the camera should return
        val cameraResult = Instrumentation.ActivityResult(Activity.RESULT_OK, correctCameraResponseIntent)

        // We expect that the camera activity will return "cameraResult"
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(cameraResult)

        // Click the add new post button to start the camera Activity
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Verify that the SharePostActivity has been launched
        Intents.intended(
            allOf(
                IntentMatchers.hasComponent(SharePostActivity::class.java.name),
                IntentMatchers.hasExtra(CameraFragment.EXTRA_IMAGE_BITMAP, image)
            )
        )
    }

    @Test
    fun doNotStartShareActivityAfterCancelingPhotoTakenWithCamera() {

        launchFragmentInContainer { fragment }

        // Define a correct result that the camera should return
        val cameraResult = Instrumentation.ActivityResult(Activity.RESULT_CANCELED, correctCameraResponseIntent)

        // We expect that the camera activity will return "cameraResult"
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(cameraResult)

        // Click the add new post button to start the camera Activity
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Check that we resume the previous activity.
        // When debugging, I noticed that for this test the value of state was RESUMED, while the state
        // of the test startShareActivityAfterTakingPhotoWithCameraAndGettingResultOK was CREATED.
        // Since I reverse engineered this test I verified that the output doesn't depend on the time by inserting random thread.sleep
        // This test is always deterministic no matter the time.
        MatcherAssert.assertThat(
            fragment.lifecycle.currentState,
            equalTo(Lifecycle.State.RESUMED)
        )
    }


    @Test
    fun doNotStartShareActivityAfterTakingPhotoWithCameraButGettingNullIntent()
    {
        launchFragmentInContainer { fragment }

        // Define a correct result that the camera should return
        val cameraResult = Instrumentation.ActivityResult(Activity.RESULT_OK, null)

        // We expect that the camera activity will return "cameraResult"
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(cameraResult)

        // Click the add new post button to start the camera Activity
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Check that we resume the previous activity.
        MatcherAssert.assertThat(
            fragment.lifecycle.currentState,
            equalTo(Lifecycle.State.RESUMED)
        )
    }

    @Test
    fun doNotStartShareActivityAfterTakingPhotoWithCameraButGettingNullExtras() {

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            SharePostActivity::class.java
        ).apply {
            this.replaceExtras(null)
        }

        launchFragmentInContainer { fragment }

        // Define a correct result that the camera should return
        val cameraResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        // We expect that the camera activity will return "cameraResult"
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(cameraResult)

        // Click the add new post button to start the camera Activity
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Check that we resume the previous activity.
        assertThat(
            fragment.lifecycle.currentState,
            equalTo(Lifecycle.State.RESUMED)
        )
    }

    @Test fun searchBarCorrectlyDisplaysWrittenText() {
        runBlocking{
            `when`(db.getAllUsers()).thenReturn(listOf())
            launchFragmentInContainer { fragment }

            // Verify that the AutoCompleteTextView now contains the selected item
            onView(withId(R.id.userSearch)).perform(typeText("blabla!"))
            onView(withId(R.id.userSearch)).check(matches(withText("blabla!")))
        }
    }

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
}
