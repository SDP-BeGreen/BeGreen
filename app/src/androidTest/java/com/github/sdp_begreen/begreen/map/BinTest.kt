package com.github.sdp_begreen.begreen.map

import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class BinTest {

    @Test
    fun equalsOnBinsOnlyComparesFields() {
        val bin1 = Bin("id", TrashCategory.PAPER, CustomLatLng(69.69, 4.20))
        val bin2 = Bin("id", TrashCategory.PAPER, CustomLatLng(69.69, 4.20))
        assertEquals(bin1, bin2)
    }

    @Test
    fun defaultConstructorCreatesExpectedDefaultBin() {
        assertEquals(Bin(), Bin(null, TrashCategory.PLASTIC, CustomLatLng(0.0, 0.0)))
    }

    @Test
    fun constructorWithLatlngAssignsExpectedLatitudeAndLongitude() {
        val location = LatLng(23.32, 34.43)
        val bin = Bin(TrashCategory.ELECTRONIC, CustomLatLng(location.latitude, location.longitude))
        assertEquals(location.latitude, bin.location.toMapLatLng().latitude, 0.0001)
        assertEquals(location.longitude, bin.location.toMapLatLng().longitude, 0.0001)
    }

    @Test
    fun locationReturnsTheRightLocation() {
        val bin = Bin("id1", TrashCategory.ORGANIC, CustomLatLng(4.32, 3.25))
        assertEquals(bin.location, CustomLatLng(4.32, 3.25))
    }
}