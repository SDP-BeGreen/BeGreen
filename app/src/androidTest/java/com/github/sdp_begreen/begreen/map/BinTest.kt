package com.github.sdp_begreen.begreen.map

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class BinTest {

    @Test
    fun equalsOnBinsOnlyComparesFields() {
        val bin1 = Bin("id", BinType.PAPER, 69.69, 4.20)
        val bin2 = Bin("id", BinType.PAPER, 69.69, 4.20)
        assertEquals(bin1, bin2)
    }

    @Test
    fun defaultConstructorCreatesExpectedDefaultBin() {
        assertEquals(Bin(), Bin(null, BinType.PLASTIC, 0.0, 0.0))
    }

    @Test
    fun constructorWithLatlngAssignsExpectedLatitudeAndLongitude() {
        val location = LatLng(23.32, 34.43)
        val bin = Bin(BinType.ELECTRONIC, location)
        assertEquals(location.latitude, bin.lat, 0.0001)
        assertEquals(location.longitude, bin.long, 0.0001)
    }

    @Test
    fun locationReturnsTheRightLocation() {
        val bin = Bin("id1", BinType.ORGANIC, 4.32, 3.25)
        assertEquals(bin.location(), LatLng(4.32, 3.25))
    }
}