package com.github.sdp_begreen.begreen.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

/**
 * Data class to store latitude and longitude used in google map
 *
 * This class is needed to be able to easily store latitude and longitude in firebase,
 * as [LatLng] does not provide with a default constructor.
 */
@Parcelize
data class CustomLatLng(var latitude: Double? = null, var longitude: Double? = null): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    /**
     * Function to convert a [CustomLatLng] to a [LatLng]
     */
    fun toMapLatLng() = LatLng(latitude ?: 0.0, longitude ?: 0.0)

    override fun toString(): String {
        return "lat=$latitude, long=$longitude"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomLatLng> {

        /**
         * Factory method to create a new [CustomLatLng] instance from an existing [LatLng].
         */
        fun fromMapLatLng(latLng: LatLng) = CustomLatLng(latLng.latitude, latLng.longitude)

        override fun createFromParcel(parcel: Parcel): CustomLatLng {
            return CustomLatLng(parcel)
        }

        override fun newArray(size: Int): Array<CustomLatLng?> {
            return arrayOfNulls(size)
        }
    }
}
