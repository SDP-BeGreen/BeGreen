package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.models.*
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.utils.Permissions.hasPermissions
import com.github.sdp_begreen.begreen.utils.TinyDB
import com.github.sdp_begreen.begreen.utils.TinyDBKey
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.EventsFragmentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream

//argument constant
private const val ARG_URI = "uri"

class SendPostFragment : Fragment() {
    private var paramUri: String? = null
    private val db by inject<DB>()
    private val eventParticipantService by inject<EventParticipantService>()

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
        lifecycleScope.launch {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.mainCameraFragmentContainer, CameraWithUIFragment.newInstance())
            }
        }
    }

    private fun setUpShare(trashCategorySelector: Spinner) {
        val shareBtn = view?.findViewById<ImageView>(R.id.send_post)
        shareBtn?.setOnClickListener {

            // fetch current user. He is necessarily not null
            connectedUserViewModel.currentUser.value?.also { user ->

                //create a metadata file
                var metadata: TrashPhotoMetadata?

                //fetch description on UI
                view?.findViewById<TextInputEditText>(R.id.post_description).also {

                    val caption = it?.text.toString()

                    val category: TrashCategory =
                        TrashCategory.values()[trashCategorySelector.selectedItemPosition]

                    val location = userLocation?.let { loc -> CustomLatLng.fromLocation(loc) }

                    /*
                    //fetch category on UI
                    view?.findViewById<TextInputEditText>(R.id.post_category)?.also { cat ->
                        category = cat.text.toString()
                    }*/

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

                // Obtain reference to Firebase's '.info/connected' node to monitor connection state.
                val connectedRef = Firebase.database.getReference(".info/connected")

                // Attach a listener for a single event to check if client is currently connected to Firebase.
                connectedRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Retrieve connectivity state from snapshot. If data is null, assume not connected.
                        val connected = snapshot.getValue(Boolean::class.java) ?: false

                        // If connected, proceed with user update and photo sharing operations.
                        if (connected) {
                            Log.d("TAG", "connected")
                            // Launch coroutine to perform potentially long-running operations off the main UI thread.
                            lifecycleScope.launch {
                                // Update the user with the new photo and return to the camera.
                                updateUser(metadata, user)
                                returnToCamera()
                            }
                        // If not connected, store metadata and user information for later use when connectivity is re-established.
                        } else {
                            Log.d("TAG", "not connected")

                            // Use TinyDB for local data storage.
                            val tinyDB = TinyDB(this@SendPostFragment.requireContext())

                            // Retrieve existing metadata and add current one.
                            val metas = tinyDB.getListObject(TinyDBKey.METAS.key, TrashPhotoMetadata::class.java)
                            val newMetas = metas + metadata!!
                            tinyDB.putListObject(TinyDBKey.METAS.key, newMetas)

                            // Retrieve existing user data and add current user.
                            val users = tinyDB.getListObject(TinyDBKey.USERS.key, User::class.java)
                            val newUsers = users + user
                            tinyDB.putListObject(TinyDBKey.USERS.key, newUsers)

                            view?.findViewById<ImageView>(R.id.preview)?.drawable?.toBitmap()?.also { bitmap ->

                                // Get the stored metadata
                                val baos = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos)

                                // Convert the bitmap to a byte array and then to a base64 string for easier storage.
                                val b: ByteArray = baos.toByteArray()
                                val encoded: String = Base64.encodeToString(b, Base64.DEFAULT)
                                val bitmaps = tinyDB.getListString(TinyDBKey.BITMAPS.key)
                                bitmaps.add(encoded)
                                tinyDB.putListString(TinyDBKey.BITMAPS.key, bitmaps)

                                // Inform user about automatic post upload once connectivity is re-established.
                                Toast.makeText(this@SendPostFragment.context, "Your post will be automatically sent once online.", Toast.LENGTH_LONG).show()

                                // Return to camera
                                returnToCamera()
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("TAG", "Listener was cancelled")
                    }
                })

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
                    it.location?.also { location ->
                        contestsUpdateScores(category, location)
                    }
                }
            }
        }
    }

    private suspend fun contestsUpdateScores(
        trashCategory: TrashCategory,
        location: CustomLatLng
    ) {
        val userId = connectedUserViewModel.currentUser.value?.id
        val participationMap =
            eventsFragmentViewModel.participationMap.dropWhile { it.isEmpty() }.first()
        val contests = eventsFragmentViewModel.allEvents.dropWhile { it.isEmpty() }.first()

        userId?.also {
            contests.forEach { contest ->
                processContest(contest, it, participationMap, trashCategory, location)
            }
        }
    }

    private suspend fun processContest(
        contest: Contest,
        userId: String,
        participationMap: Map<String, Boolean>,
        trashCategory: TrashCategory,
        location: CustomLatLng
    ) {
        location.toMapLocation()?.let {
            if (participationMap[contest.id] == true &&
                contest.isActive() == true &&
                contest.isInRange(it) == true
            ) {
                val updatedParticipant = eventParticipantService.getParticipant(
                    RootPath.CONTESTS,
                    contest.id!!,
                    userId,
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