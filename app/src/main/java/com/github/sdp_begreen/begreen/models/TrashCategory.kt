package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable
import com.github.sdp_begreen.begreen.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory

enum class TrashCategory(val id : String, val title : Int, val color : Float) : Parcelable {

    // The title will be displayed in the screen to the user, so we prefer to define it in the strings.xml instead
    // of Capitalizing the toString() value.

    // The id is unique (stored in the database). We preferred to store a category by its "id" instead of its "value"
    // (i.e PLASTIC) because the value could be changed afterward.

    PAPER("0", R.string.paper, BitmapDescriptorFactory.HUE_RED),
    PLASTIC("1", R.string.plastic, BitmapDescriptorFactory.HUE_AZURE),
    ORGANIC("2", R.string.organic, BitmapDescriptorFactory.HUE_GREEN),
    GLASS("3", R.string.glass, BitmapDescriptorFactory.HUE_MAGENTA),
    ELECTRONIC("4", R.string.electronic, BitmapDescriptorFactory.HUE_ROSE),
    CLOTHES("5", R.string.clothes, BitmapDescriptorFactory.HUE_VIOLET),
    METAL("6", R.string.metal, BitmapDescriptorFactory.HUE_YELLOW);

    /*
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        0,
        0.0f
    ) {

        return getCategoryById(id)
    }*/

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrashCategory> {

        override fun createFromParcel(parcel: Parcel): TrashCategory {
            return getCategoryById(parcel.readString()!!)
        }

        override fun newArray(size: Int): Array<TrashCategory?> {
            return arrayOfNulls(size)
        }

        private fun getCategoryById(id : String) : TrashCategory {
            return values().find { it.id == id } ?: throw IllegalArgumentException("Invalid id")
        }
    }
}