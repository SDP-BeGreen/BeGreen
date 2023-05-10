package com.github.sdp_begreen.begreen.models

import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher.Companion.hasProp
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TrashPhotoMetadataTest {

    @Test
    fun emptyConstructorWorks(){

        val trashPhotoMetadata = TrashPhotoMetadata()

        ViewMatchers.assertThat(trashPhotoMetadata.pictureId, equalTo(null))
        ViewMatchers.assertThat(trashPhotoMetadata.takenOn, equalTo(null))
        ViewMatchers.assertThat(trashPhotoMetadata.takenBy, equalTo(null))
        ViewMatchers.assertThat(trashPhotoMetadata.caption, equalTo(null))
        ViewMatchers.assertThat(trashPhotoMetadata.trashCategory, equalTo(null))
    }

    @Test
    fun photoGettersWorks(){

        val date = ParcelableDate.now
        val trashPhotoMetadata = TrashPhotoMetadata("key", date, "id", "title", TrashCategory.PLASTIC)

        ViewMatchers.assertThat(trashPhotoMetadata.pictureId, equalTo("key"))
        ViewMatchers.assertThat(trashPhotoMetadata.takenOn, equalTo(date))
        ViewMatchers.assertThat(trashPhotoMetadata.takenBy, equalTo("id"))
        ViewMatchers.assertThat(trashPhotoMetadata.caption, equalTo("title"))
        ViewMatchers.assertThat(trashPhotoMetadata.trashCategory, equalTo(TrashCategory.PLASTIC))
    }

    @Test
    fun photoPictureIdSetterWorks(){

        val date = ParcelableDate.now
        val trashPhotoMetadata = TrashPhotoMetadata("key", date, "id", "title", TrashCategory.PLASTIC)
        trashPhotoMetadata.pictureId = "edited"

        ViewMatchers.assertThat(trashPhotoMetadata.pictureId, equalTo("edited"))
    }
}