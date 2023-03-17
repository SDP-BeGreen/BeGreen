package com.github.sdp_begreen.begreen.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdp_begreen.begreen.R
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
class AddNewPostActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddNewPostActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testAddNewImageBtn() {
        // Click the add new image button
        onView(withId(R.id.addNewPostBtn)).perform(click())

        // Check if the camera app is opened
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    @Test
    fun testOnActivityResult() {
        // Create a bitmap to be returned by the camera activity
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // Create an intent to simulate the result from the camera activity
        val resultIntent = Intent()
        resultIntent.putExtra("data", bitmap)

        // Call the onActivityResult method with the simulated result
        activityRule.scenario.onActivity { activity ->
            activity.onActivityResult(AddNewPostActivity.REQUEST_IMAGE_CAPTURE, AppCompatActivity.RESULT_OK, resultIntent)
        }

        // Verify that the SharePostActivity was started with the correct intent and extras
        intended(
            allOf(
                hasComponent(SharePostActivity::class.java.name),
                hasExtra("image", bitmap)
            )
        )
    }
/*
    @Test
    fun testCameraPermissionRequest() {

        /* TODO : Test the following cases

            revoke permissions -> ask -> accept
            revoke permissions -> ask -> deny
            permission already asked -> so we don't ask
         */

        // Launch the activity
        val scenario = launchActivity<AddNewPostActivity>()

        // Grant camera permission

        val permission = Manifest.permission.CAMERA
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        val permissionDenied = PackageManager.PERMISSION_DENIED
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(getTargetContext(), permission)

        if (cameraPermissionCheck == permissionDenied) {
            // Permission is not granted, so click the allow button when prompted

            // TODO This strings doesn't work on the CI
            val permissionDialogMatcher = withText("permission_camera")
            val allowButtonMatcher = withText("allow")
            onView(permissionDialogMatcher).check(matches(isDisplayed()))
            onView(allowButtonMatcher).perform(click())
        }

        // Verify that the permission is granted
        assertThat(ContextCompat.checkSelfPermission(getTargetContext(), permission), equalTo(permissionGranted))
    }*/
}