package com.github.sdp_begreen.begreen.map.location

import com.google.android.gms.location.LocationResult

interface LocationListener {
    fun locationResponse(locationResult: LocationResult)
}