package com.github.sdp_begreen.begreen.services

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.github.sdp_begreen.begreen.models.CustomLatLng

/**
 * Implementation of the GeocodingService, that uses the actual Geocoder from android
 *
 * @param context The application context
 */
class GeocodingServiceImpl(context: Context) : GeocodingService {

    private val geocoder: Geocoder

    init {
        geocoder = Geocoder(context)
    }

    override suspend fun getAddresses(latLng: CustomLatLng, maxResult: Int): MutableList<Address>? {
        latLng.latitude?.also { lat ->
            latLng.longitude?.also { lon ->
                return geocoder.getFromLocation(lat, lon, maxResult)
                //textView.text = addresses?.first()?.locality
            }
        }

        return mutableListOf()
    }
}