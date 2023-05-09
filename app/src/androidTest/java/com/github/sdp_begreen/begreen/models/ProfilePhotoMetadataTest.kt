package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Test

class ProfilePhotoMetadataTest {

    @Test
    fun emptyConstructorWorks(){

        val profilePhotoMetadata = ProfilePhotoMetadata()

        assertThat(profilePhotoMetadata.pictureId, equalTo(null))
        assertThat(profilePhotoMetadata.takenOn, equalTo(null))
        assertThat(profilePhotoMetadata.takenBy, equalTo(null))
    }

    @Test
    fun photoGettersWorks(){

        val date = ParcelableDate.now
        val profilePhotoMetadata = ProfilePhotoMetadata("key", date, "id")

        assertThat(profilePhotoMetadata.pictureId, equalTo("key"))
        assertThat(profilePhotoMetadata.takenOn, equalTo(date))
        assertThat(profilePhotoMetadata.takenBy, equalTo("id"))
    }

    @Test
    fun photoPictureIdSetterWorks(){

        val date = ParcelableDate.now
        val profilePhotoMetadata = ProfilePhotoMetadata("key", date, "id")
        profilePhotoMetadata.pictureId = "edited"

        assertThat(profilePhotoMetadata.pictureId, equalTo("edited"))
    }
}