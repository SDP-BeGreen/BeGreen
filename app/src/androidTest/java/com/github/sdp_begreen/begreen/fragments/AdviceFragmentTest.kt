package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers

@RunWith(AndroidJUnit4::class)
@LargeTest
class AdviceFragmentTest {
    @Test
    fun testAdviceFragment() {
        launchFragmentInContainer<AdviceFragment>()
        onView(withId(R.id.adviceFragmentTextView))
            .check(matches(withText("Fragment where we can display ecological advice to the user")))
    }


    @Test
    fun checkTextViewIsEmpty() {
        onView(withId(R.id.adviceFragmentTextView))
            .check(matches(withText(""))) // checks if the text view is empty
    }

    @Test
    fun checkStringLength() {
        val maxLength = 100
        val stringToBeMatched = "Hello World!"
        onView(withId(R.id.adviceFragmentTextView))
            .check(matches(withText(CoreMatchers.not(CoreMatchers.containsString(stringToBeMatched.substring(maxLength))))))
    }


    @Test
    fun checkText() {
        val expectedText = "Hello, world!"
        onView(withId(R.id.adviceFragmentTextView)).perform(typeText(expectedText)) // enters text into the text view
        onView(withId(R.id.adviceFragmentTextView)).check(matches(withText(expectedText))) // retrieves the text from the text view
    }
}