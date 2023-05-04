package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class ProfilePhotoMetadataTest {

    @Test
    fun photoParcelConstructorNotNull() {
        val profilePhotoMetadata = ProfilePhotoMetadata(Parcel.obtain())
        MatcherAssert.assertThat(profilePhotoMetadata, CoreMatchers.notNullValue())
    }

    /*
    @Test
    fun photoGetFromDatabaseGetBitmap() {
        val profilePhotoMetadata = ProfilePhotoMetadata(Parcel.obtain())
        assertThat(profilePhotoMetadata.getPhotoFromDataBase()!!::class.java, equalTo(Bitmap::class.java))
    }
    */

    @Test
    fun photoWriteToParcelWorks() {

        val profilePhotoMetadata = ProfilePhotoMetadata(Parcel.obtain())
        val parcel = Parcel.obtain()
        profilePhotoMetadata.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = ProfilePhotoMetadata(parcel)
        MatcherAssert.assertThat(profilePhotoMetadata, CoreMatchers.equalTo(result))
    }

    @Test
    fun photoDescribeContentsReturnZero() {
        val profilePhotoMetadata = ProfilePhotoMetadata(Parcel.obtain())
        MatcherAssert.assertThat(profilePhotoMetadata.describeContents(), CoreMatchers.equalTo(0))
    }

    @Test
    fun photoCreatorIsNotNull() {

        MatcherAssert.assertThat(ProfilePhotoMetadata.CREATOR, CoreMatchers.notNullValue())
    }

    @Test
    fun photoCreateFromParcelWorks(){
        val profilePhotoMetadata = ProfilePhotoMetadata(Parcel.obtain())
        val parcel = Parcel.obtain()
        profilePhotoMetadata.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = ProfilePhotoMetadata.CREATOR.createFromParcel(parcel)
        MatcherAssert.assertThat(profilePhotoMetadata, CoreMatchers.equalTo(result))
    }

    @Test
    fun photoNewArrayIsNotNull(){

        val result = ProfilePhotoMetadata.CREATOR.newArray(1)
        MatcherAssert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun photoGettersWorks(){
        val date = ParcelableDate.now
        val user = User("1",0, "test")
        val profilePhotoMetadata = ProfilePhotoMetadata("key", date, user.id)
        MatcherAssert.assertThat(
            profilePhotoMetadata, CoreMatchers.allOf(
                ContainsPropertyMatcher.hasProp("pictureId", CoreMatchers.equalTo("key")),
                ContainsPropertyMatcher.hasProp("takenOn", CoreMatchers.equalTo(date)),
                ContainsPropertyMatcher.hasProp("takenBy", CoreMatchers.equalTo(user.id))
            )
        )
    }
}