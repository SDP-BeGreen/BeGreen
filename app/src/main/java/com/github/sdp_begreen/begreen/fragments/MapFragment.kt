package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.map.BinType

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {

    // Get the db instance
    private val db by inject<DB>()

    companion object {
        private const val MAP_DEFAULT_ZOOM = 12f
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private var userLocation : Location? = null

    private val mapReadyCallback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         */

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        checkUserLocationPermissions()

        // Displays the markers (fetched from the database) on the map
        lifecycleScope.launch {
            displayBinsMarkers(db.getAllBins())
        }

        // Setup the markers click listener action
        setupInfoWindowClick()

        setupAddBinBtnAndSelector()
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        if (isGranted) {
            displayUserLocation()
        } else {
            Toast.makeText(requireActivity(), getString(R.string.location_permissions_not_granted), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)

        // Get the localisation of the user
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    /**
     * Helper function to setup the behavior of the "Add new post" button and its type selector
     */
    private fun setupAddBinBtnAndSelector() {

        val binTypeSelector: Spinner = requireView().findViewById(R.id.binTypeSelector)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            BinType.values()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binTypeSelector.adapter = adapter

        // If the user clicks on the "Add new bin" button it will add a new marker on the map
        // with the type of the currently selected BinType
        val addNewBinBtn: Button = requireView().findViewById(R.id.addNewBinBtn)
        addNewBinBtn.setOnClickListener {
            // Get the BinType from the selector
            val binType: BinType = BinType.values()[binTypeSelector.selectedItemPosition]
            addNewBin(binType)
        }
    }

    /**
     * Displays the user current location. Asks for permissions if needed.
     */
    private fun checkUserLocationPermissions() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        } else {

            displayUserLocation()
        }
    }

    /**
     * Displays the user current location assuming that location permissions are granted
     */
    @SuppressLint("MissingPermission")
    private fun displayUserLocation() {

        // Fetch and display the user's current location
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            displayBlueDotLocation(location)
            userLocation = location
        }
    }

    private fun displayBlueDotLocation(location: Location?) {

        // Got last known location. In some rare situations this can be null.
        if (location != null) {

            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MAP_DEFAULT_ZOOM))

        } else {

            Toast.makeText(requireActivity(), getString(R.string.user_current_location_error), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Helper functions that displays bins markers on the map, and sets up click events
     * Only called once when the view is created
     */
    private fun displayBinsMarkers(bins: Set<Bin>) = bins.forEach{ addMarker(it) }


    /**
     * Helper function that setups a marker's info window click listener action
     */
    private fun setupInfoWindowClick() {

        /**
         * According to this page (the last sentence, at the very end):
         * https://developers.google.com/maps/documentation/android-sdk/infowindows,
         * it is impossible to distinguish where the user clicks on an info window.
         * Thus, we cannot just add a button "remove" on the info window to remove the bin
         * The only alternative I found was to implement the removal of bins with a long click
         * on the marker's info window, which seems acceptable
         */

        map.setOnInfoWindowLongClickListener { marker ->

            // Remove the bin from the database
            lifecycleScope.launch {
                // We always add the bin in the tag after the bin has been added to the database,
                // hence the id is always valid
                db.removeBin((marker.tag as Bin).id!!)
            }

            // Remove the marker from the map
            marker.remove()

            // Informs the user that his action took place
            Toast.makeText(requireContext(), "Bin removed",
                Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Helper function that add a bin marker to the user current location, with the given binType
     */
    private fun addNewBin(binType: BinType) {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        } else {

            userLocation?.apply {
                // Add a bin of type "binType" at the user current location
                Bin(binType, LatLng(latitude, longitude))
                    .let {bin ->
                        lifecycleScope.launch {
                            // Add the new bin to the database
                            if (db.addBin(bin)) {
                                // Display the marker on the map
                                addMarker(bin)
                                // Informs the user that his action took place
                                Toast.makeText(requireContext(), "Bin added",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }
    }

    /**
     * Helper function to add a marker on the map and set its tag with the bin infos
     */
    private fun addMarker(bin: Bin){
        map.addMarker(
            MarkerOptions()
                .position(bin.location())
                .title(bin.type.toString())
                .snippet("Hold to remove the bin")
                .icon(BitmapDescriptorFactory.defaultMarker(bin.type.markerColor))
        )?.apply {
            tag = bin
        }
    }
}