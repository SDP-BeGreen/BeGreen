package com.github.sdp_begreen.begreen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun nameWrittenCorrectly() {
        onView(withId(R.id.mainName))
            .perform(typeText("David"))
            .check(matches(withText("David")))
    }

    @Test
    fun intentCorrectlyFiredWhenButtonPressed() {
        Intents.init()

        // Type the name
        onView(withId(R.id.mainName))
            .perform(typeText("David"))
            .perform(closeSoftKeyboard())

        // Perform the click on the button
        onView(withId(R.id.mainButton))
            .perform(click())

        // Assert correctness of values
        intended(allOf(
            hasExtraWithKey("name"),
            hasExtra("name", "David"),
            hasComponent(GreetingActivity::class.java.name)))

        Intents.release()
    }

    @Test
    fun intentCorrectlyFiredWhenQueryButtonPressed() {
        Intents.init()

        // Perform the click on the button
        onView(withId(R.id.buttonDB))
            .perform(click())

        // Assert correctness of values
        intended(allOf(hasComponent(DatabaseActivity::class.java.name)))

        Intents.release()
    }

}