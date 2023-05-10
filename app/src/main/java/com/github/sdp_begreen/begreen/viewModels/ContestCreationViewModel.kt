package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel

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

    /**
     * Variable storing the contest title
     */
    var contestTitle: String? = null
        set(value) {
            field = value
        }
}