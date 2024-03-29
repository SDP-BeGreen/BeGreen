package com.github.sdp_begreen.begreen.models

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CustomLatLngTest {

    @Test
    fun customLatLngToStringReturnExpectedString() {
        val newCustomLatLng = CustomLatLng(12.456, 64.156)
        assertThat(newCustomLatLng.toString(), `is`("lat=12.456, long=64.156"))
    }

    @Test
    fun toMapLatLngReturnNullWhenLatitudeIsNull() {
        val newCustomLatLng = CustomLatLng(null, 4.32)
        assertThat(newCustomLatLng.toMapLatLng(), `is`(nullValue()))
    }

    @Test
    fun toMapLatLngReturnNullWhenLongitudeIsNull() {
        val newCustomLatLng = CustomLatLng(2.34, null)
        assertThat(newCustomLatLng.toMapLatLng(), `is`(nullValue()))
    }

    @Test
    fun toMapLatLngReturnCorrectValue() {
        val newCustomLatLng = CustomLatLng(12.456, 64.156)
        assertThat(LatLng(12.456, 64.156), `is`(newCustomLatLng.toMapLatLng()))
    }

    @Test
    fun customLatLngFromMapLatLngReturnCorrectValue() {
        val latLng = LatLng(12.456, 64.156)
        val customLatLng = CustomLatLng(12.456, 64.156)
        assertThat(CustomLatLng.fromMapLatLng(latLng), `is`(customLatLng))
    }

    @Test
    fun fromLocationReturnsExpectedLocation() {

        val lat = 3.14
        val long = 4.2
        val location = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = lat
            longitude = long
        }

        val customLatLng = CustomLatLng.fromLocation(location)

        assertThat(customLatLng.latitude, `is`(equalTo(lat)))
        assertThat(customLatLng.longitude, `is`(equalTo(long)))
    }

    @Test
    fun toMapLocationReturnNullWhenLatitudeIsNull() {
        val newCustomLatLng = CustomLatLng(null, 4.32)
        assertThat(newCustomLatLng.toMapLocation(), `is`(nullValue()))
    }

    @Test
    fun toMapLocationReturnNullWhenLongitudeIsNull() {
        val newCustomLatLng = CustomLatLng(2.34, null)
        assertThat(newCustomLatLng.toMapLocation(), `is`(nullValue()))
    }

    @Test
    fun toMapLocationReturnsExpectedLocation() {

        val lat = -0.7
        val long = -42.42
        val location = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = lat
            longitude = long
        }

        val customLatLng = CustomLatLng(lat, long)

        assertThat(customLatLng.toMapLocation()!!.latitude, `is`(equalTo(location.latitude)))
        assertThat(customLatLng.toMapLocation()!!.longitude, `is`(equalTo(location.longitude)))
    }

    @Test
    fun distanceFromReturnsExpectedDistance() {

        val loc1 = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 3.14
            longitude = 4.2
        }
        val loc2 = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = -6.2
            longitude = 15.4
        }
        val expectedDistance = loc1.distanceTo(loc2).toLong()

        val customLatLng = CustomLatLng.fromLocation(loc1)

        assertThat(customLatLng.distanceFrom(loc2), `is`(equalTo(expectedDistance)))
    }


}