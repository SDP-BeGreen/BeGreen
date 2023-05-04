package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher.Companion.hasProp
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TrashPhotoMetadataTest {

    @Test
    fun photoEqualsComparesOnlyPictureId() {

        // A PhotoMetaData is uniquely defined by its id.

        val trashPhotoMetadata1 = TrashPhotoMetadata("123", ParcelableDate.now, "aaa", "hello", TrashCategory.PLASTIC)
        val trashPhotoMetadata2 = TrashPhotoMetadata("123", ParcelableDate.now, "aaa", "hello", TrashCategory.PLASTIC)
        assertThat(trashPhotoMetadata1, equalTo(trashPhotoMetadata2))
    }

    @Test
    fun photoParcelConstructorNotNull() {
        val trashPhotoMetadata = TrashPhotoMetadata(Parcel.obtain())
        assertThat(trashPhotoMetadata, notNullValue())
    }

    /*
    @Test
    fun photoGetFromDatabaseGetBitmap() {
        val trashPhotoMetadata = TrashPhotoMetadata(Parcel.obtain())
        assertThat(trashPhotoMetadata.getPhotoFromDataBase()!!::class.java, equalTo(Bitmap::class.java))
    }
    */

    @Test
    fun photoWriteToParcelWorks() {

        val trashPhotoMetadata = TrashPhotoMetadata(Parcel.obtain())
        val parcel = Parcel.obtain()
        trashPhotoMetadata.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = TrashPhotoMetadata(parcel)
        assertThat(trashPhotoMetadata, equalTo(result))
    }

    @Test
    fun photoDescribeContentsReturnZero() {
        val trashPhotoMetadata = TrashPhotoMetadata(Parcel.obtain())
        assertThat(trashPhotoMetadata.describeContents(), equalTo(0))
    }

    @Test
    fun photoCreatorIsNotNull() {

        assertThat(TrashPhotoMetadata.CREATOR, notNullValue())
    }

    @Test
    fun photoCreateFromParcelWorks(){
        val trashPhotoMetadata = TrashPhotoMetadata(Parcel.obtain())
        val parcel = Parcel.obtain()
        trashPhotoMetadata.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = TrashPhotoMetadata.CREATOR.createFromParcel(parcel)
        assertThat(trashPhotoMetadata, equalTo(result))
    }

    @Test
    fun photoNewArrayIsNotNull(){

        val result = TrashPhotoMetadata.CREATOR.newArray(1)
        assertThat(result, notNullValue())
    }

    @Test
    fun photoGettersWorks(){
        val date = ParcelableDate.now
        val user = User("1",0, "test")
        val trashPhotoMetadata = TrashPhotoMetadata("key", date, user.id, "title", TrashCategory.PLASTIC)
        assertThat(trashPhotoMetadata, allOf(
            hasProp("pictureId", equalTo("key")),
            hasProp("takenOn", equalTo(date)),
            hasProp("takenByUserId", equalTo(user.id)),
            hasProp("caption", equalTo("title")),
            hasProp("trashCategory", equalTo(TrashCategory.PLASTIC))
            ))
    }
}