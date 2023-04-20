package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.BinsFakeDatabase
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.BinType
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.function.Predicate.isEqual

@RunWith(AndroidJUnit4::class)
@LargeTest
class MapFragmentTest {
    
    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    private val fragment = MapFragment()


    @Test
    fun testFragmentInflation() {

        /* The googlemaps library is quite hard to test. For now, we have only tested the launching of the mapFragment.

        According to the bootcamp https://github.com/sweng-epfl/public/blob/main/project/bootcamp/Maps.md :

        [...] since you cannot set the inputs, it will be difficult to achieve 100% of coverage.
        Therefore, we advise you to write as much as possible code that is independent from map components, and that you can easily test.
        */

        launchFragmentInContainer { fragment }

        // Wait until the map fragment is displayed
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        // Check that the map is displayed
        onView(withContentDescription("Google Map")).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnMapAddsNewBin() {

        val nbOfBinsOld = BinsFakeDatabase.fakeBins.size

        launchFragmentInContainer { fragment }

        // Wait until the map fragment is displayed
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        // Click on the map view
        onView(withContentDescription("Google Map")).perform(ViewActions.click())

        val nbOfBinsNew = BinsFakeDatabase.fakeBins.size

        // Check that a new bin have been added
        assertThat(nbOfBinsOld, lessThan(nbOfBinsNew))
    }
}