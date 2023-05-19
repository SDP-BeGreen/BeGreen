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

    override suspend fun getAddresses(latLng: CustomLatLng, maxResult: Int): List<Address>? {
        latLng.latitude?.also { lat ->
            latLng.longitude?.also { lon ->
                return geocoder.getFromLocation(lat, lon, maxResult)
            }
        }

        return mutableListOf()
    }

    override suspend fun getLongLat(address: String): CustomLatLng? {
        val addressList = geocoder.getFromLocationName(address, 1)

        if (addressList?.isEmpty() ?: true) {
            return null
        }

        val currAddress = addressList!!.get(0);
        return CustomLatLng(currAddress.latitude, currAddress.longitude)
    }
}