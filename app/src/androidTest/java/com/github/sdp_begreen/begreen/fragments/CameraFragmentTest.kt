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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.activities.SharePostActivity
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

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
        onView(withId(R.id.addNewPostBtn)).perform(ViewActions.click())

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
        onView(withId(R.id.addNewPostBtn)).perform(ViewActions.click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Verify that the SharePostActivity has been launched
        Intents.intended(
            Matchers.allOf(
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
        onView(withId(R.id.addNewPostBtn)).perform(ViewActions.click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Check that we resume the previous activity.
        // When debugging, I noticed that for this test the value of state was RESUMED, while the state
        // of the test startShareActivityAfterTakingPhotoWithCameraAndGettingResultOK was CREATED.
        // Since I reverse engineered this test I verified that the output doesn't depend on the time by inserting random thread.sleep
        // This test is always deterministic no matter the time.
        MatcherAssert.assertThat(
            fragment.lifecycle.currentState,
            Matchers.equalTo(Lifecycle.State.RESUMED)
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
        onView(withId(R.id.addNewPostBtn)).perform(ViewActions.click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Check that we resume the previous activity.
        MatcherAssert.assertThat(
            fragment.lifecycle.currentState,
            Matchers.equalTo(Lifecycle.State.RESUMED)
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
        onView(withId(R.id.addNewPostBtn)).perform(ViewActions.click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Check that we resume the previous activity.
        MatcherAssert.assertThat(
            fragment.lifecycle.currentState,
            Matchers.equalTo(Lifecycle.State.RESUMED)
        )
    }
}
