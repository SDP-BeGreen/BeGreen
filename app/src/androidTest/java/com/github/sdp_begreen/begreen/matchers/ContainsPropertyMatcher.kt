package com.github.sdp_begreen.begreen.matchers

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.util.*

class ContainsPropertyMatcher<T> private constructor(private val name: String, private val matcher: Matcher<*>): TypeSafeMatcher<T>() {
    override fun describeTo(description: Description?) {
        description?.appendText("Check that given elem has property name")
    }

    override fun matchesSafely(item: T?): Boolean {
        return matcher.matches(readInstanceProperty(item!!, name))
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
            return instance.javaClass.getMethod("get" + propertyName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }).invoke(instance) as R
        }

        fun <T> hasProp(name: String, matcher: Matcher<T>): Matcher<T> {
            return ContainsPropertyMatcher(name, matcher)
        }
    }
}