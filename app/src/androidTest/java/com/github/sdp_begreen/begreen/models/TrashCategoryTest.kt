package com.github.sdp_begreen.begreen.models


import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class TrashCategoryTest {

    private val trashCategoryTest = TrashCategory.CLOTHES

    @Test
    fun trashCategoryGettersReturnExpectedValues() {

        // We prefer to check each field like that instead of CoreMatchers.allOf because the CoreMatchers.allOf uses
        // string to represent each field, and these string names are not updated when we refactor the field name in the
        // class

        MatcherAssert.assertThat(trashCategoryTest.title, CoreMatchers.equalTo("Clothes"))
        MatcherAssert.assertThat(trashCategoryTest.color, CoreMatchers.equalTo(BitmapDescriptorFactory.HUE_VIOLET))
    }
}