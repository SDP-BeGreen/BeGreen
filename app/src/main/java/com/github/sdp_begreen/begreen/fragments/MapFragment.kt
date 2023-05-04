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
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.models.CustomLatLng

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
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

    // Currently selected marker on the map, or null if no marker selected
    private var selectedMarker: Marker? = null

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

        // Setup the marker and map clicks listener action
        setupMarkerAndMapClicks()

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

        // Type selector
        val binTypeSelector: Spinner = requireView().findViewById(R.id.binTypeSelector)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TrashCategory.values()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binTypeSelector.adapter = adapter

        // Button
        val binBtn: Button = requireView().findViewById(R.id.binBtn)
        setUpBinButtonClick(binBtn, binTypeSelector)
    }

    /**
     * Helper function that sets up button click events listeners
     */
    private fun setUpBinButtonClick(binBtn: Button, binTypeSelector: Spinner) {
        binBtn.setOnClickListener {
            // If no marker is currently selected, add a new one at the current location
            if (selectedMarker == null) {
                // Get the TrashCategory from the selector
                val trashCategory: TrashCategory = TrashCategory.values()[binTypeSelector.selectedItemPosition]
                addNewBin(trashCategory)
            } else {
                // Remove the bin from the database
                lifecycleScope.launch {
                    // We always add the bin in the tag after the bin has been added to the database,
                    // hence the id is always valid and the "!!" is safe
                    db.removeBin((selectedMarker!!.tag as Bin).id!!)
                }

                // Remove the marker from the map
                selectedMarker!!.remove()
                selectedMarker = null

                binBtn.text = getString(R.string.add_new_bin)
                // Informs the user that his action took place
                Toast.makeText(requireContext(), "Bin removed",
                    Toast.LENGTH_SHORT).show()
            }

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
    private fun displayBinsMarkers(bins: List<Bin>) = bins.forEach{ addMarker(it) }

    /**
     * Helper function that setups marker and map click listener actions
     */
    private fun setupMarkerAndMapClicks() {

        val addNewBinBtn: Button = requireView().findViewById(R.id.binBtn)

        map.setOnMarkerClickListener {
            selectedMarker = it
            addNewBinBtn.text = getString(R.string.remove_bin)
            false
        }
        map.setOnMapClickListener {
            selectedMarker = null
            addNewBinBtn.text = getString(R.string.add_new_bin)
        }
    }

    /**
     * Helper function that add a bin marker to the user current location, with the given binType
     */
    private fun addNewBin(trashCategory: TrashCategory) {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        } else {

            userLocation?.apply {
                // Add a bin of type "binType" at the user current location
                Bin(trashCategory, LatLng(latitude, longitude))
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
                .position(bin.location)
                .title(bin.type.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(bin.type.color))
        )?.apply {
            tag = bin
        }
    }
}