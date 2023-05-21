package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import com.github.sdp_begreen.begreen.R
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.github.sdp_begreen.begreen.dialog.ContestMapDialog

/**
 * View model to use with the [ContestMapDialog] class to handle the logic
 */
class ContestMapDialogViewModel: ViewModel() {

    private val mutableSelectedButton = MutableStateFlow(SelectedButton.LOCATION_BUTTON)
    private val mutableLocationMarker = MutableStateFlow<Marker?>(null)
    private val mutableRadiusMarker = MutableStateFlow<Marker?>(null)
    private val mutableDrawnCircle = MutableStateFlow<Circle?>(null)


    val selectedButton = mutableSelectedButton.asStateFlow()
    val locationMarker = mutableLocationMarker.asStateFlow()
    val radiusMarker = mutableRadiusMarker.asStateFlow()
    val drawnCircle = mutableDrawnCircle.asStateFlow()

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

    /**
     * Add a new location marker
     *
     * If the new marker is null, keep the old marker and do nothing
     *
     * @param marker The new marker to add
     */
    fun newLocationMarker(marker: Marker?) {
        // Only remove previous marker if new one is non null
        marker?.also { nonNullMarker ->
            mutableLocationMarker.value?.also {
                it.remove()
            }
            mutableLocationMarker.value = nonNullMarker
        }
    }

    /**
     * Add a new radius marker
     *
     * If the new marker is null, keep the old marker and do nothing
     *
     * @param marker The new marker to add
     */
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
     * Add a new circle
     *
     * If the new circle is null, keep the old circle and do nothing
     *
     * @param circle the new circle to add
     */
    fun newCircle(circle: Circle?) {
        circle?.also { nonNullCircle ->
            mutableDrawnCircle.value?.also {
                it.remove()
            }
            mutableDrawnCircle.value = nonNullCircle
        }
    }

    /**
     * Enum that represents the two button to add markers
     */
    enum class SelectedButton(val id: Int) {
        LOCATION_BUTTON(R.id.create_contest_location_button),
        RADIUS_BUTTON(R.id.create_contest_radius_button)
    }
}