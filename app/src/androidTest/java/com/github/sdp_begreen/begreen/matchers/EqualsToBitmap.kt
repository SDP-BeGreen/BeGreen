package com.github.sdp_begreen.begreen.matchers

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.ByteArrayOutputStream

/**
 * Custom matcher class, to compare two bitmaps and know whether they are the same or not
 */
class EqualsToBitmap private constructor(private val bmp: Bitmap): TypeSafeMatcher<Bitmap>() {
    override fun describeTo(description: Description?) {
        description?.appendText(String.format("Expected item width: %s, expected item height: %s", bmp.width.toString(), bmp.height.toString()))
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

    override fun describeMismatchSafely(item: Bitmap?, mismatchDescription: Description?) {
        mismatchDescription?.appendText(String.format("Received item width: %s, received item height: %s", item!!.width.toString(), item.height.toString()))
    }

    companion object {
        fun equalsBitmap(bmp: Bitmap): Matcher<Bitmap> = EqualsToBitmap(bmp)
    }

}