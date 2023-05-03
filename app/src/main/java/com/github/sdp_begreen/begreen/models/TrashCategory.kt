package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable
import com.github.sdp_begreen.begreen.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory

enum class TrashCategory(val id : String, val title : String, val color : Float) : Parcelable {

    // The title will be displayed in the screen to the user, so we prefer to define it in the strings.xml instead
    // of Capitalizing the toString() value.

    // The id is unique (stored in the database). We preferred to store a category by its "id" instead of its "value"
    // name (i.e PLASTIC) because the value name could be changed afterward.

    // TODO : use the strings.xml file for the title field. Since the class is a model class, it doesn't recognize this file.
    // TODO : Update it also in the action Action class, as well as in their tests.

    PAPER("0", "Paper", BitmapDescriptorFactory.HUE_RED),
    PLASTIC("1", "Plastic", BitmapDescriptorFactory.HUE_AZURE),
    ORGANIC("2", "Organic", BitmapDescriptorFactory.HUE_GREEN),
    GLASS("3", "Glass", BitmapDescriptorFactory.HUE_MAGENTA),
    ELECTRONIC("4", "Electronic", BitmapDescriptorFactory.HUE_ROSE),
    CLOTHES("5", "Clothes", BitmapDescriptorFactory.HUE_VIOLET),
    METAL("6", "Metal", BitmapDescriptorFactory.HUE_YELLOW);


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