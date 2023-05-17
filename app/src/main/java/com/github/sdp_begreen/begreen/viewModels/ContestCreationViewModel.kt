package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel that will be used to persist changes in Contest creation
 *
 * Such persistence system is useful to keep modification upon configuration changes,
 * such as device rotation, to not lose all modification we made.
 *
 * As [ViewModel] are not destroyed upon activity or fragment destruction when it is a
 * configuration change
 */

class ContestCreationViewModel : ViewModel() {

    //Private mutable state flow variables
    private var mutableCity = MutableStateFlow<String?>(null)
    private var mutableCountry = MutableStateFlow<String?>(null)
    private var mutablePostalCode = MutableStateFlow<String?>(null)
    private var mutableRadius = MutableStateFlow<Int?>(null)
    private var mutableLatitude = MutableStateFlow<Double?>(null)
    private var mutableLongitude = MutableStateFlow<Double?>(null)

    /**
     * Variable storing the contest title
     */
    var contestTitle: String? = null

    /**
     * Variable storing the contest privacy
     */
    var isPrivate: Boolean = false

    /**
     * Variable storing the contest start date
     */
    var startDate: Long? = null

    /**
     * Variable storing the contest end date
     */
    var endDate: Long? = null

    /**
     * Variable storing the contest start hour
     */
    var startHour: Int? = null

    /**
     * Variable storing the contest start minute
     */
    var startMinute: Int? = null

    /**
     * Variable storing the contest end hour
     */
    var endHour: Int? = null

    /**
     * Variable storing the contest end minute
     */
    var endMinute: Int? = null

    /**
     * Variable storing the contest city input
     */
    val city = mutableCity.asStateFlow()

    /**
     * Variable storing the contest country input
     */
    val country = mutableCountry.asStateFlow()

    /**
     * Variable storing the contest postal code input
     */
    val postalCode = mutablePostalCode.asStateFlow()

    /**
     * Variable storing the contest radius input
     */
    val radius = mutableRadius.asStateFlow()

    /**
     * Variable storing the contest latitude input
     */
    val latitude = mutableLatitude.asStateFlow()

    /**
     * Variable storing the contest longitude input
     */
    val longitude = mutableLongitude.asStateFlow()

    /**
     * Function to call to edit city flow
     */
    fun editCity(city: String?) : Boolean {
        if(city == null) return false
        if(city.isEmpty()) return false
        if(city.contains(Regex("[0-9]"))) return false
        mutableCity.value = city
        return true
    }

    /**
     * Function to call to edit country flow
     */
    fun editCountry(country: String?) : Boolean {
        if(country == null) return false
        if(country.isEmpty()) return false
        if(country.contains(Regex("[0-9]"))) return false
        mutableCountry.value = country
        return true
    }

    /**
     * Function to call to edit postal code flow
     */
    fun editPostalCode(postalCode: String?) : Boolean {
        if(postalCode == null) return false
        if(postalCode.isEmpty()) return false
        if(postalCode.contains(Regex("[a-zA-Z]"))) return false
        mutablePostalCode.value = postalCode
        return true
    }

    /**
     * Function to call to edit radius flow
     */
    fun editRadius(radius: Int?) : Boolean {
        if(radius == null) return false
        if(radius < 0) return false
        mutableRadius.value = radius
        return true
    }

    /**
     * Function to call to edit latitude flow
     */
    fun editLatitude(latitude: Double?) : Boolean {
        if(latitude == null) return false
        if(latitude < -90 || latitude > 90) return false
        mutableLatitude.value = latitude
        return true
    }

    /**
     * Function to call to edit longitude flow
     */
    fun editLongitude(longitude: Double?) : Boolean {
        if(longitude == null) return false
        if(longitude < -180 || longitude > 180) return false
        mutableLongitude.value = longitude
        return true
    }

    /**
     * Function to call to check if the contest creation is valid
     */
    fun isContestCreationValid() : Boolean {
        return true
    }

}