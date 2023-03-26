package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.Mockito.mock

class PostTest {

    private val testBitmap = mock(Bitmap::class.java)
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
