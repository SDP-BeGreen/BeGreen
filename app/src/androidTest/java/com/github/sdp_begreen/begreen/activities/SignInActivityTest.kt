package com.github.sdp_begreen.begreen.activities

import android.app.Instrumentation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.sdp_begreen.begreen.R
import junit.framework.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest

class SignInActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SignInActivity::class.java)


    @Test
    fun pressGoogleLogin() {

        Espresso.onView(ViewMatchers.withId(R.id.llGoogle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())


        val activityMonitor: Instrumentation.ActivityMonitor = getInstrumentation()
            .addMonitor(MainActivity::class.java.getName(), null, false)

        val targetActivity: MainActivity? =
            activityMonitor.waitForActivityWithTimeout(50000) as MainActivity? // By using ActivityMonitor

        assertNotNull("Target Activity is not launched", targetActivity)

    }


}