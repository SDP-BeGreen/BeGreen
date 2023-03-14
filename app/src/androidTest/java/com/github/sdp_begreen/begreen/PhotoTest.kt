package com.github.sdp_begreen.begreen

import android.os.Parcel
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class PhotoTest {
    @Test
    fun photoParcelConstructorWorks() {
        val photo = Photo(Parcel.obtain())
        assertThat(photo, notNullValue())
    }

    @Test
    fun photoGetFromDatabaseWorks() {
        val photo = Photo(Parcel.obtain())
        assertThat(photo.getPhotoFromDataBase(), notNullValue())
    }

    @Test
    fun photoWriteToParcelWorks() {
        val photo = Photo(Parcel.obtain())
        val parcel = Parcel.obtain()
        photo.writeToParcel(parcel, 0)
        assertThat(parcel, notNullValue())
    }

    @Test
    fun photoDescribeContentsWorks() {
        val photo = Photo(Parcel.obtain())
        assertThat(photo.describeContents(), equalTo(0))
    }

    @Test
    fun photoCreatorWorks() {
        val photo = Photo(Parcel.obtain())
        assertThat(Photo.CREATOR, notNullValue())
    }
}