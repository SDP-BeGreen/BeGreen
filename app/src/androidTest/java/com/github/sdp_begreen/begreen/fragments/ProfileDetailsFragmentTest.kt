package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdp_begreen.begreen.*
import com.github.sdp_begreen.begreen.activities.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class ProfileDetailsFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        val bundle = Bundle()
        bundle.putParcelable(ARG_USER, User(1,"Alice", 1))


        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer(bundle) {
            ProfileDetailsFragment()
        }
    }
    @Test
    fun testProfileDetailsFragmentIsCorrectlyDisplayed() {

        Espresso.onView(ViewMatchers.withId(R.id.fragment_profile_details)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testProfileDetailsFragmentFollowButton(){
        onView(withId(R.id.follow_button)).perform(click())
        onView(withId(R.id.follow_button)).check(ViewAssertions.matches(ViewMatchers.withText("Unfollow")))
    }

    @Test
    fun testProfileDetailsFragmentFollowButton2(){
        onView(withId(R.id.follow_button)).perform(click())
        onView(withId(R.id.follow_button)).perform(click())
        onView(withId(R.id.follow_button)).check(ViewAssertions.matches(ViewMatchers.withText("Follow")))
    }

    @Test
    fun testProfileDetailsFragmentNewInstance(){
        ProfileDetailsFragment.newInstance(User(1,"Alice", 1))
    }

    @Test
    fun testProfileDetailsWithCompleteUserFragmentIsCorrectlyDisplayed() {
        val bundle = Bundle()
        val photo: Photo = Photo("1", ParcelableDate(Date()), User(1, "Alice", 33, ), "Gros vilain pas beau")
        bundle.putParcelable(ARG_USER, User(1, "Alice", 33, 1, photo, "Description poutou poutou", "cc@gmail.com", "08920939459802", 67, null, null))


        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer(bundle) {
            ProfileDetailsFragment()
        }
        Espresso.onView(ViewMatchers.withId(R.id.fragment_profile_details)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}