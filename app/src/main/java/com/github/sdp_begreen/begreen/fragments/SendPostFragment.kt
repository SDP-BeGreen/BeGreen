package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

//argument constant
private const val ARG_URI = "uri"

class SendPostFragment : Fragment() {
    private var paramUri: String? = null
    private val db by inject<DB>()
    private val eventParticipantService by inject<EventParticipantService>()

    //private val eventService by inject<EventService>()
    //private val eventParticipantService by inject<EventParticipantService>()
    //private val contestPhotoService by inject<ContestPhotoService>()
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private val eventsFragmentViewModel by viewModels<EventsFragmentViewModel<Contest, ContestParticipant>> {
        EventsFragmentViewModel.factory(
            connectedUserViewModel.currentUser,
            RootPath.CONTESTS,
            Contest::class.java,
            ContestParticipant::class.java
        )
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private var userLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramUri = it.getString(ARG_URI)
        }
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
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
     * Displays the user current location assuming that location permissions are granted
     */
    @SuppressLint("MissingPermission")
    private fun displayUserLocation(/*user: User, category: TrashCategory, bitmap: Bitmap, metadata: TrashPhotoMetadata*/) {

        // Fetch and display the user's current location
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            userLocation = location
            //contestsUpdateScoresAndPhotos(user, category, bitmap, metadata, location)

        }
    }

    private fun checkUserLocationPermissions(/*user: User, category: TrashCategory, bitmap: Bitmap, metadata: TrashPhotoMetadata*/) {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

                if (isGranted) {
                    displayUserLocation(/*user, category, bitmap, metadata*/)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.location_permissions_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        } else {
            displayUserLocation(/*user, category, bitmap, metadata*/)
        }
    }

    private fun initView() {
        //load image
        Picasso.Builder(requireContext()).build().load(paramUri)
            .into(view?.findViewById(R.id.preview))
        setUpCancel()
        setUpShare()
    }

    private fun setUpCancel() {
        val cancelBtn = view?.findViewById<ImageView>(R.id.cancel_post)
        cancelBtn?.setOnClickListener {
            returnToCamera()
        }
    }

    private fun returnToCamera() {
        //return to camera fragment
        lifecycleScope.launch {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.mainCameraFragmentContainer, CameraWithUIFragment.newInstance())
            }
        }
    }

    private fun setUpShare() {
        val shareBtn = view?.findViewById<ImageView>(R.id.send_post)
        shareBtn?.setOnClickListener {

            // fetch current user. He is necessarily not null
            connectedUserViewModel.currentUser.value?.also { user ->

                //create a metadata file
                var metadata: TrashPhotoMetadata?

                //fetch description on UI
                view?.findViewById<TextInputEditText>(R.id.post_description).also {

                    val caption = it?.text.toString()

                    // TODO : Let the user choose the category like what we did in googlemap for the bin category
                    val category = TrashCategory.ORGANIC

                    /*
                    //fetch category on UI
                    view?.findViewById<TextInputEditText>(R.id.post_category)?.also { cat ->
                        category = cat.text.toString()
                    }*/

                    metadata =
                        TrashPhotoMetadata(null, ParcelableDate.now, user.id, caption, category)

                }

                // Update the user and return
                lifecycleScope.launch {
                    updateUser(metadata, user)
                    returnToCamera()
                }
            }
        }
    }

    /**
     * Helper function to update the user after posting a photo
     */
    private suspend fun updateUser(metadata: TrashPhotoMetadata?, user: User) {
        view?.findViewById<ImageView>(R.id.preview)?.drawable?.toBitmap()?.also { bitmap ->

            // Get the stored metadata
            val storedMetadata = metadata?.let {
                db.addTrashPhoto(bitmap, it)
            }

            storedMetadata?.let {

                // update the user with the new photo metadata and update its score
                user.addPhotoMetadata(it)
                user.score += it.trashCategory?.value ?: 0

                // store the new User in firebase
                db.addUser(user, user.id)

                // once stored, set again the new user along with his metadata in current
                // user, for consistency
                connectedUserViewModel.setCurrentUser(user, true)

                // Display toast
                Toast.makeText(requireContext(), R.string.photo_shared_success, Toast.LENGTH_SHORT)
                    .show()

                // update the user's score in the active contests
                it.trashCategory?.also { category ->
                    userLocation?.also { location ->
                        contestsUpdateScores(category, location)
                    }
                }
            }
        }
    }

    private suspend fun contestsUpdateScores(
        trashCategory: TrashCategory,
        location: Location,
    ) {
        Log.d("SendPostFragment", "User location: " + location)
        Log.d("SendPostFragment", "Updating user's contest score")
        Log.d("SendPostFragment", "User id: " + connectedUserViewModel.currentUser.value?.id)

        connectedUserViewModel.currentUser.value?.id?.also { participantId ->
            val participationMap = eventsFragmentViewModel.participationMap./*last() */dropWhile { it.isEmpty() }.first()
            Log.d("SendPostFragment", "Contest IDs: " + eventsFragmentViewModel.allEvents.value)

            eventsFragmentViewModel.allEvents.dropWhile { it.isEmpty() }.first().forEach { contest ->
                Log.d("SendPostFragment", "ContestID: " + contest.id + " participates?: " + (participationMap[contest.id] == true) +
                " isActive?: " + contest.isActive() + " is in range?: " + contest.isInRange(location))
                if (participationMap[contest.id] == true /*&& contest.isActive()*/ //TODO: uncomment this when active Contests will be in the flow
                    && contest.isInRange(location)
                ) {
                    val updatedParticipant = eventParticipantService.getParticipant(
                        RootPath.CONTESTS,
                        contest.id!!,
                        participantId,
                        ContestParticipant::class.java
                    )
                    eventParticipantService.addParticipant(
                        RootPath.CONTESTS,
                        contest.id!!,
                        updatedParticipant.copy(
                            score = updatedParticipant.score?.plus(trashCategory.value)
                                ?: trashCategory.value
                        )
                    )
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