package com.github.sdp_begreen.begreen.map

import com.github.sdp_begreen.begreen.models.TrashCategory
import com.google.android.gms.maps.model.LatLng

data class Bin(var id: String? = null, val type: TrashCategory, val lat : Double, val long : Double) {

    // Default constructor required to deserialized object retrieved from firebase
    constructor() : this(
        null,
        TrashCategory.PLASTIC,
        0.0,
        0.0
    )

    constructor(type: TrashCategory, location: LatLng) :this(
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