package com.github.sdp_begreen.begreen

import com.github.sdp_begreen.begreen.models.Bin
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.google.android.gms.maps.model.LatLng

/**
 * This class will be removed once we will use the real bins database
 */
class BinsFakeDatabase {

    companion object {

        var fakeBins = mutableSetOf(
            Bin(1, TrashCategory.CLOTHES, LatLng(51.5074, -0.1278)),
            Bin(2, TrashCategory.ELECTRONIC, LatLng(40.7128, -74.0060)),
            Bin(3, TrashCategory.GLASS, LatLng(-33.8688, 151.2093)),
            Bin(4, TrashCategory.METAL, LatLng(35.6895, 139.6917))
        )

        fun removeBin(binId : Int) {

            fakeBins.removeIf { bin -> bin.id == binId }
        }

        fun addBin(bin : Bin) {

            fakeBins.add(bin)
        }
    }
}