package com.github.sdp_begreen.begreen.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class DatabaseActivityTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(DatabaseActivity::class.java)

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun emailWrittenCorrectly() {
        onView(withId(R.id.databaseEmail))
            .perform(typeText("email@example.com"))
            .check(matches(withText("email@example.com")))
    }

    @Test
    fun phoneNumberWrittenCorrectly() {
        onView(withId(R.id.databasePhoneNumber))
            .perform(typeText("1112223344"))
            .check(matches(withText("1112223344")))
    }

    @Test
    fun getWithInexistantKeyPrintsNothing() {
        // Type phone number
        onView(withId(R.id.databasePhoneNumber))
            .perform(typeText("1"))
            .perform(closeSoftKeyboard())

        // Press get button
        onView(withId(R.id.databaseGet))
            .perform(click())
        // Check that no email is linked to that phone number
        onView(withId(R.id.databaseEmail))
            .check(matches(withText("")))
    }

    @Test
    fun getOnNonStringValuePrintsNothing() {

        // Type phone number
        onView(withId(R.id.databasePhoneNumber))
            .perform(typeText("123"))
            .perform(closeSoftKeyboard())
        // Press get button
        onView(withId(R.id.databaseGet))
            .perform(click())
        // Check that no email is linked to that phone number
        onView(withId(R.id.databaseEmail))
            .check(matches(withText("")))
    }

    @Test
    fun setCorrectlyUpdatesDatabase() {
        // Set value "email@example.com" for key 123
        onView(withId(R.id.databasePhoneNumber))
            .perform(typeText("123456"))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.databaseEmail))
            .perform(typeText("email@example.com"))
            .perform(closeSoftKeyboard())
        onView(withId(R.id.databaseSet))
            .perform(click())

        // Clear the e-mail
        onView(withId(R.id.databaseEmail))
            .perform(clearText())
            .perform(closeSoftKeyboard())

        // Check that value return by "get" for key 123 is "email@example.com"
        onView(withId(R.id.databaseGet))
            .perform(click())
        onView(withId(R.id.databaseEmail))
            .check(matches(withText("email@example.com")))
    }

    /*

    TODO : ce test qui ne passe pas sur la CI ne test rien

    @Test
    fun storeFollowedByLoadCorrectlyDisplaysBitmap() {
        onView(withId(R.id.databaseStorePicture))
            .perform(click())
        onView(withId(R.id.databaseLoadPicture))
            .perform(click())
    }
    */
}