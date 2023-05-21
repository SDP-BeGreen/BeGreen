package com.github.sdp_begreen.begreen.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.firebase.auth.FirebaseAuth

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignInActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignInActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun testSignInWithCurrentUser() {
        // Simulate a signed-in user
        FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener {
            // Perform a click on the button that triggers the code snippet
            onView(withId(R.id.signInGoogleLayout)).perform(click())
        }
        FirebaseAuth.getInstance().signOut()
    }
    @Test
    fun onCreate() {
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signInGoogleLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}