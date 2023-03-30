package com.github.sdp_begreen.begreen.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.social.GoogleAuth
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignInActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignInActivity::class.java)

    @Test
    fun onCreate() {
        Espresso.onView(ViewMatchers.withId(R.id.signInGoogleLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun googleSignInActivityIsDisplayedAfterLayoutClicked() {
        // Click the add new post button
        onView(withId(R.id.signInGoogleLayout)).perform(click())

        // Check if the camera intent is opened
        intended(hasAction(GoogleAuth.googleSignIn().action))

    }

    @Test
    fun testSignIn() {
        val image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        val correctCameraResponseIntent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            this.putExtra("data", image)
        }
        // Define a correct result that the camera should return
        val cameraResult =
            Instrumentation.ActivityResult(Activity.RESULT_OK, correctCameraResponseIntent)

        // We expect that the camera activity will return "cameraResult"
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(cameraResult)

        // Click the add new post button to start the camera Activity
        onView(withId(R.id.addNewPostBtn)).perform(ViewActions.click())

        // Verify that the camera activity was actually launched
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // Verify that the SharePostActivity has been launched
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name),
                //IntentMatchers.hasExtra(CameraFragment.EXTRA_IMAGE_BITMAP, image)
            )
        )
    }
}