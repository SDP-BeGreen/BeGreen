package com.github.sdp_begreen.begreen.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith


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
    fun startCameraIntentIfCameraPermissionGrantedWhenAddNewPostButtonClicked() {

        // @get:Rule granted the CAMERA permission. Unfortunately, it is impossible to revoke or clear the permissions
        // in a secure way, so the other path cannot be tested. The only alternative to clear permission is to execute
        // shell command, which is a trick that doesn't always work. So we will test only the granted path.

        // Click the add new post button
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Check if the camera intent is opened
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @Test
    fun startShareActivityAfterTakingPhotoWithCameraAndGettingResultOK()
    {
        // Call the onActivityResult from the Camera activity method with the taken picture as extra
        val result = ActivityResult(AppCompatActivity.RESULT_OK, correctCameraResponseIntent)

        activityRule.scenario.onActivity { activity ->
            activity.onCameraActivityResult(result)
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
    fun doNotStartShareActivityAfterCancelingPhotoTakenWithCamera()
    {
        // Call the onActivityResult after cancelling the Camera activity
        val result = ActivityResult(AppCompatActivity.RESULT_CANCELED, correctCameraResponseIntent)

        activityRule.scenario.onActivity { activity ->
            activity.onCameraActivityResult(result)
        }

        // Check that we resume the previous activity.
        // When debbuging, we noticed that for this test the value of state was RESUMED, while the state
        // of the test startShareActivityAfterTakingPhotoWithCameraAndGettingResultOK was CREATED. The state seems to be independent
        // of the time since we tested at different instants with thread.sleep.
        assertThat(activityRule.scenario.state, equalTo(Lifecycle.State.RESUMED))
    }


    @Test
    fun doNotStartShareActivityAfterTakingPhotoWithCameraButGettingNullIntent()
    {
        val result = ActivityResult(AppCompatActivity.RESULT_OK, null)

        activityRule.scenario.onActivity { activity ->
            activity.onCameraActivityResult(result)
        }

        assertThat(activityRule.scenario.state, equalTo(Lifecycle.State.RESUMED))
    }

    @Test
    fun doNotStartShareActivityAfterTakingPhotoWithCameraButGettingNullExtras()
    {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.replaceExtras(null)
        }

        val result = ActivityResult(AppCompatActivity.RESULT_OK, intent)

        activityRule.scenario.onActivity { activity ->
            activity.onCameraActivityResult(result)
        }

        assertThat(activityRule.scenario.state, equalTo(Lifecycle.State.RESUMED))
    }
}