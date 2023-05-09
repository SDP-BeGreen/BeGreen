package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TrashCategory(val title : String, val color : Float) : Parcelable {

    // The title will be displayed in the screen to the user, so we prefer to define it in the strings.xml instead
    // of Capitalizing the toString() value.


    // TODO : use the strings.xml file for the title field. Since the class is a model class, it doesn't recognize this file.
    // TODO : Update it also in the action Action class, as well as in their tests.

    PAPER("Paper", BitmapDescriptorFactory.HUE_RED),
    PLASTIC("Plastic", BitmapDescriptorFactory.HUE_AZURE),
    ORGANIC("Organic", BitmapDescriptorFactory.HUE_GREEN),
    GLASS("Glass", BitmapDescriptorFactory.HUE_MAGENTA),
    ELECTRONIC("Electronic", BitmapDescriptorFactory.HUE_ROSE),
    CLOTHES("Clothes", BitmapDescriptorFactory.HUE_VIOLET),
    METAL("Metal", BitmapDescriptorFactory.HUE_YELLOW);
}