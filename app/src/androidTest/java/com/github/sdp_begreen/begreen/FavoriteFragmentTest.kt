package com.github.sdp_begreen.begreen

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.fragments.FavoriteFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FavoriteFragmentTest {

    @Test
    fun testMailFragment() {
        launchFragmentInContainer<FavoriteFragment>()
        onView(withId(R.id.favoriteFragment)).check(matches(withText("Favorite Placeholder")))
    }

    @Test
    fun testMailFragmentWithArgs() {


        val bundle = Bundle()
        bundle.putString("param1", "Param 1")
        bundle.putString("param2", "Param 2")

        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer(bundle) {
            FavoriteFragment.newInstance("", "")
        }
        onView(withId(R.id.favoriteFragment)).check(matches(withText("Favorite Placeholder Param 1 Param 2")))

    }
}