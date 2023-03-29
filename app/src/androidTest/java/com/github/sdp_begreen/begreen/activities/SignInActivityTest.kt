package com.github.sdp_begreen.begreen.activities

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
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
}