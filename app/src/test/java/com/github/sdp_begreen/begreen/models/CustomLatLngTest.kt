package com.github.sdp_begreen.begreen.models

import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CustomLatLngTest {

    @Test
    fun customLatLngToStringReturnExpectedString() {
        val newCustomLatLng = CustomLatLng(12.456, 64.156)
        assertThat(newCustomLatLng.toString(), `is`("lat=12.456, long=64.156"))
    }

    @Test
    fun customLatLngToMapLatLngReturnCorrectValue() {
        val newCustomLatLng = CustomLatLng(12.456, 64.156)
        assertThat(LatLng(12.456, 64.156), `is`(newCustomLatLng.toMapLatLng()))
    }
}