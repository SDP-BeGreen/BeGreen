package com.github.sdp_begreen.begreen.entities

import android.graphics.Bitmap

class Post(private val title: String, private val image: Bitmap) {

    fun getTitle(): String = title

    fun getImage(): Bitmap = image
}