package com.github.sdp_begreen.begreen.services

import android.location.Address
import com.github.sdp_begreen.begreen.models.CustomLatLng
import java.io.IOException

/**
 * Interface to expose method related to the geocoder
 */
interface GeocodingService {

    /**
     * Function to retrieve a list of address given some coordinates
     *
     * @param latLng The coordinates to lookup to find the address
     * @param maxResult The maximum number of results that we want to find
     *
     * @return A list of addresses composed of found address at the received coordinates
     *
     * @throws IOException Throws if an error occurred while geocoding the coordinates
     */
    @Throws(IOException::class)
    suspend fun getAddresses(latLng: CustomLatLng, maxResult: Int): List<Address>?

    /**
     * Function to retrieve coordinates given an address
     *
     * @param address The address to lookup to find the coordinates
     *
     * @return The coordinates of the received address
     *
     * @throws IOException Throws if an error occurred while geocoding the address
     */
    @Throws(IOException::class)
    suspend fun getLongLat(address: String): CustomLatLng?
}