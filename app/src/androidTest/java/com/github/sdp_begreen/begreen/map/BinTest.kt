package com.github.sdp_begreen.begreen.map

import com.github.sdp_begreen.begreen.models.TrashCategory
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class BinTest {

    @Test
    fun equalsOnBinsOnlyComparesFields() {
        val bin1 = Bin("id", TrashCategory.PAPER, LatLng(69.69, 4.20))
        val bin2 = Bin("id", TrashCategory.PAPER, LatLng(69.69, 4.20))
        assertEquals(bin1, bin2)
    }

    @Test
    fun defaultConstructorCreatesExpectedDefaultBin() {
        assertEquals(Bin(), Bin(null, TrashCategory.PLASTIC, LatLng(0.0, 0.0)))
    }

    @Test
    fun constructorWithLatlngAssignsExpectedLatitudeAndLongitude() {
        val location = LatLng(23.32, 34.43)
        val bin = Bin(TrashCategory.ELECTRONIC, location)
        assertEquals(location.latitude, bin.location.latitude, 0.0001)
        assertEquals(location.longitude, bin.location.longitude, 0.0001)
    }

    @Test
    fun locationReturnsTheRightLocation() {
        val bin = Bin("id1", TrashCategory.ORGANIC, LatLng(4.32, 3.25))
        assertEquals(bin.location, LatLng(4.32, 3.25))
    }
}