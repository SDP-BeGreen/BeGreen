package com.github.sdp_begreen.begreen.models

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
data class CustomLatLng(var latitude: Double? = null, var longitude: Double? = null): Parcelable {

    companion object {
        /**
         * Factory method to create a new [CustomLatLng] instance from an existing [LatLng].
         */
        fun fromMapLatLng(latLng: LatLng) = CustomLatLng(latLng.latitude, latLng.longitude)
    }

    /**
     * Function to convert a [CustomLatLng] to a [LatLng]
     */
    fun toMapLatLng() = LatLng(latitude ?: 0.0, longitude ?: 0.0)

    override fun toString(): String {
        return "lat=$latitude, long=$longitude"
    }
}