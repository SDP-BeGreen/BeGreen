package com.github.sdp_begreen.begreen.matchers

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom matcher class to compare two collections.
 *
 * The collection to check should be a sublist of the reference collection, and the elements should
 * appear in the same order in both collections. But the elements of the sublist do not
 * necessarily need to be next to one another.
 */
class ContainsWithSameOrder<T> private constructor(private val collection: Collection<T>) :
    TypeSafeMatcher<Collection<T>>() {
    override fun describeTo(description: Description?) {
        description?.appendText(
            "All elements should be contained in this list: {$collection}. And in the same order"
        )
    }

    override fun matchesSafely(collectionToCheck: Collection<T>?): Boolean {
        if (collectionToCheck == null) return false
        val filteredCollection = collection.filter { collectionToCheck.contains(it) }

        // check if collection are strictly equals (order and contents)
        return collectionToCheck == filteredCollection
    }

    override fun describeMismatchSafely(
        collectionToCheck: Collection<T>,
        mismatchDescription: Description?
    ) {
        super.describeMismatchSafely(collectionToCheck, mismatchDescription)
        mismatchDescription?.appendText(
            "\n Some element from the list, where either missing or not in the correct order"
        )
    }

    companion object {
        /**
         * Check that the collection is a sub-collection of the reference collection, and
         * that the elements are in the same order. The element are not necessarily
         * next to each other.
         *
         * @param collection The reference collection
         */
        fun <T> inWithOrder(collection: Collection<T>): Matcher<Collection<T>> =
            ContainsWithSameOrder(collection)
    }
}