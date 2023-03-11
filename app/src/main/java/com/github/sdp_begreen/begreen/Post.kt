package com.example.mapwithmarker

import android.graphics.Bitmap

class Post(private val title: String, private val image: Bitmap) {

    fun getTitle(): String = title

    fun getImage(): Bitmap = image
}
