package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import com.github.sdp_begreen.begreen.R
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContestMapDialogViewModel: ViewModel() {

    private val mutableSelectedButton = MutableStateFlow(SelectedButton.LOCATION_BUTTON)
    private val mutableLocationMarker = MutableStateFlow<Marker?>(null)
    private val mutableRadiusMarker = MutableStateFlow<Marker?>(null)
    var drawnCircle: Circle? = null
        set(value) {
            value?.also { nonNullCircle ->
                field?.also {
                    it.remove()
                }
                field = nonNullCircle
            }
        }


    val selectedButton = mutableSelectedButton.asStateFlow()
    val locationMarker = mutableLocationMarker.asStateFlow()
    val radiusMarker = mutableRadiusMarker.asStateFlow()

    /**
     * Select a new button
     *
     * If the same button that was already selected is selected, nothing happens
     *
     * @param selectedButton The newly selected button
     */
    fun selectButton(selectedButton: SelectedButton) {
        if (mutableSelectedButton.value != selectedButton) {
            mutableSelectedButton.value = selectedButton
        }
    }

    fun newLocationMarker(marker: Marker?) {
        // Only remove previous marker if new one is non null
        marker?.also { nonNullMarker ->
            mutableLocationMarker.value?.also {
                it.remove()
            }
            mutableLocationMarker.value = nonNullMarker
        }
    }

    fun newRadiusMarker(marker: Marker?) {
        // Only remove previous marker if new one is non null
        marker?.also { nonNullMarker ->
            mutableRadiusMarker.value?.also {
                it.remove()
            }
            mutableRadiusMarker.value = nonNullMarker
        }
    }

    /**
     * Enum that represents the two possible button
     */
    enum class SelectedButton(val id: Int) {
        LOCATION_BUTTON(R.id.create_contest_location_button),
        RADIUS_BUTTON(R.id.create_contest_radius_button)
    }
}