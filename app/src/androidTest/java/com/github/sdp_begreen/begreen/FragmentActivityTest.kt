package com.github.sdp_begreen.begreen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FragmentActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(FragmentActivity::class.java)

    @Test
    fun drawerMenuClosedWhenActivityOpens() {
        onView(withId(R.id.drawerLayout))
            .check(matches(isClosed()))
    }

    @Test
    fun drawerMenuContainsTitle() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())

        onView(withId(R.id.header_drawer_main_menu)).check(matches(isDisplayed()))
    }

    @Test
    fun openDrawerByClickingOnToolBar() {
        onView(withContentDescription("Open menu")).perform(click())

        onView(withId(R.id.drawerLayout)).check(matches(isOpen()))
    }

    @Test
    fun clickOnFavoriteMenuDisplayFavoriteFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())

        onView(withId(R.id.item_favorite))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.favoriteFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnHomeMenuDisplayHomeFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())

        onView(withId(R.id.item_home))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.homeTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnMailMenuDisplayMailFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())

        onView(withId(R.id.item_mail))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.mailFragment)).check(matches(isDisplayed()))
    }

}