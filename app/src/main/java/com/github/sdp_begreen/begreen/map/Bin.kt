package com.github.sdp_begreen.begreen.map

import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata

data class Bin(var id: String? = null, val type: TrashCategory, val location : CustomLatLng) {

    // Default constructor required to deserialized object retrieved from firebase
    constructor() : this(
        null,
        TrashCategory.PLASTIC,
        CustomLatLng(0.0,0.0)
    )

    constructor(type: TrashCategory, location: CustomLatLng) :this(
        null,
        type,
        CustomLatLng(location.latitude, location.longitude)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bin) return false

        // ProfilePhotoMetadata is uniquely defined by the picture id because if we compare all fields, some of them
        // are obsolete in the database. Actually this was the case in our case.

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}