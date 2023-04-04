package com.github.sdp_begreen.begreen.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.matchers.ContainsStringFromCollectionMatcher.Companion.hasStringFromCollection
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@LargeTest
class AdviceFragmentTest {

    private val db: DB = mock(DB::class.java)

    @get:Rule
    val koinTEstRule = KoinTestRule(
        modules = listOf(module {
            single {db}
        })
    )

    @Test
    fun textViewDisplaysStringFromListOfAdvices() {
        runBlocking {
            val advices = setOf("Advice1", "Advice2", "Advice3")
            `when`(db.getAdvices()).thenReturn(advices)

            launchFragmentInContainer<AdviceFragment>()

            withTimeout(5000) {
                // get the advices retrieved from the database
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