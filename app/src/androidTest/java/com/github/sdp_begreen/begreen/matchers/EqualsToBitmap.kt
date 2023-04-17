package com.github.sdp_begreen.begreen.matchers

import android.graphics.Bitmap
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom matcher class, to compare two bitmaps and know whether they are the same or not
 */
class EqualsToBitmap private constructor(private val bmp: Bitmap): TypeSafeMatcher<Bitmap>() {

    override fun describeTo(description: Description?) {
        description?.appendText("width: ${bmp.width}, height: ${bmp.height}")
    }

    override fun matchesSafely(item: Bitmap?): Boolean {
        if (item == null) return false

        if (item.height != bmp.height || item.width != bmp.width) return false

        return true
    }

    override fun describeMismatchSafely(item: Bitmap?, mismatchDescription: Description?) {
        mismatchDescription?.appendText("width: ${item?.width}, height: ${item?.height}")
    }

    companion object {
        /**
         * Check bitmap width and height to uniquely identify bitmap
         *
         * It should be to the tester responsibility to ensure that no two
         * fake test bitmap could be equal by chance
         */
        fun equalsBitmap(bmp: Bitmap): Matcher<Bitmap> = EqualsToBitmap(bmp)
    }

}