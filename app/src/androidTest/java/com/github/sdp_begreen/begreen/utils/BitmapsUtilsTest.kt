package com.github.sdp_begreen.begreen.utils

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapsUtilsTest {

    @Test
    fun resizeBitmapWithBiggestWidthWorks() {
        val bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)

        val newBitmap = BitmapsUtils.rescaleImage(bitmap, 200, 200)

        assertThat(newBitmap.width, `is`(lessThanOrEqualTo(200)))
        assertThat(newBitmap.height, `is`(lessThanOrEqualTo(200)))
    }

    @Test
    fun resizeBitmapWithBiggestHeightWorks() {
        val bitmap = Bitmap.createBitmap(100, 400, Bitmap.Config.ARGB_8888)

        val newBitmap = BitmapsUtils.rescaleImage(bitmap, 200, 200)

        assertThat(newBitmap.width, `is`(lessThanOrEqualTo(200)))
        assertThat(newBitmap.height, `is`(lessThanOrEqualTo(200)))
    }

    @Test
    fun resizePortraitImageToLandscapeWorks() {
        val bitmap = Bitmap.createBitmap(300, 400, Bitmap.Config.ARGB_8888)

        val newBitmap = BitmapsUtils.rescaleImage(bitmap, 200, 50)

        assertThat(newBitmap.width, `is`(lessThanOrEqualTo(200)))
        assertThat(newBitmap.height, `is`(lessThanOrEqualTo(200)))
    }

    @Test
    fun resizeLandscapeImageToPortraitWorks() {
        val bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)

        val newBitmap = BitmapsUtils.rescaleImage(bitmap, 50, 100)

        assertThat(newBitmap.width, `is`(lessThanOrEqualTo(200)))
        assertThat(newBitmap.height, `is`(lessThanOrEqualTo(200)))
    }

    @Test
    fun resizeSmallImageToBiggerWorks() {
        val bitmap = Bitmap.createBitmap(100, 50, Bitmap.Config.ARGB_8888)

        val newBitmap = BitmapsUtils.rescaleImage(bitmap, 200, 200)

        assertThat(newBitmap.width, `is`(lessThanOrEqualTo(200)))
        assertThat(newBitmap.height, `is`(lessThanOrEqualTo(200)))
        assertThat(newBitmap.width, `is`(greaterThanOrEqualTo(100)))
        assertThat(newBitmap.height, `is`(greaterThanOrEqualTo(50)))
    }
}