package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.Actions
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.utils.BitmapsUtils
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import kotlinx.coroutines.launch
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileDetailsFragment(private val testActivityRegistry: ActivityResultRegistry? = null)
    : Fragment() {
    var user: User? = null

    private val connectedUserViewModel:
            ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_USER, User::class.java)
            } else {
                it.getParcelable(ARG_USER)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile_details, container, false)
        val rating: RatingBar = view.findViewById(R.id.fragment_profile_details_profile_rating)
        val userTextLevel: TextView = view.findViewById(R.id.fragment_profile_details_level)
        val userProgressBar: ProgressBar =
            view.findViewById(R.id.fragment_profile_details_user_progress)
        val followButton: Button = view.findViewById(R.id.fragment_profile_details_follow_button)
        val editButton: Button = view.findViewById(R.id.fragment_profile_details_edit_profile)
        val saveButton: Button = view.findViewById(R.id.fragment_profile_details_save_profile)
        val takePictureButton: ImageButton =
            view.findViewById(R.id.fragment_profile_details_take_picture)
        rating.rating = user?.score?.toFloat() ?: 0.0f
        userTextLevel.text = getString(
            R.string.user_details_level_text, user?.displayName ?: "Default User",
        )
        userProgressBar.progress = user?.progression ?: 0

        setUpUserInfo(view)
        setUpUserProfilePicture(view)
        setupFollowListener(followButton)
        setupEditButton(editButton)
        setupSaveButton(saveButton)
        setupTakePictureButton(takePictureButton)
        return view
    }

    /**
     * setup the observer on the currentUser to display user information
     *
     * If the user is the same as the connected one, then we display the connected user's information
     * otherwise simply the received user information's
     *
     * @param view The view from which to find the elements, required as it is not yet fully
     * initialized
     */
    private fun setUpUserInfo(view: View) {
        val profileDescription: TextView =
            view.findViewById(R.id.fragment_profile_details_profile_description)
        val name: TextView = view.findViewById(R.id.fragment_profile_details_profile_name)
        val profilePhone: TextView = view.findViewById(R.id.fragment_profile_details_profile_phone)
        val profileEmail: TextView = view.findViewById(R.id.fragment_profile_details_profile_email)

        connectedUserViewModel.currentUser.observe(viewLifecycleOwner) { cUser ->
            val userToUse = cUser?.let { if (it.id == user?.id) it else user } ?: user
            profileDescription.text =
                userToUse?.description ?: getString(R.string.nav_drawer_user_description)
            name.text = userToUse?.displayName ?: getString(R.string.nav_drawer_username)
            profilePhone.text = userToUse?.phone
            profileEmail.text = userToUse?.email
        }
    }

    /**
     * Helper function to setup the observer on the current profile picture to display
     */
    private fun setUpUserProfilePicture(view: View) {
        val profileImgView: ImageView =
            view.findViewById(R.id.fragment_profile_details_profile_image)
        connectedUserViewModel.currentUserProfilePicture.observe(viewLifecycleOwner) {
            if (connectedUserViewModel.currentUser.value?.id == user?.id) {
                val img = it ?: BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture)
                profileImgView.setImageBitmap(BitmapsUtils.rescaleImage(img,
                    PROFILE_PICTURE_DIM,
                    PROFILE_PICTURE_DIM
                ))
            } else {
                lifecycleScope.launch {
                    val img = user?.let { user ->
                        user.profilePictureMetadata?.let { pMetadata->
                            FirebaseDB.getUserProfilePicture(pMetadata, user.id)
                        }
                    } ?: BitmapFactory.decodeResource(resources, R.drawable.blank_profile_picture)
                    profileImgView.setImageBitmap(BitmapsUtils.rescaleImage(img,
                        PROFILE_PICTURE_DIM,
                        PROFILE_PICTURE_DIM
                    ))
                }
            }
        }
    }

    /**
     * Helper function to setup the edit button
     *
     * Setup if visible or not
     * setup its listener
     */
    private fun setupEditButton(editButton: Button) {

        // listen for currentUserChange to hide or show button accordingly
        connectedUserViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != user) {
                editButton.visibility = View.GONE
            }
        }

        editButton.setOnClickListener {
            toggleVisibleElement(View.GONE, View.VISIBLE)

            setUpRelativeLayoutAttribute(R.id.fragment_profile_details_save_profile)
        }
    }

    /**
     * Helper function to setup the save button
     */
    private fun setupSaveButton(saveButton: Button) {
        connectedUserViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != user) {
                saveButton.visibility = View.GONE
            }
        }

        saveButton.setOnClickListener {
            toggleVisibleElement(View.VISIBLE, View.GONE)
            setUpRelativeLayoutAttribute(R.id.fragment_profile_details_edit_profile)

        }
    }

    /**
     * Helper function to toggle all element view depending on whether we are in edit mode,
     * or if we are on "display" mode.
     *
     * @param editVisibility The visibility to pass to object that should be visible in edit mode
     * @param saveVisibility The visibility to pass to object that should be visible in display mode
     */
    private fun toggleVisibleElement(editVisibility: Int, saveVisibility: Int) {
        EDIT_RELATED_VIEW.forEach {
            requireView().findViewById<View>(it).visibility = editVisibility
        }
        SAVE_RELATED_VIEW.forEach {
            requireView().findViewById<View>(it).visibility = saveVisibility
        }
    }

    /**
     * Helper function to register an activity to launch the camera to take a picture
     */
     fun registerTakePictureActivity(): ActivityResultLauncher<Void?> {
        return registerForActivityResult(ActivityResultContracts.TakePicturePreview(),
            testActivityRegistry ?: requireActivity().activityResultRegistry)
        { photo ->
            val photoMetadata: PhotoMetadata =
                PhotoMetadata(takenBy = user, takenOn = ParcelableDate(Date()))
            user?.apply {
                photo?.let {
                    // set the taken picture to the current user profile picture
                    connectedUserViewModel.currentUserProfilePicture.value = it

                    // store the profile picture in the database
                    lifecycleScope.launch {
                        FirebaseDB.storeUserProfilePicture(it, id, photoMetadata)
                    }
                }
            }
        }
    }

    /**
     * Helper function to register an activity to request the camera permission
     */
    private fun registerRequestCameraPermission(
        takePicture: ActivityResultLauncher<Void?>,
        button: ImageButton): ActivityResultLauncher<String> {

        return registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // If permission is granted then directly launch the camera
            if (it) {
                button.setOnClickListener {
                    takePicture.launch()
                }
            } else {
                //TODO Handle this case better in the future, by using a popup for example
                // to explain to the user that he won't be able to take picture if he
                // doesn't accept
                Log.d(
                    "Camera permission not granted",
                    "The user did not grant camera permission"
                )
            }
        }
    }

    /**
     * Helper function to setup the take picture button listener
     *
     * Check if we have the permission, if yes take picture, if not ask them to the user
     */
    private fun setupTakePictureButton(button: ImageButton) {
        val takePicture = registerTakePictureActivity()
        val requestPermissionLauncher = registerRequestCameraPermission(takePicture, button)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
            button.setOnClickListener {
                takePicture.launch()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Helper function to setup the relative layout attribute of the
     * name text view
     *
     * @param startOf the index of the element we want to set as the START_OF element relative
     * to the text view
     */
    private fun setUpRelativeLayoutAttribute(startOf: Int) {
        val tView = requireView()
            .findViewById<TextView>(R.id.fragment_profile_details_profile_name)
        tView.layoutParams = RelativeLayout.LayoutParams(tView?.layoutParams).apply {
            addRule(RelativeLayout.START_OF, startOf)
            addRule(RelativeLayout.CENTER_IN_PARENT)
            addRule(RelativeLayout.ALIGN_PARENT_START)
        }
    }

    private fun setupFollowListener(followButton: Button) {
        followButton.setOnClickListener {
            if (followButton.text == Actions.FOLLOW.text) {
                followButton.text = Actions.UNFOLLOW.text
                lifecycleScope.launch {
                    //TODO : add currentUser
                    user?.addFollower(User.currentUser)
                }
            } else {
                followButton.text = Actions.FOLLOW.text
                lifecycleScope.launch {
                    //user?.removeFollower(User.currentUser)
                }
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user user to show details.
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_USER = "USER"
        private const val PROFILE_PICTURE_DIM = 400
        private val EDIT_RELATED_VIEW= listOf(
            R.id.fragment_profile_details_edit_profile,
            R.id.fragment_profile_details_profile_image
        )
        private val SAVE_RELATED_VIEW= listOf(
            R.id.fragment_profile_details_save_profile,
            R.id.fragment_profile_details_take_picture
        )

        @JvmStatic
        fun newInstance(user: User) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}