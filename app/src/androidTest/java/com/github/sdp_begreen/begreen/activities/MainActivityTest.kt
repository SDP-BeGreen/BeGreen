package com.github.sdp_begreen.begreen.activities

import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    /*
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }
    */


    /*@Test
    fun nameWrittenCorrectly() {
        onView(withId(R.id.mainName))
            .perform(typeText("David"))
            .check(matches(withText("David")))
    }

    @Test
    fun intentCorrectlyFiredWhenButtonPressed() {

        // Type the name
        onView(withId(R.id.mainName))
            .perform(typeText("David"))
            .perform(closeSoftKeyboard())

        // Perform the click on the button
        onView(withId(R.id.mainButton))
            .perform(click())

        // Wait for UI thread to idle
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // Assert correctness of values
        intended(
            allOf(
                hasExtraWithKey("name"),
                hasExtra("name", "David"),
                hasComponent(GreetingActivity::class.java.name)
            )
        )
    }

    @Test
    fun intentCorrectlyFireFragmentButtonPressed() {

        onView(withId(R.id.fragmentTest))
            .perform(click())

        intended(hasComponent(FragmentActivity::class.java.name))
    }

    @Test
    fun intentCorrectlyFiredWhenQueryButtonPressed() {

        // Perform the click on the button
        onView(withId(R.id.mainQuery))
            .perform(click())

        // Assert correctness of values
        intended(hasComponent(DatabaseActivity::class.java.name))

    }*/
}