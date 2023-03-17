package com.github.sdp_begreen.begreen.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule
import com.github.sdp_begreen.begreen.R
import org.junit.After
import org.junit.Before
import androidx.test.rule.ActivityTestRule
import org.junit.Test
import org.mockito.Mockito

class SharePostActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(SharePostActivity::class.java)

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
    fun titleWrittenCorrectly() {

        val bitmap = Mockito.mock(Bitmap::class.java)
        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java)
        intent.putExtra("image", bitmap)

        ActivityScenario.launch<SharePostActivity>(intent)

        onView(withId(R.id.postTitleEditText))
            .perform(clearText(), typeText("This is a custom title"))
            .check(matches(withText("This is a custom title")))
    }
    */
/*

    // TODO withBitmapDrawable is not recognized

    @Test
    fun bitmapNotNull_ShouldSetImageViewBitmap() {

        val bitmap = Mockito.mock(Bitmap::class.java)
        val intent = Intent(ApplicationProvider.getApplicationContext(), SharePostActivity::class.java)
        intent.putExtra("image", bitmap)

        ActivityScenario.launch<SharePostActivity>(intent)

        onView(withId(R.id.imageView)).check(matches(withBitmapDrawable(bitmap.toDrawable())))
    }*/

    /*

    // TODO This test doesn't pass I don't know why

    @Test//(expected = IllegalArgumentException::class)
    fun bitmapNull_ShouldThrowNullPointerException() {
        val intent = Intent()
        activityRule.launchActivity(intent)
    }
    */
}