package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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

    private var city: StateFlow<String>? = null
    private var country: StateFlow<String>? = null
    private var postalCode: StateFlow<String>? = null
    private var radius: StateFlow<Int>? = null
    private var latitude: StateFlow<Double>? = null
    private var longitude: StateFlow<Double>? = null

    /**
     * Variable storing the contest title
     */
    var contestTitle: String? = null
    var isPrivate: Boolean? = null
    var startDate: Long? = null
    var endDate: Long? = null
    var startHour: Int? = null
    var startMinute: Int? = null
    var endHour: Int? = null
    var endMinute: Int? = null


}