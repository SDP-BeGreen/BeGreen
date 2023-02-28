package com.github.sdp_begreen.begreen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
@LargeTest
class DatabaseActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(DatabaseActivity::class.java)

    /*
    Implementation of a local database
     */
    class LocalDataBase : Database() {
        private val map: HashMap<String, String> = HashMap()
        override fun get(key: String): CompletableFuture<String> {
            return CompletableFuture.completedFuture(map[key])
        }

        override fun set(key: String, value: String) {
            map[key] = value
        }
    }

    @Before
    fun before() {
        // Change the used database to the local database
        Database.db = LocalDataBase()
    }

    @Test
    fun emailWrittenCorrectly() {
        onView(withId(R.id.emailDB))
            .perform(typeText("email@example.com"))
            .check(matches(withText("email@example.com")))
    }

    @Test
    fun phoneNumberWrittenCorrectly() {
        onView(withId(R.id.phoneDB))
            .perform(typeText("1112223344"))
            .check(matches(withText("1112223344")))
    }

    @Test
    fun getWithInexistantKeyReturnsNothing() {
        // Type phone number
        onView(withId(R.id.phoneDB))
            .perform(typeText("1"))
        // Press get button
        onView(withId(R.id.getButton))
            .perform(click())
        // Check that no email is linked to that phone number
        onView(withId(R.id.emailDB))
            .check(matches(withText("")))
    }

    @Test
    fun setCorrectlyUpdatesDatabase() {
        // Set value "email@example.com" for key 123
        onView(withId(R.id.phoneDB))
            .perform(typeText("123"))
        onView(withId(R.id.emailDB))
            .perform(typeText("email@example.com"))
        onView(withId(R.id.setButton))
            .perform(click())

        // Check that value return by "get" for key 123 is "email@example.com"
        onView(withId(R.id.getButton))
            .perform(click())
        onView(withId(R.id.emailDB))
            .check(matches(withText("email@example.com")))
    }
}