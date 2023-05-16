package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

//argument constant
private const val ARG_URI = "uri"

class SendPostFragment : Fragment() {
    private var paramUri: String? = null
    private val db by inject<DB>()
    //private val eventService by inject<EventService>()
    //private val eventParticipantService by inject<EventParticipantService>()
    //private val contestPhotoService by inject<ContestPhotoService>()
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    /*private val eventsFragmentViewModel by viewModels<EventsFragmentViewModel<Contest, ContestParticipant>> {
        EventsFragmentViewModel.factory(
            connectedUserViewModel.currentUser,
            RootPath.CONTESTS,
            Contest::class.java,
            ContestParticipant::class.java
        )
    }*/

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
        initView()
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
                // update the user's score in the active contests
                //it.trashCategory?.also { category -> contestsUpdateScoresAndPhotos(user, category, bitmap, metadata) }

                // store the new User in firebase
                db.addUser(user, user.id)

                // once stored, set again the new user along with his metadata in current
                // user, for consistency
                connectedUserViewModel.setCurrentUser(user, true)

                // Display toast
                Toast.makeText(requireContext(), R.string.photo_shared_success, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /*private fun contestsUpdateScoresAndPhotos(user: User, trashCategory: TrashCategory, bitmap: Bitmap, metadata: TrashPhotoMetadata) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(user, trashCategory, bitmap, metadata)

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                userLocation = location
                lifecycleScope.launch { updateScore(user, trashCategory, location, bitmap, metadata) }
            }
            //userLocation?.also { lifecycleScope.launch { updateScore(user, trashCategory, it, bitmap, metadata) } }
        }



    }

    private suspend fun updateScore(user: User, trashCategory: TrashCategory, location: Location, bitmap: Bitmap, trashPhotoMetadata: TrashPhotoMetadata) {
        Log.d("SendPostFragment", "User location: " + userLocation)
        Log.d("SendPostFragment", "Updating user's contest score")
        user.contestIdsList?.forEach {contestId ->
            val event = eventService.getEvent(contestId, RootPath.CONTESTS, Contest::class.java)
            Log.d("SendPostFragment", "User takes part in contest with id: $contestId")
            Log.d("SendPostFragment", "Is event active? " + event.isActive())
            Log.d("SendPostFragment", "Is event in range? " + event.isInRange(location))
            if (event.isActive() && event.isInRange(location)) {
                val participant = eventParticipantService.getParticipant(RootPath.CONTESTS, contestId, user.id, ContestParticipant::class.java)
                eventsFragmentViewModel.participate(contestId)
                //val participants = eventParticipantService.getAllParticipants(RootPath.CONTESTS, contestId, ContestParticipant::class.java)
                //contestPhotoService.addContestsPhotoAndUpdateScore(contestId, trashPhotoMetadata, bitmap, participant)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestPermissions(user: User, trashCategory: TrashCategory, bitmap: Bitmap, metadata: TrashPhotoMetadata){
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                    userLocation = location
                    lifecycleScope.launch { updateScore(user, trashCategory, location, bitmap, metadata) }
                }

            } else {
                Toast.makeText(requireActivity(), getString(R.string.cant_update_contests_score), Toast.LENGTH_LONG).show()
            }
        }
    }*/

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