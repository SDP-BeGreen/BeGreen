package com.github.sdp_begreen.begreen.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

object BitmapsUtils {

    /**
     * Function that rescale a bitmap image to a given width and height
     *
     * Keep the aspect ratio of the picture, by resizing the height or width given the biggest
     * value of both
     *
     * @param bitmap The picture to resize
     * @param width The expected width
     * @param height The expected height
     *
     */
    fun rescaleImage(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val newImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newImg)
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val xTranslation: Float
        val yTranslation: Float
        val scale: Float

        val xScale = width / originalWidth.toFloat()
        val yScale = height / originalHeight.toFloat()

        if (xScale < yScale) {
            scale = xScale
            xTranslation = 0f
            yTranslation = (height - originalHeight * scale) / 2.0f
        } else {
            scale = yScale
            yTranslation = 0f
            xTranslation = (width - originalWidth * scale) / 2.0f
        }

        drawOnCanvasWithTransformations(canvas, bitmap, scale, xTranslation, yTranslation)
        return newImg
    }

    /**
     * Helper function to draw the [bitmap] image on the [canvas], and apply the corresponding
     * translation and scaling to the image.
     */
    private fun drawOnCanvasWithTransformations(canvas: Canvas, bitmap: Bitmap, scale: Float,
                             xTranslation: Float, yTranslation: Float) {
        val transformation = Matrix().apply {
            preScale(scale, scale)
            postTranslate(xTranslation, yTranslation)
        }
        val paint: Paint = Paint().apply {
            isFilterBitmap = true
        }
        canvas.drawBitmap(bitmap, transformation, paint)
    }
}