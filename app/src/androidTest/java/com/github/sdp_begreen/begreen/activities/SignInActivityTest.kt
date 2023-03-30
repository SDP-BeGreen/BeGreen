package com.github.sdp_begreen.begreen.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.social.GoogleAuth
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
        onView(withId(R.id.signInGoogleLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun googleSignInActivityIsDisplayedAfterLayoutClicked() {
        // Click the google sign in button
        onView(withId(R.id.signInGoogleLayout)).perform(click())

        // Check if the google sign in action is opened
        intended(hasAction(GoogleAuth.googleSignIn().action))
    }

}