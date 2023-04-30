package com.github.sdp_begreen.begreen.models

import com.google.android.gms.maps.model.BitmapDescriptorFactory

enum class BinType(val id: String, val markerColor: Float) {

    PAPER("0", BitmapDescriptorFactory.HUE_RED),
    PLASTIC("1", BitmapDescriptorFactory.HUE_AZURE),
    ORGANIC("3", BitmapDescriptorFactory.HUE_GREEN),
    GLASS("4", BitmapDescriptorFactory.HUE_MAGENTA),
    ELECTRONIC("5", BitmapDescriptorFactory.HUE_ROSE),
    CLOTHES("6", BitmapDescriptorFactory.HUE_VIOLET),
    METAL("7", BitmapDescriptorFactory.HUE_YELLOW);

    companion object {
        fun getBinTypeById(id: String): BinType {
            for (binType in values()) {
                if (binType.id == id) {
                    return binType
                }
            }
            throw IllegalArgumentException("No BinType found with ID $id")
        }
    }
}