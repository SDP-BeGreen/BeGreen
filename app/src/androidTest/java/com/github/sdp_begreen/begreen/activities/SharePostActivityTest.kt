package com.github.sdp_begreen.begreen.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import com.github.sdp_begreen.begreen.R
import org.junit.After
import org.junit.Before
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.intent.IntentStubber
import com.google.android.material.snackbar.Snackbar
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SharePostActivityTest {

    private val image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    private val intentWithCorrectExtra = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
        this.putExtra(AddNewPostActivity.EXTRA_IMAGE_BITMAP, image)
    }

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

/*
    @Test
    fun intentWithNoExtraImageThrowsIllegalArgumentException() {

        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java)

        // TODO : Je ne comprends pas pourquoi le test fail en disant que l'exception est lancée

        // Should raise an IllegalArgumentException because we launch an activity without extra in the intent
        assertThrows(IllegalArgumentException::class.java) {
            launchActivity<SharePostActivity>(intent)
        }
    }

    @Test
    fun intentWithNullExtrasIllegalArgumentException() {

        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.replaceExtras(null)
        }

        // TODO : Je ne comprends pas pourquoi le test fail en disant que l'exception est lancée

        assertThrows(IllegalArgumentException::class.java) {
            launchActivity<SharePostActivity>(intent)
        }
    }*/


    @Test
    fun postTitleWrittenCorrectly() {

        // We launch an activity with correct extra in intent
        val activity = launchActivity<SharePostActivity>(intentWithCorrectExtra)

        val title = "This is my title"

        onView(withId(R.id.postTitleEditText))
            .perform(typeText(title))
            .check(matches(withText(title)))

        activity.close()
    }

/*
    @Test
    fun postImageViewDisplaysCorrectBitmap() {

        val activity = launchActivity<SharePostActivity>(intentWithCorrectExtra)



        activity.close()
    }*/

    /*

    TODO once we implement the sharing process with the database

    @Test
    fun sharePostSendsPostToDatabase() {

        val activity = launchActivity<SharePostActivity>(intentWithCorrectExtra)

        activity.close()
    }
     */

    @Test
    fun sharePostFinishesActivity() {

        val activity = launchActivity<SharePostActivity>(intentWithCorrectExtra)

        onView(withId(R.id.sharePostBtn))
            .perform(click())

        // Check that the activity has finished, so we go back to the previous
        assertTrue(activity.state.isAtLeast(Lifecycle.State.DESTROYED))

        activity.close()
    }
}