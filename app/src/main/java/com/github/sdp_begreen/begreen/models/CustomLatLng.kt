package com.github.sdp_begreen.begreen.models

import android.location.Location
import android.location.LocationManager
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize


/**
 * Data class to store latitude and longitude used in google map
 *
 * This class is needed to be able to easily store latitude and longitude in firebase,
 * as [LatLng] does not provide with a default constructor.
 */
@Parcelize
data class CustomLatLng(var latitude: Double? = null, var longitude: Double? = null) : Parcelable {

    companion object {
        /**
         * Factory method to create a new [CustomLatLng] instance from an existing [LatLng].
         */
        fun fromMapLatLng(latLng: LatLng) = CustomLatLng(latLng.latitude, latLng.longitude)

        /**
         * Factory method to create a new [CustomLatLng] instance from an existing [Location]
         */
        fun fromLocation(location: Location) = CustomLatLng(location.latitude, location.longitude)
    }

    /**
     * Function to convert a [CustomLatLng] to a [LatLng].
     * Returns null if [latitude] or [longitude] is null
     */
    fun toMapLatLng(): LatLng? =
        latitude?.let { lat -> longitude?.let { long -> LatLng(lat, long) } }

    /**
     * Function to convert a [CustomLatLng] to a [Locatiom]
     * Returns null if [latitude] or [longitude] is null
     */
    fun toMapLocation(): Location? =
        latitude?.let { lat ->
            longitude?.let { long ->
                Location(LocationManager.GPS_PROVIDER).apply {
                    latitude = lat
                    longitude = long
                }
            }
        }

    /**
     * Computes the distance with the given [location]
     * @param location the location from which we want to know the distance
     * @return the distance between this CustomLatLng and the given [location]
     */
    fun distanceFrom(location: Location): Long? {
        return latitude?.let { lat ->
            longitude?.let { long ->
                val loc = Location(LocationManager.GPS_PROVIDER)

                loc.latitude = lat
                loc.longitude = long

                loc.distanceTo(location).toLong()
            }
        }

    }

    override fun toString(): String {
        return "lat=$latitude, long=$longitude"
    }
}