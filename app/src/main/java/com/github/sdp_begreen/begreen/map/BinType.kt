package com.github.sdp_begreen.begreen.map

import com.google.android.gms.maps.model.BitmapDescriptorFactory

enum class BinType(val markerColor: Float) {

    PAPER(BitmapDescriptorFactory.HUE_RED),
    PLASTIC(BitmapDescriptorFactory.HUE_AZURE),
    ORGANIC(BitmapDescriptorFactory.HUE_GREEN),
    GLASS(BitmapDescriptorFactory.HUE_MAGENTA),
    ELECTRONIC(BitmapDescriptorFactory.HUE_ROSE),
    CLOTHES(BitmapDescriptorFactory.HUE_VIOLET),
    METAL(BitmapDescriptorFactory.HUE_YELLOW);
}