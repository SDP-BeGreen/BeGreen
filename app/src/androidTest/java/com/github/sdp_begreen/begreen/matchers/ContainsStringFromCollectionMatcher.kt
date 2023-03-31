package com.github.sdp_begreen.begreen.matchers

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class ContainsStringFromCollectionMatcher<T> private constructor(private val strings: Collection<String>): TypeSafeMatcher<T>() {
    override fun describeTo(description: Description?) {
        description?.appendText("should contain one of these strings: $strings")
    }

    override fun matchesSafely(item: T?): Boolean {
        item?.let {
            for (string in strings) {
                if (it == string) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        fun hasStringFromCollection(strings: Collection<String>): Matcher<String> = ContainsStringFromCollectionMatcher(strings)
    }
}