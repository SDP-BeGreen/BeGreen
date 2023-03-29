package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AdviceFragmentTest {
    @Test
    fun testAdviceFragment() {
        val QUOTES = "quotes"
        launchFragmentInContainer<AdviceFragment>(Bundle().apply {
                putStringArrayList(QUOTES, arrayListOf("Hello World!"))
        })
        onView(withId(R.id.adviceFragmentTextView))
            .check(matches(withText("Hello World!")))
    }
}