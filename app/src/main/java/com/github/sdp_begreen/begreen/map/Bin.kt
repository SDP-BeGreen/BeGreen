package com.github.sdp_begreen.begreen.map

import com.google.android.gms.maps.model.LatLng

data class Bin(var id: String? = null, val type: BinType, val lat : Double, val long : Double) {

    // Default constructor required to deserialized object retrieved from firebase
    constructor() : this(
        null,
        BinType.PLASTIC,
        0.0,
        0.0
    )

    constructor(type: BinType, location: LatLng) :this(
        null,
        type,
        location.latitude,
        location.longitude
    )

    /**
     * Getter for the location (latitude, longitude) of the bin
     *
     * @return the location of the bin
     */
    fun location(): LatLng = LatLng(lat, long)
}