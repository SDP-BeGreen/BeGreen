package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ProfileFragmentTest {
    @Test
    fun testAdviceFragment() {
        launchFragmentInContainer<ProfileFragment>()
        Espresso.onView(ViewMatchers.withId(R.id.profileFragmentTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText("Fragment where we can display the user profile")))
    }

    @Test
    fun testAdviceFragmentWithArgs() {


        val bundle = Bundle()
        bundle.putString("param1", "Param 1")
        bundle.putString("param2", "Param 2")

        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer(bundle) {
            ProfileFragment.newInstance("", "")
        }
        Espresso.onView(ViewMatchers.withId(R.id.profileFragmentTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText("Fragment where we can display the user profile Param 1, Param 2")))

    }
}