package com.github.sdp_begreen.begreen.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.matchers.ContainsStringFromCollectionMatcher.Companion.hasStringFromCollection
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
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
        runBlocking {
            val deferred = CompletableDeferred<Set<String>>()

            launchFragmentInContainer{ AdviceFragment {deferred.complete(it)} }

            withTimeout(5000) {
                // get the advices retrieved from the database
                val advices = deferred.await()
                // Find the TextView by its ID and check if it's displayed
                onView(withId(R.id.adviceFragmentTextView)).check(matches(isDisplayed()))
                // Check if the TextView has text that is contained in the stringList
                onView(withId(R.id.adviceFragmentTextView)).check(matches(withText(
                    hasStringFromCollection(advices)
                )))
            }
        }
    }



}