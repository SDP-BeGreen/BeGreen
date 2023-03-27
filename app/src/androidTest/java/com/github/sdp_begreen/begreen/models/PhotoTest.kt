package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher.Companion.hasProp
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class PhotoTest {
    @Test
    fun photoParcelConstructorNotNull() {
        val photo = Photo(Parcel.obtain())
        assertThat(photo, notNullValue())
    }

    @Test
    fun photoGetFromDatabaseGetBitmap() {
        val photo = Photo(Parcel.obtain())
        assertThat(photo.getPhotoFromDataBase()!!::class.java, equalTo(Bitmap::class.java))
    }

    @Test
    fun photoWriteToParcelWorks() {
        val photo = Photo(Parcel.obtain())
        val parcel = Parcel.obtain()
        photo.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = Photo(parcel)
        assertThat(photo, equalTo(result))
    }

    @Test
    fun photoDescribeContentsReturnZero() {
        val photo = Photo(Parcel.obtain())
        assertThat(photo.describeContents(), equalTo(0))
    }

    @Test
    fun photoCreatorIsNotNull() {
        Photo(Parcel.obtain())
        assertThat(Photo.CREATOR, notNullValue())
    }

    @Test
    fun photoCreateFromParcelWorks(){
        val photo = Photo(Parcel.obtain())
        val parcel = Parcel.obtain()
        photo.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = Photo.CREATOR.createFromParcel(parcel)
        assertThat(photo, equalTo(result))
    }

    @Test
    fun photoNewArrayIsNotNull(){
        Photo(Parcel.obtain())
        val result = Photo.CREATOR.newArray(1)
        assertThat(result, notNullValue())
    }

    @Test
    fun photoGettersWorks(){
        val date = ParcelableDate(Date())
        val user = User(1,"test",0)
        val photo = Photo("key", "test", date, user, "cat", "desc")
        assertThat(photo, allOf(
            hasProp("key", equalTo("key")),
            hasProp("title", equalTo("test")),
            hasProp("takenOn", equalTo(date)),
            hasProp("takenBy", equalTo(user)),
            hasProp("category", equalTo("cat")),
            hasProp("description", equalTo("desc"))))
    }
}