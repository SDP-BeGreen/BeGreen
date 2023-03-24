package com.github.sdp_begreen.begreen.models

import android.graphics.Bitmap
import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher.Companion.hasProp
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class PhotoMetadataTest {
    @Test
    fun photoParcelConstructorNotNull() {
        val photoMetadata = PhotoMetadata(Parcel.obtain())
        assertThat(photoMetadata, notNullValue())
    }

    //@Test
    //fun photoGetFromDatabaseGetBitmap() {
    //    val photoMetadata = PhotoMetadata(Parcel.obtain())
    //    assertThat(photoMetadata.getPhotoFromDataBase()!!::class.java, equalTo(Bitmap::class.java))
    //}

    @Test
    fun photoWriteToParcelWorks() {
        val photoMetadata = PhotoMetadata(Parcel.obtain())
        val parcel = Parcel.obtain()
        photoMetadata.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = PhotoMetadata(parcel)
        assertThat(photoMetadata, equalTo(result))
    }

    @Test
    fun photoDescribeContentsReturnZero() {
        val photoMetadata = PhotoMetadata(Parcel.obtain())
        assertThat(photoMetadata.describeContents(), equalTo(0))
    }

    @Test
    fun photoCreatorIsNotNull() {
        PhotoMetadata(Parcel.obtain())
        assertThat(PhotoMetadata.CREATOR, notNullValue())
    }

    @Test
    fun photoCreateFromParcelWorks(){
        val photoMetadata = PhotoMetadata(Parcel.obtain())
        val parcel = Parcel.obtain()
        photoMetadata.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = PhotoMetadata.CREATOR.createFromParcel(parcel)
        assertThat(photoMetadata, equalTo(result))
    }

    @Test
    fun photoNewArrayIsNotNull(){
        PhotoMetadata(Parcel.obtain())
        val result = PhotoMetadata.CREATOR.newArray(1)
        assertThat(result, notNullValue())
    }

    @Test
    fun photoGettersWorks(){
        val date = ParcelableDate(Date())
        val user = User("1",0, "test")
        val photoMetadata = PhotoMetadata("key", date, user, "cat")
        assertThat(photoMetadata, allOf(
            hasProp("pictureId", equalTo("key")),
            hasProp("takenOn", equalTo(date)),
            hasProp("takenBy", equalTo(user)),
            hasProp("category", equalTo("cat"))))
    }
}