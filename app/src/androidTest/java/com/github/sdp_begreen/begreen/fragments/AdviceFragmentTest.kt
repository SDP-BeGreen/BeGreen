package com.github.sdp_begreen.begreen.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AdviceFragmentTest {
    companion object {
        @BeforeClass
        @JvmStatic fun setup() {
            try {
                Firebase.database.useEmulator("10.0.2.2", 9000)
                Firebase.storage.useEmulator("10.0.2.2", 9199)
                Firebase.auth.useEmulator("10.0.2.2", 9099)
            } catch (_:java.lang.IllegalStateException){}
        }
    }

    @Test
    fun textViewDisplaysStringFromListOfAdvices() {

        // Advices stored in the emulator
        val advices = setOf("Advice1", "Advice2", "Advice3")

        // Since the AdviceFragment retrieves the advices in an asynchronous manner,
        // we launch it inside a runBlocking block that waits for all suspend functions to complete
        runBlocking {
            launchFragmentInContainer<AdviceFragment>()
        }

        // Find the TextView by its ID and check if it's displayed
        onView(withId(R.id.adviceFragmentTextView)).check(matches(isDisplayed()))
        // Check if the TextView has text that is contained in the stringList
        onView(withId(R.id.adviceFragmentTextView)).check(matches(withText(containsOneElementFromStringSet(advices))))

    }

    // This matcher checks that the text displayed is contained in a Set of strings (quotes here)
    private fun containsOneElementFromStringSet(stringSet: Set<String>): Matcher<String> {
        return object : TypeSafeMatcher<String>() {
            override fun describeTo(description: Description?) {
                description?.appendText("should contain one of these strings: $stringSet")
            }

            override fun matchesSafely(item: String?): Boolean {
                item?.let {
                    for (string in stringSet) {
                        if (it == string) {
                            return true
                        }
                    }
                }
                return false
            }
        }
    }

}