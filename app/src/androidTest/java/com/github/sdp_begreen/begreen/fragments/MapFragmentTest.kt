package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MapFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    private val fragment = MapFragment()


    @Test
    fun testFragmentInflation() {

        /* The googlemaps library is quite hard to test. For now, we have only tested the launching of the mapFragment.

        According to the bootcamp https://github.com/sweng-epfl/public/blob/main/project/bootcamp/Maps.md :

        [...] since you cannot set the inputs, it will be difficult to achieve 100% of coverage.
        Therefore, we advise you to write as much as possible code that is independent from map components, and that you can easily test.
        */

        launchFragmentInContainer { fragment }

        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))
    }

}