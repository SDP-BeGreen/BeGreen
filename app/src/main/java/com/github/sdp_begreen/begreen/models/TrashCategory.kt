package com.github.sdp_begreen.begreen.models

import android.os.Parcelable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TrashCategory(val title: String, val color: Float, val value: Int) : Parcelable {

    // The title will be displayed in the screen to the user, so we prefer to define it in the strings.xml instead
    // of Capitalizing the toString() value.


    // TODO : use the strings.xml file for the title field. Since the class is a model class, it doesn't recognize this file.
    // TODO : Update it also in the action Action class, as well as in their tests.

    PAPER("Paper", BitmapDescriptorFactory.HUE_RED, 5),
    PLASTIC("Plastic", BitmapDescriptorFactory.HUE_AZURE, 10),
    ORGANIC("Organic", BitmapDescriptorFactory.HUE_GREEN, 3),
    GLASS("Glass", BitmapDescriptorFactory.HUE_MAGENTA, 15),
    ELECTRONIC("Electronic", BitmapDescriptorFactory.HUE_ROSE, 30),
    CLOTHES("Clothes", BitmapDescriptorFactory.HUE_VIOLET, 20),
    METAL("Metal", BitmapDescriptorFactory.HUE_YELLOW, 25);
}