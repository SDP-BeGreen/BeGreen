package com.github.sdp_begreen.begreen.services

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.CustomLatLng
import java.io.IOException

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
                return try {
                    geocoder.getFromLocation(lat, lon, maxResult)
                } catch (e: IOException){
                    null
                }
            }
        }

        return mutableListOf()
    }

    override suspend fun getLongLat(address: String): CustomLatLng? {

        try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList.isNullOrEmpty()) {
                return null
            }
            val currAddress = addressList[0]
            return CustomLatLng(currAddress.latitude, currAddress.longitude)
        } catch (e: IOException) {
            return null
        }
    }
}