package com.github.sdp_begreen.begreen.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.utils.Permissions.hasPermissions
import com.github.sdp_begreen.begreen.viewModels.ContestMapDialogViewModel
import com.github.sdp_begreen.begreen.viewModels.ContestMapDialogViewModel.SelectedButton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


/**
 * This class represent a dialog fragment used to display the map with the location of the contest
 * and draw a circle with the radius. The dialog allow to easily select the location and
 * the radius by placing markers.
 */
class ContestMapDialog(private val listener: ContestMapDialogListener) : DialogFragment(),
    OnMapReadyCallback {

    private val contestMapDialogViewModel by viewModels<ContestMapDialogViewModel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                displayLocation()
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.location_permissions_not_granted),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_create_contest_map, container, false)

        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.create_contest_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        view.findViewById<MaterialButton>(R.id.create_contest_location_button).setOnClickListener {
            contestMapDialogViewModel.selectButton(SelectedButton.LOCATION_BUTTON)
        }
        view.findViewById<MaterialButton>(R.id.create_contest_radius_button).setOnClickListener {
            contestMapDialogViewModel.selectButton(SelectedButton.RADIUS_BUTTON)
        }

        setupButtonBackgroundToggle(view)
        setupApproveButton(view)
        setupCancelButton(view)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // Start by checking that the user has the permission to use his location to center the map
        addUserLocationLayer()
        setupAddMarker()
        setupRedrawCircleUponMarkerChange()
        setupDragMarkerListener()
        setupInitMarkerAndCircle()
    }

    /**
     * Helper function to initially draw the marker and the circle on the map
     */
    private fun setupInitMarkerAndCircle() {
        var initLocation: LatLng? = null
        var initRadius = 0.0
        arguments?.also { arg ->
            initLocation = arg.getParcelable<CustomLatLng?>(LOCATION_ARG)?.toMapLatLng()
            initRadius = arg.getDouble(RADIUS_ARG)
        }

        if (initLocation != null && initRadius > 0.0) {
            addLocationMarker(initLocation!!)
            addRadiusMarker(SphericalUtil.computeOffset(initLocation!!, initRadius, DEFAULT_HEADING))
            drawCircle(initLocation!!, initRadius)
        }
    }

    /**
     * Helper function to add the user location, and request permission if not already granted
     */
    private fun addUserLocationLayer() {
        if (hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            displayLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Helper function to setup the possibility to add a marker
     */
    private fun setupAddMarker() {
        map.setOnMapClickListener {
            when (contestMapDialogViewModel.selectedButton.value) {
                SelectedButton.LOCATION_BUTTON -> {
                    addLocationMarker(it)
                }
                SelectedButton.RADIUS_BUTTON -> {
                    addRadiusMarker(it)
                }
            }
        }
    }

    /**
     * Helper function to add a location marker
     */
    private fun addLocationMarker(pos: LatLng) = contestMapDialogViewModel.newLocationMarker(
        map.addMarker(
            MarkerOptions().position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
    )

    /**
     * Helper function to add a radius marker
     */
    private fun addRadiusMarker(pos: LatLng) = contestMapDialogViewModel.newRadiusMarker(
        map.addMarker(
            MarkerOptions().position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true)
        )
    )

    /**
     * Helper function to setup the listener for dragging marker event
     */
    private fun setupDragMarkerListener() {
        map.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                // it is only possible to drag the radius marker, so take position of
                // location marker as in viewModel

                // if location marker not yet set, do nothing
                contestMapDialogViewModel.locationMarker.value?.position?.also {
                    drawCircle(it, SphericalUtil.computeDistanceBetween(it, marker.position))
                }
            }
        })
    }

    /**
     * Helper function to setup the flow collection to redraw the circle if one of the
     * two marker have changed
     */
    private fun setupRedrawCircleUponMarkerChange() {
        lifecycleScope.launch {
            contestMapDialogViewModel.locationMarker
                .combine(contestMapDialogViewModel.radiusMarker) { location, radius ->
                    location to radius
                }.flowWithLifecycle(lifecycle).collect {
                    it.first?.position?.also { locationPos ->
                        it.second?.position?.also { radiusPos ->
                            drawCircle(locationPos, SphericalUtil.computeDistanceBetween(locationPos, radiusPos))
                        }
                    }
                }
        }
    }

    /**
     * Helper function to draw the circle
     */
    private fun drawCircle(centerPos: LatLng, radius: Double) {
        val circle = map.addCircle(
            CircleOptions()
                .center(centerPos)
                .radius(radius)
                .fillColor(requireContext().getColor(R.color.contestAreaOverlay))
        )
        contestMapDialogViewModel.newCircle(circle)
    }

    /**
     * Helper function to setup the background of the marker button to correctly mark
     * the selected button
     */
    private fun setupButtonBackgroundToggle(view: View) {
        lifecycleScope.launch {
            contestMapDialogViewModel.selectedButton.flowWithLifecycle(lifecycle)
                .collect { selectedButton ->
                    SelectedButton.values().forEach {
                        if (it != selectedButton) {
                            view.findViewById<MaterialButton>(it.id)
                                .setBackgroundColor(requireContext().getColor(R.color.white))
                        }
                    }
                    view.findViewById<MaterialButton>(selectedButton.id)
                        .setBackgroundColor(requireContext().getColor(R.color.middle_light_grey))
                }
        }
    }

    /**
     * Helper function to setup the approve button
     *
     * It will simply call the listener function, to notify the create contest fragment about
     * the new values
     *
     * And close the dialog
     */
    private fun setupApproveButton(view: View) {
        view.findViewById<MaterialButton>(R.id.create_contest_map_approve_button)
            .setOnClickListener {
                val location = contestMapDialogViewModel.locationMarker.value?.position?.let {
                    CustomLatLng.fromMapLatLng(it)
                }
                val radius =
                    contestMapDialogViewModel.locationMarker.value?.position?.let { locationPos ->
                        contestMapDialogViewModel.radiusMarker.value?.position?.let { radiusPos ->
                            SphericalUtil.computeDistanceBetween(locationPos, radiusPos)
                        }
                    }
                listener.onDialogApprove(location, radius)
                dismiss()
            }
    }

    private fun setupCancelButton(view: View) {
        view.findViewById<MaterialButton>(R.id.create_contest_map_cancel_button)
            .setOnClickListener { dismiss() }
    }


    /**
     * Helper function to setup the display location functionality
     *
     * We can silent this warning, as this method is ensured to be called only
     * in places where the permission have been granted
     */
    @SuppressLint("MissingPermission")
    private fun displayLocation() {
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            location?.also {
                // either center on user, or if initial position, center on it
                val latLng =
                    arguments?.getParcelable<CustomLatLng?>(LOCATION_ARG)?.toMapLatLng() ?: LatLng(
                        it.latitude,
                        it.longitude
                    )
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng, INIT_ZOOM_FACTOR
                    )
                )
            }
        }

    }

    /**
     * Interface that the Activity of Fragment that call this Dialog must implement
     *
     * this method will be called when the user approve what has been done on the fragment
     */
    interface ContestMapDialogListener {
        fun onDialogApprove(location: CustomLatLng?, radius: Double?)
    }

    companion object {

        private const val LOCATION_ARG = "Location"
        private const val RADIUS_ARG = "Radius"
        private const val INIT_ZOOM_FACTOR = 11f
        private const val DEFAULT_HEADING = 0.0

        /**
         * Method to create a new factory for this [DialogFragment], as it needs a special
         * parameter
         */
        fun factory(listener: ContestMapDialogListener): FragmentFactory =
            object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                    when (loadFragmentClass(classLoader, className)) {
                        ContestMapDialog::class.java -> ContestMapDialog(listener)
                        else -> super.instantiate(classLoader, className)
                    }
            }

        /**
         * Function to easily create a new instance of [ContestMapDialog]
         */
        fun newInstance(
            listener: ContestMapDialogListener,
            location: CustomLatLng? = null,
            radius: Double? = null
        ) = ContestMapDialog(listener).apply {
            arguments = Bundle().apply {
                putParcelable(LOCATION_ARG, location)
                radius?.also {
                    putDouble(RADIUS_ARG, it)
                }
            }
        }
    }
}
