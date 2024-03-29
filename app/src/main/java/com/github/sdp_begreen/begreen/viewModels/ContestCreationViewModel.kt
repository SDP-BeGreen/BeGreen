package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.services.GeocodingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


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

    private val geocodingApi by inject<GeocodingService>(GeocodingService::class.java)

    //Private mutable state flow variables
    private var mutableCity = MutableStateFlow<String?>("Lausanne")
    private var mutableCountry = MutableStateFlow<String?>("Switzerland")
    private var mutablePostalCode = MutableStateFlow<String?>("1010")
    private var mutableRadius = MutableStateFlow<Double?>(1000.0)
    private var mutableLatLng = MutableStateFlow<CustomLatLng?>(null)
    private var mutableStartDate = MutableStateFlow<Long?>(null)
    private var mutableEndDate = MutableStateFlow<Long?>(null)
    private var mutableStartHour = MutableStateFlow<Int?>(null)
    private var mutableStartMinute = MutableStateFlow<Int?>(null)
    private var mutableEndHour = MutableStateFlow<Int?>(null)
    private var mutableEndMinute = MutableStateFlow<Int?>(null)

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
    var startDate = mutableStartDate.asStateFlow()

    /**
     * Variable storing the contest end date
     */
    var endDate = mutableEndDate.asStateFlow()

    /**
     * Variable storing the contest start hour
     */
    var startHour = mutableStartHour.asStateFlow()

    /**
     * Variable storing the contest start minute
     */
    var startMinute = mutableStartMinute.asStateFlow()

    /**
     * Variable storing the contest end hour
     */
    var endHour = mutableEndHour.asStateFlow()

    /**
     * Variable storing the contest end minute
     */
    var endMinute = mutableEndMinute.asStateFlow()

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
     * Variable storing the contest customLongLat
     */
    val customLongLat = mutableLatLng.asStateFlow()

    fun editStartDate(date: Long?): Boolean {
        if (date == null) return false
        if (date < System.currentTimeMillis()) return false
        mutableStartDate.value = date
        return true
    }

    fun editEndDate(date: Long?): Boolean {
        if (date == null) return false
        if (date < System.currentTimeMillis()) return false
        mutableEndDate.value = date
        return true
    }

    fun editStartHour(hour: Int?): Boolean {
        if (hour == null) return false
        if (hour < 0 || hour > 23) return false
        mutableStartHour.value = hour
        return true
    }

    fun editStartMinute(minute: Int?): Boolean {
        if (minute == null) return false
        if (minute < 0 || minute > 59) return false
        mutableStartMinute.value = minute
        return true
    }

    fun editEndHour(hour: Int?): Boolean {
        if (hour == null) return false
        if (hour < 0 || hour > 23) return false
        mutableEndHour.value = hour
        return true
    }

    fun editEndMinute(minute: Int?): Boolean {
        if (minute == null) return false
        if (minute < 0 || minute > 59) return false
        mutableEndMinute.value = minute
        return true
    }

    /**
     * Function to call to edit city flow
     */
    fun editCity(city: String?): Boolean {
        if (city == null) return false
        if (city.isEmpty()) return false
        if (city.contains(Regex("[0-9]"))) return false
        mutableCity.value = city
        changeLatLongIfCorrectFromManualInput()
        return true
    }

    /**
     * Function to call to edit country flow
     */
    fun editCountry(country: String?): Boolean {
        if (country == null) return false
        if (country.isEmpty()) return false
        if (country.contains(Regex("[0-9]"))) return false
        mutableCountry.value = country
        changeLatLongIfCorrectFromManualInput()
        return true
    }

    /**
     * Function to call to edit postal code flow
     */
    fun editPostalCode(postalCode: String?): Boolean {
        if (postalCode == null) return false
        if (postalCode.isEmpty()) return false
        if (postalCode.contains(Regex("[a-zA-Z]"))) return false
        mutablePostalCode.value = postalCode
        changeLatLongIfCorrectFromManualInput()
        return true
    }

    /**
     * Function to call to edit radius flow
     */
    fun editRadius(radius: Double?): Boolean {
        if (radius == null) return false
        if (radius < 0) return false
        mutableRadius.value = radius
        return true
    }

    /**
     * Function to call to edit LongLat flow
     */
    fun editLongLat(longLat: CustomLatLng?): Boolean {
        if (longLat == null) return false
        if (longLat.latitude == null) return false
        if (longLat.latitude!! < -90 || longLat.latitude!! > 90) return false
        if (longLat.longitude == null) return false
        if (longLat.longitude!! < -180 || longLat.longitude!! > 180) return false


        mutableLatLng.value = longLat
        viewModelScope.launch {
            geocodingApi.getAddresses(longLat, 1)?.let {
                mutableCity.value = it[0].locality
                mutablePostalCode.value = it[0].postalCode
                mutableCountry.value = it[0].countryName
            }
        }

        return true
    }

    /**
     * Function to call to check if the contest creation is valid
     */
    fun isContestCreationValid(): Boolean {
        if (customLongLat.value == null) return false
        if (contestTitle == null) return false
        if (startDate.value == null) return false
        if (endDate.value == null) return false
        if (startHour.value == null) return false
        if (startMinute.value == null) return false
        if (endHour.value == null) return false
        if (endMinute.value == null) return false
        if (radius.value == null) return false
        return true
    }

    /**
     * Function to call to change longLat if address is correct
     */
    private fun changeLatLongIfCorrectFromManualInput() {
        val address = "${mutablePostalCode.value} ${mutableCity.value}, ${mutableCountry.value}}"

        viewModelScope.launch {
            geocodingApi.getLongLat(address)?.let {
                mutableLatLng.value = CustomLatLng(it.latitude, it.longitude)
            }
        }
    }

}