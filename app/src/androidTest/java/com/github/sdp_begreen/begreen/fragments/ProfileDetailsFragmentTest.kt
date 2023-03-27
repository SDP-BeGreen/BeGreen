package com.github.sdp_begreen.begreen.fragments

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
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class ProfileDetailsFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        val photos = listOf(
            PhotoMetadata("erfs","Look at me cleaning!", ParcelableDate(Date()),User("0", 111, "SuperUser69",0), "Déchet organique","Wowa je suis incroyable en train de ramasser cette couche usagée pour faire un selfie avec!"),
            PhotoMetadata("1", "title", ParcelableDate(Date()), User("1", 812, "Alice", 33, ), "Gros vilain pas beau", "desc"),
        )
        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer {
            ProfileDetailsFragment.newInstance(
                user = User("1",142, "Alice", 56, photos[0], "Description poutou poutou", "cc@gmail.com", "08920939459802", 67, null, null),
                photos = photos
            )
        }
    }
    @Test
    fun testProfileDetailsFragmentIsCorrectlyDisplayed() {

        Espresso.onView(ViewMatchers.withId(R.id.fragment_profile_details)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testProfileDetailsFragmentFollowButton(){
        onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
        onView(withId(R.id.fragment_profile_details_follow_button)).check(ViewAssertions.matches(ViewMatchers.withText("Unfollow")))
    }

    @Test
    fun testProfileDetailsFragmentFollowButton2(){
        onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
        onView(withId(R.id.fragment_profile_details_follow_button)).perform(click())
        onView(withId(R.id.fragment_profile_details_follow_button)).check(ViewAssertions.matches(ViewMatchers.withText("Follow")))
    }

    @Test
    fun testProfileDetailsWithCompleteUserFragmentIsCorrectlyDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.fragment_profile_details)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}