package com.github.sdp_begreen.begreen.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
@LargeTest
class AddNewPostActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddNewPostActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

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
    fun clickAddNewPostBtn_StartsCameraIntentIfCameraPermissionGranted() {

        // @get:Rule granted the CAMERA permission. Unfortunately, it is impossible to revoke or clear the permissions
        // in a secure way, so the other path cannot be tested. The only alternative to clear permission is to execute
        // shell command, which is a trick that doesn't always work. So we will test only the granted path.

        // Click the add new post button
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Check if the camera intent is opened
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @Test
    fun onActivityResult_OK_FromCameraDisplaysShareActivity()
    {
        // Call the onActivityResult from the Camera activity method with the taken picture as extra
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(
                AddNewPostActivity.REQUEST_IMAGE_CAPTURE,
                AppCompatActivity.RESULT_OK,
                correctCameraResponseIntent
            )
        }

        // Verify that the SharePostActivity was started with the correct intent and extras
        intended(
            allOf(
                hasComponent(SharePostActivity::class.java.name),
                hasExtra(AddNewPostActivity.EXTRA_IMAGE_BITMAP, image)
            )
        )
    }

    @Test
    fun onActivityResult_CANCELED_FromCameraDoesntDisplayShareActivity()
    {
        // Call the onActivityResult after cancelling the Camera activity
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(
                AddNewPostActivity.REQUEST_IMAGE_CAPTURE,
                AppCompatActivity.RESULT_CANCELED,
                Intent()
            )
        }

        // Check that we resume the previous activity.
        // When debbuging, we noticed that for this test the value of state was RESUMED, while the state
        // of the test onActivityResult_OK_FromCameraDisplaysShareActivity was CREATED. The state seems to be independent
        // of the time since we tested at different instants with thread.sleep.
        assertTrue(activityRule.scenario.state.isAtLeast(Lifecycle.State.RESUMED))
    }

    @Test
    fun onActivityResult_nullIntent_FromCameraDoesntDisplayShareActivity()
    {
        // Call the onActivityResult after cancelling the Camera activity
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(
                AddNewPostActivity.REQUEST_IMAGE_CAPTURE,
                AppCompatActivity.RESULT_OK,
                null
            )
        }

        assertTrue(activityRule.scenario.state.isAtLeast(Lifecycle.State.RESUMED))
    }

    @Test
    fun onActivityResult_nullExtras_FromCameraDoesntDisplayShareActivity()
    {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.replaceExtras(null)
        }

        // Call the onActivityResult after cancelling the Camera activity
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(
                AddNewPostActivity.REQUEST_IMAGE_CAPTURE,
                AppCompatActivity.RESULT_OK,
                intent
            )
        }

        assertTrue(activityRule.scenario.state.isAtLeast(Lifecycle.State.RESUMED))
    }

    @Test
    fun onActivityResult_notFromCamera_DoesntDisplayShareActivity()
    {
        // Call the onActivityResult after cancelling the Camera activity
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(
                AddNewPostActivity.REQUEST_IMAGE_CAPTURE + 1,
                AppCompatActivity.RESULT_OK,
                correctCameraResponseIntent
            )
        }

        assertTrue(activityRule.scenario.state.isAtLeast(Lifecycle.State.RESUMED))
    }

    @Test
    fun onActivityResult_notBitmapResult_FromCameraDoesntDisplayShareActivity()
    {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.putExtra("data", "Hello")
        }

        // Call the onActivityResult after cancelling the Camera activity
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(
                AddNewPostActivity.REQUEST_IMAGE_CAPTURE,
                AppCompatActivity.RESULT_OK,
                intent
            )
        }

        assertTrue(activityRule.scenario.state.isAtLeast(Lifecycle.State.RESUMED))
    }
}