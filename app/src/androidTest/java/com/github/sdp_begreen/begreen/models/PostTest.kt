package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat

// Needs to be in Android Test to use Bitmap
class PostTest {

    private val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private val testTitle = "Test Title"
    private val post = Post(testTitle, testBitmap)

    @Test
    fun getTitleReturnsCorrectTitle() {
        assertThat(post.getTitle(), equalTo(testTitle))
    }

    @Test
    fun getImageReturnsCorrectImage() {
        assertThat(post.getImage(), equalTo(testBitmap))
    }
}