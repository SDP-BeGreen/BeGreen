package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.models.*
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.utils.Permissions.hasPermissions
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject

//argument constant
private const val ARG_URI = "uri"

class SendPostFragment : Fragment() {
    private var paramUri: String? = null
    // Used to make sure that we register only 1 click on the post button
    private var sendingPost: Boolean = false

    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramUri = it.getString(ARG_URI)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkUserLocationPermissions()
        initView()
    }

    /**
     * Helper function to setup the behavior of the trash category selector
     */
    private fun setUpTrashCategorySpinner(trashCategorySelector: Spinner) {

        // Type selector
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TrashCategory.values().map { trash -> trash.title }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        trashCategorySelector.adapter = adapter
    }


    /**
     * Displays the user current location assuming that location permissions are granted
     */
    @SuppressLint("MissingPermission")
    private fun updateUserLocation() {

        // Fetch and display the user's current location
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            userLocation = location
        }
    }

    private fun checkUserLocationPermissions() {

        if (hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION))
            updateUserLocation()
        else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

                if (isGranted) {
                    updateUserLocation()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.cant_update_contests_score),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun initView() {
        //load image
        Picasso.Builder(requireContext()).build().load(paramUri)
            .into(view?.findViewById(R.id.preview))
        val trashCategorySelector: Spinner =
            requireView().findViewById(R.id.post_trash_category_selector)
        setUpTrashCategorySpinner(trashCategorySelector)
        setUpCancel()
        setUpShare(trashCategorySelector)
    }

    private fun setUpCancel() {
        val cancelBtn = view?.findViewById<ImageView>(R.id.cancel_post)
        cancelBtn?.setOnClickListener {
            returnToCamera()
        }
    }

    private fun returnToCamera() {
        //return to camera fragment
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.mainCameraFragmentContainer, CameraWithUIFragment.newInstance())
        }
    }

    private fun setUpShare(trashCategorySelector: Spinner) {
        val shareBtn = view?.findViewById<ImageView>(R.id.send_post)
        shareBtn?.setOnClickListener {

            if (!sendingPost) {
                // fetch current user. He is necessarily not null
                connectedUserViewModel.currentUser.value?.also { user ->
                    sendingPost = true
                    //create a metadata file
                    var metadata: TrashPhotoMetadata?

                    //fetch description on UI
                    view?.findViewById<TextInputEditText>(R.id.post_description).also {

                        val caption = it?.text.toString()

                        val category: TrashCategory =
                            TrashCategory.values()[trashCategorySelector.selectedItemPosition]

                        val location = userLocation?.let { loc -> CustomLatLng.fromLocation(loc) }

                        metadata =
                            TrashPhotoMetadata(
                                null,
                                ParcelableDate.now,
                                user.id,
                                caption,
                                category,
                                location
                            )
                    }

                    // Update the user and return
                    view?.findViewById<ImageView>(R.id.preview)?.drawable?.toBitmap()?.also {
                        (activity as MainActivity).sendPost(metadata, user, it)
                    }

                    // Changes fragment
                    returnToCamera()
                }
            }
        }
    }

    /**
     * Companion object to create fragment
     * with arguments
     */
    companion object {
        @JvmStatic
        fun newInstance(imageUri: String) =
            SendPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, imageUri)
                }
            }
    }
}