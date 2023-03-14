package com.github.sdp_begreen.begreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.test.platform.app.InstrumentationRegistry


data class Photo(val key: String?, val takenOn: ParcelableDate?, val takenBy: User?, val category: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ParcelableDate::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString()
    ) {
    }

    fun getPhotoFromDataBase() : Bitmap? {
        //TODO : get the photo from the database and maybe cache?
        return getBitmapFromVectorDrawable(InstrumentationRegistry.getInstrumentation().targetContext, R.drawable.ic_launcher_background)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeParcelable(takenOn, flags)
        parcel.writeParcelable(takenBy, flags)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    private fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap? {
        var drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}