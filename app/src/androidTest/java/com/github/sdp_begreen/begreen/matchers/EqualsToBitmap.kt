package com.github.sdp_begreen.begreen.matchers

import android.graphics.Bitmap
import android.util.Log
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates

/**
 * Custom matcher class, to compare two bitmaps and know whether they are the same or not
 *
 * This matcher is not working yet, we need to find a way to be able to compare bitmap
 * when they have been compressed an uncompressed
 */
class EqualsToBitmap private constructor(private val bmp: Bitmap): TypeSafeMatcher<Bitmap>() {

    var counter1 by Delegates.notNull<Int>()
    var numberElm by Delegates.notNull<Int>()

    override fun describeTo(description: Description?) {
        description?.appendText(String.format("Expected item width: %s, expected item height: %s\n", bmp.width.toString(), bmp.height.toString()))
        description?.appendText("\n Expected counter: $numberElm")

    }

    override fun matchesSafely(item: Bitmap?): Boolean {
        if (item == null) return false

        val width = bmp.width.coerceAtMost(item.width)
        val height = bmp.height.coerceAtMost(item.height)
        val bmp2 = Bitmap.createScaledBitmap(bmp, width, height, false)
        val item2 = Bitmap.createScaledBitmap(item, width, height, false)



        val b1 = ByteArrayOutputStream()
        val b2 = ByteArrayOutputStream()
        bmp2.compress(Bitmap.CompressFormat.JPEG, 100, b1)
        item2.compress(Bitmap.CompressFormat.JPEG, 100, b2)

        // If any pixel is not the same, return false, image are not the same (see if must add threshold)
        var counter = 0
        val bA1 = b1.toByteArray()
        val bA2 = b2.toByteArray()
        numberElm = if (bA1.size < bA2.size) bA1.size else bA2.size
        b1.toByteArray().zip(b2.toByteArray()).forEach {

            Log.d("first and second", "${it.first} : ${it.second}")
            if (it.first != it.second) counter++
        }

        // TODO This doesn't work I think compressing and uncompressing image are not always the same
        // TODO Is it another way to compare Bitmap ?
        counter1 = counter
        Log.d("total elem and different elem", "$numberElm : $counter")
        if (counter / numberElm.toDouble() > 0.1) return false

        return true
    }

    override fun describeMismatchSafely(item: Bitmap?, mismatchDescription: Description?) {
        mismatchDescription?.appendText(String.format("Received item width: %s, received item height: %s\n", item!!.width.toString(), item.height.toString()))
        mismatchDescription?.appendText("\n Received counter: $counter1")
    }

    companion object {
        fun equalsBitmap(bmp: Bitmap): Matcher<Bitmap> = EqualsToBitmap(bmp)
    }

}