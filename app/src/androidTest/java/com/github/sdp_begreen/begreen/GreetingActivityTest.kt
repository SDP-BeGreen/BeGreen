package com.github.sdp_begreen.begreen

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GreetingActivityTest {
    @Test
    fun greetingActivityStart() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java)
        intent.putExtra("name", "David")
        val activity = ActivityScenario.launch<GreetingActivity>(intent)
        activity.use {
            onView(withId(R.id.grettingText)).check(matches(withText("Greetings David")))
        }
    }
}