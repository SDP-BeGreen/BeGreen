package com.github.sdp_begreen.begreen.matchers

import android.graphics.Bitmap
import android.util.Log
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.ByteArrayOutputStream

/**
 * Custom matcher class, to compare two bitmaps and know whether they are the same or not
 */
class EqualsToBitmap private constructor(private val bmp: Bitmap): TypeSafeMatcher<Bitmap>() {
    override fun describeTo(description: Description?) {
        description?.appendText("Check that the two bitmaps are equals")
    }

    override fun matchesSafely(item: Bitmap?): Boolean {
        if (bmp.width != item?.width || bmp.height != item.height) return false

        val b1 = ByteArrayOutputStream()
        val b2 = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, b1)
        item.compress(Bitmap.CompressFormat.JPEG, 100, b1)

        // If any pixel is not the same, return false, image are not the same (see if must add threshold)
        b1.toByteArray().zip(b2.toByteArray()).forEach {
            if (it.first != it.second) return false
        }

        return true
    }

    companion object {
        fun equalsBitmap(bmp: Bitmap): Matcher<Bitmap> = EqualsToBitmap(bmp)
    }

}