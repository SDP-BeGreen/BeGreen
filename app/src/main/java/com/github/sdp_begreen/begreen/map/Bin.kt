package com.github.sdp_begreen.begreen.map

import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.google.android.gms.maps.model.LatLng

data class Bin(var id: String? = null, val type: TrashCategory, val location : LatLng) {

    // Default constructor required to deserialized object retrieved from firebase
    constructor() : this(
        null,
        TrashCategory.PLASTIC,
        LatLng(0.0,0.0)
    )

    constructor(type: TrashCategory, location: LatLng) :this(
        null,
        type,
        location
    )

    constructor(type: TrashCategory, location: CustomLatLng) :this(
        null,
        type,
        LatLng(location.toMapLatLng().latitude, location.toMapLatLng().longitude)
    )
}