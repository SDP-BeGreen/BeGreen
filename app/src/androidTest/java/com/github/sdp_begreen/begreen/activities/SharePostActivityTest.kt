package com.github.sdp_begreen.begreen.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.fragments.CameraFragment
import org.hamcrest.Matchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class SharePostActivityTest {

    private val image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    private val intentWithCorrectExtra = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
        this.putExtra(CameraFragment.EXTRA_IMAGE_BITMAP, image)
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
    fun intentWithNoExtraImageFinishesActivity() {

        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.putExtra("Hello", "Hello")
        }
        val activity = launchActivity<SharePostActivity>(intent)

        assertThat(activity.state, equalTo(Lifecycle.State.DESTROYED))
    }

    fun intentWithExtraNotBitmapFinishesActivity() {

        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.putExtra(CameraFragment.EXTRA_IMAGE_BITMAP, "Hello")
        }
        val activity = launchActivity<SharePostActivity>(intent)

        assertThat(activity.state, equalTo(Lifecycle.State.DESTROYED))
    }

    fun intentWithNullExtraFinishesActivity() {

        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java).apply {
            this.replaceExtras(null)
        }
        val activity = launchActivity<SharePostActivity>(intent)

        assertThat(activity.state, equalTo(Lifecycle.State.DESTROYED))
    }

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

    @Test
    fun sharePostFinishesActivity() {

        val activity = launchActivity<SharePostActivity>(intentWithCorrectExtra)

        onView(withId(R.id.sharePostBtn))
            .perform(click())

        // Check that the activity has finished, so we go back to the previous
        assertThat(activity.state, equalTo(Lifecycle.State.DESTROYED))
    }

    /*

    TODO once we implement the sharing process with the database

    @Test
    fun sharePostSendsPostToDatabase() {

        val activity = launchActivity<SharePostActivity>(intentWithCorrectExtra)

        activity.close()
    }
     */
}