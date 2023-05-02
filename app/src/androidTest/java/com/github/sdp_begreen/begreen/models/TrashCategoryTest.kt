package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import com.github.sdp_begreen.begreen.matchers.ContainsPropertyMatcher
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test
import com.github.sdp_begreen.begreen.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class TrashCategoryTest {

    private val trashCategoryTest = TrashCategory.CLOTHES

    @Test
    fun trashCategoryGettersReturnExpectedValues() {

        // We prefer to check each field like that instead of CoreMatchers.allOf because the CoreMatchers.allOf uses
        // string to represent each field, and these string names are not updated when we refactor the field name in the
        // class

        MatcherAssert.assertThat(trashCategoryTest.id, CoreMatchers.equalTo("5"))
        MatcherAssert.assertThat(trashCategoryTest.title, CoreMatchers.equalTo(R.string.clothes))
        MatcherAssert.assertThat(trashCategoryTest.color, CoreMatchers.equalTo(BitmapDescriptorFactory.HUE_VIOLET))
    }

    @Test
    fun trashCategoryIDsAreUniqueAndNotChanged() {

        // This test doesn't affect the coverage but it's important to prevent a developer from changing a trashCategory id.
        // A trashCategory is uniquely defined by its "id" in the database so if someone changes a trashCategory
        // "id" in the front-end, it will be no more consistent with the value stored in the backend and could let
        // to collisions and/or switches between trashCategory.

        var counter = 0

        // Check that each category has the correct id. We suppose that they are contiguous and the ids are in the same
        // order of their definition in the enum class
        // If assumption is no more valid, remove the for-loop and check every id manually
        for (category in TrashCategory.values()) {

            MatcherAssert.assertThat(category.id, CoreMatchers.equalTo(counter.toString()))
            counter++
        }

        // Check that we have tested all cases
        MatcherAssert.assertThat(counter, CoreMatchers.equalTo(TrashCategory.values().size))
    }

    @Test
    fun trashCategoryDescribeContentsEqualsZero() {
        MatcherAssert.assertThat(trashCategoryTest.describeContents(), CoreMatchers.equalTo(0))
    }

    @Test
    fun trashCategoryWriteToParcelCorrectly() {

        val parcel = Parcel.obtain()
        trashCategoryTest.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val createdFromParcel =  TrashCategory.CREATOR.createFromParcel(parcel)

        MatcherAssert.assertThat(
            trashCategoryTest.id,
            CoreMatchers.equalTo(createdFromParcel.id)
        )
    }

    @Test
    fun trashCategoryNewArrayHasCorrectSize() {

        val array = TrashCategory.CREATOR.newArray(1)

        MatcherAssert.assertThat(array.size, CoreMatchers.equalTo(1))
    }

    @Test
    fun trashCategoryCreatorWriteCorrectTrashCategory() {

        val parcel = Parcel.obtain()
        parcel.writeString(trashCategoryTest.id)
        parcel.setDataPosition(0)

        val createdFromParcel = TrashCategory.CREATOR.createFromParcel(parcel)
        MatcherAssert.assertThat(trashCategoryTest.id, CoreMatchers.equalTo(createdFromParcel.id))
    }

    @Test
    fun trashCategoryCreatorWrongIdThrowsException() {

        Assert.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {

                val parcel = Parcel.obtain()
                parcel.writeString("-1")
                parcel.setDataPosition(0)

                TrashCategory.CREATOR.createFromParcel(parcel)
            }
        }
    }
}