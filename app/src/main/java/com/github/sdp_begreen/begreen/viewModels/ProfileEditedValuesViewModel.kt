package com.github.sdp_begreen.begreen.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

/**
 * ViewModel that will be used to persist changes in profile edition mode
 *
 * Such persistence system is useful to keep modification upon configuration changes,
 * such as device rotation, to not lose all modification we made.
 *
 * As [ViewModel] are not destroyed upon activity or fragment destruction when it is a
 * configuration change
 */
class ProfileEditedValuesViewModel: ViewModel() {
    private var isCurrentlyEditing = false

    /**
     * Variable storing the current edited display name
     */
    var displayName: String? = null
        set(value) {
            field = if (isCurrentlyEditing) value else null
        }

    /**
     * Variable storing the current edited email
     */
    var email: String? = null
        set(value) {
            field = if (isCurrentlyEditing) value else null
        }

    /**
     * Variable storing the current edited phone number
     */
    var phone: String? = null
        set(value) {
            field = if (isCurrentlyEditing) value else null
        }

    /**
     * Variable storing the current edited description
     */
    var description: String? = null
        set(value) {
            field = if (isCurrentlyEditing) value else null
        }

    /**
     * Variable storing the current edited profilePicutre
     */
    var profilePicture: Bitmap? = null
        set(value) {
            field = if (isCurrentlyEditing) value else null
        }

    /**
     * Function to call when we start editing the profile
     */
    fun startEditing() {
        isCurrentlyEditing = true
    }

    /**
     * Function to call when we are done editing the profile
     *
     * Calling this function will cause all field to be reset to null
     *
     * Be sure to save them to the DB before calling this method
     */
    fun finishEditing() {
        resetValues()
        isCurrentlyEditing = false
    }

    /**
     * Function that returns whether we are currently editing the profile or not
     */
    fun isCurrentlyEditing() = isCurrentlyEditing

    private fun resetValues() {
        displayName = null
        profilePicture = null
        email = null
        phone = null
        description = null
    }

}