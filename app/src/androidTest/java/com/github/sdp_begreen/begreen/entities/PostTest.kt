package com.github.sdp_begreen.begreen.entities

import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

class PostTest {

    private val mockBitmap = mock(Bitmap::class.java)
    private val testTitle = "Test Title"
    private val image = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    private val post = Post(testTitle, mockBitmap)

    @Test
    fun testGetTitle() {
        assertEquals(testTitle, post.getTitle())
    }
/*
    @Test
    fun testGetImage() {
        assertEquals(mockBitmap, post.getImage())
    }*/
}
