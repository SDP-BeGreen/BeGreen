package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.*
import com.github.sdp_begreen.begreen.utils.BitmapsUtils
import com.github.sdp_begreen.begreen.utils.Permissions.hasPermissions
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.github.sdp_begreen.begreen.viewModels.ProfileEditedValuesViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileDetailsFragment(private val testActivityRegistry: ActivityResultRegistry? = null) :
    Fragment() {
    private var user: User? = null
    private var recentPosts: List<TrashPhotoMetadata>? = null

    private val connectedUserViewModel:
            ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })
    private val profileEditedValuesViewModel by viewModels<ProfileEditedValuesViewModel>()
    private val db by inject<DB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                user = it.getParcelable(ARG_USER, User::class.java)
                recentPosts =
                    it.getParcelableArrayList(ARG_RECENT_POSTS, TrashPhotoMetadata::class.java)
            } else {
                user = it.getParcelable(ARG_USER)
                recentPosts = it.getParcelableArrayList(ARG_RECENT_POSTS)
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
        val userProgressBar: ProgressBar =
            view.findViewById(R.id.fragment_profile_details_user_progress)
        val followButton: Button = view.findViewById(R.id.fragment_profile_details_follow_button)
        val editButton: Button = view.findViewById(R.id.fragment_profile_details_edit_profile)
        val saveButton: Button = view.findViewById(R.id.fragment_profile_details_save_profile)
        val cancelButton: Button =
            view.findViewById(R.id.fragment_profile_details_cancel_modification)
        val takePictureButton: ImageButton =
            view.findViewById(R.id.fragment_profile_details_take_picture)
        rating.rating = user?.score?.toFloat() ?: 0.0f
        userProgressBar.progress = user?.progression ?: 0

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragment_recent_profile_photo,
                UserPhotoFragment.newInstance(1, recentPosts, false),
                ""
            )
            ?.commit()

        setUpUserInfo(view)
        setupUserFieldViewUponCreation(view)
        setUpUserProfilePicture(view)
        setupFollowListener(followButton)
        setupEditButton(editButton)
        setupSaveButton(saveButton)
        setupCancelButton(cancelButton)
        setupTakePictureButton(takePictureButton)
        return view
    }

    /**
     * Helper function to correctly setup the user related fields (i.e. set visibility) inside this
     * view upon view creation.
     *
     * Determine which component to display based on whether we are displaying the profile
     * of the currently connected user, and whether we were currently editing or not
     *
     * @param view The view where to find those components
     */
    private fun setupUserFieldViewUponCreation(view: View) {
        if (
            connectedUserViewModel.currentUser.value?.id == user?.id &&
            profileEditedValuesViewModel.isCurrentlyEditing()
        ) {
            toggleVisibleElement(View.GONE, View.VISIBLE, view)
        } else {
            toggleVisibleElement(View.VISIBLE, View.GONE, view)
        }
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
        val descriptionEdit: EditText =
            view.findViewById(R.id.fragment_profile_details_profile_description_edit)
        val name: TextView = view.findViewById(R.id.fragment_profile_details_profile_name)
        val nameEdit: EditText = view.findViewById(R.id.fragment_profile_details_profile_name_edit)
        val profilePhone: TextView = view.findViewById(R.id.fragment_profile_details_profile_phone)
        val phoneEdit: EditText =
            view.findViewById(R.id.fragment_profile_details_profile_phone_edit)
        val profileEmail: TextView = view.findViewById(R.id.fragment_profile_details_profile_email)
        val emailEdit: EditText =
            view.findViewById(R.id.fragment_profile_details_profile_email_edit)
        val userTextLevel: TextView = view.findViewById(R.id.fragment_profile_details_level)
        setupEditableUserInfoListener(nameEdit, phoneEdit, emailEdit, descriptionEdit)

        lifecycleScope.launch {
            connectedUserViewModel.currentUser
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { cUser ->
                    val userToUse = cUser?.let { if (it.id == user?.id) it else user } ?: user
                    profileDescription.text =
                        userToUse?.description ?: getString(R.string.nav_drawer_user_description)
                    descriptionEdit.setText(
                        profileEditedValuesViewModel.description ?: userToUse?.description
                        ?: getString(R.string.nav_drawer_user_description)
                    )
                    name.text = userToUse?.displayName ?: getString(R.string.nav_drawer_username)
                    nameEdit.setText(
                        profileEditedValuesViewModel.displayName ?: // priority to edited name
                        userToUse?.displayName
                        ?: getString(R.string.fragment_profile_details_username_placeholder)
                    )
                    profilePhone.text = userToUse?.phone
                    phoneEdit.setText(profileEditedValuesViewModel.phone ?: userToUse?.phone)
                    profileEmail.text = userToUse?.email
                    emailEdit.setText(profileEditedValuesViewModel.email ?: userToUse?.email)
                    userTextLevel.text = getString(
                        R.string.user_details_level_text, userToUse?.displayName ?: "Default User",
                    )
                }
        }
    }

    /**
     * Helper function to setup all the editable text listener, to persist their changes
     * into the viewModel
     *
     * @param nameEdit The editText that contains the display name
     * @param phoneEdit The editText that contains the phone number
     * @param emailEdit The editText that contains de email
     * @param descriptionEdit The editText that contains the description
     */
    private fun setupEditableUserInfoListener(
        nameEdit: EditText, phoneEdit: EditText, emailEdit: EditText, descriptionEdit: EditText
    ) {
        nameEdit.addTextChangedListener {
            profileEditedValuesViewModel.displayName = it.toString()
        }
        phoneEdit.addTextChangedListener {
            profileEditedValuesViewModel.phone = it.toString()
        }
        emailEdit.addTextChangedListener {
            profileEditedValuesViewModel.email = it.toString()
        }
        descriptionEdit.addTextChangedListener {
            profileEditedValuesViewModel.description = it.toString()
        }
    }

    /**
     * Helper function to setup the observer on the current profile picture to display
     *
     * @param view The view in which to look for visual element
     */
    private fun setUpUserProfilePicture(view: View) {
        val profileImgView: ImageView =
            view.findViewById(R.id.fragment_profile_details_profile_image)
        lifecycleScope.launch {
            connectedUserViewModel.currentUserProfilePicture
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (connectedUserViewModel.currentUser.value?.id == user?.id) {
                        val img = it ?: BitmapFactory.decodeResource(
                            resources,
                            R.drawable.blank_profile_picture
                        )
                        profileImgView.setImageBitmap(
                            BitmapsUtils.rescaleImage(
                                img,
                                PROFILE_PICTURE_DIM,
                                PROFILE_PICTURE_DIM
                            )
                        )
                    } else {
                        val img = user?.let { user ->
                            user.profilePictureMetadata?.let { pMetadata ->
                                db.getUserProfilePicture(pMetadata, user.id)
                            }
                        } ?: BitmapFactory.decodeResource(
                            resources,
                            R.drawable.blank_profile_picture
                        )
                        profileImgView.setImageBitmap(
                            BitmapsUtils.rescaleImage(
                                img,
                                PROFILE_PICTURE_DIM,
                                PROFILE_PICTURE_DIM
                            )
                        )
                    }
                }
        }
    }

    /**
     * Helper function to setup the edit button
     *
     * Setup if visible or not
     * setup its listener
     *
     * @param editButton The button representing the edit button
     */
    private fun setupEditButton(editButton: Button) {

        setupButtonVisibility(editButton) {
            !profileEditedValuesViewModel.isCurrentlyEditing()
        }

        editButton.setOnClickListener {
            profileEditedValuesViewModel.startEditing()
            toggleVisibleElement(View.GONE, View.VISIBLE)
        }
    }

    /**
     * Helper function to setup the save button
     *
     * @param saveButton The button representing the save button
     */
    private fun setupSaveButton(saveButton: Button) {
        setupButtonVisibility(saveButton) {
            profileEditedValuesViewModel.isCurrentlyEditing()
        }

        saveButton.setOnClickListener {
            saveEditedField()
            profileEditedValuesViewModel.finishEditing()
            hideKeyboard()
            toggleVisibleElement(View.VISIBLE, View.GONE)
        }
    }

    /**
     * Helper function to setup the cancel button
     *
     * @param cancelButton The button representing the cancel button
     */
    private fun setupCancelButton(cancelButton: Button) {
        setupButtonVisibility(cancelButton) {
            profileEditedValuesViewModel.isCurrentlyEditing()
        }

        cancelButton.setOnClickListener {
            // finish editing without saving
            profileEditedValuesViewModel.finishEditing()
            hideKeyboard()

            toggleVisibleElement(View.VISIBLE, View.GONE)
            // reset edited values
            connectedUserViewModel.currentUser.value?.apply {
                requireView()
                    .findViewById<EditText>(R.id.fragment_profile_details_profile_description_edit)
                    .setText(
                        description ?: getString(R.string.nav_drawer_user_description)
                    )

                requireView()
                    .findViewById<EditText>(R.id.fragment_profile_details_profile_name_edit)
                    .setText(displayName ?: getString(R.string.nav_drawer_username))

                requireView()
                    .findViewById<EditText>(R.id.fragment_profile_details_profile_phone_edit)
                    .setText(phone)

                requireView()
                    .findViewById<EditText>(R.id.fragment_profile_details_profile_email_edit)
                    .setText(email)
            }
        }
    }

    /**
     * Helper function to setup the button visibility
     *
     * The visibility is determined based on if the currently logged in user is the user
     * for whom we are currently displaying the profile, and a function telling whether the button
     * should be visible or not
     *
     * @param button The button for which we want to setup the visibility
     * @param shouldBeVisible A function telling whether this button should be visible or not
     */
    private fun setupButtonVisibility(button: Button, shouldBeVisible: () -> Boolean) {
        lifecycleScope.launch {
            connectedUserViewModel.currentUser
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it?.id != user?.id || !shouldBeVisible()) {
                        button.visibility = View.GONE
                    } else if (shouldBeVisible()) {
                        button.visibility = View.VISIBLE
                    }
                }
        }
    }

    /**
     * Helper function to hide the soft keyboard
     */
    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /**
     * Helper function to save all the edited fields to the DB to persist changes,
     *
     * and also set the new values to the currentUser
     */
    private fun saveEditedField() {
        connectedUserViewModel.currentUser.value?.apply {
            // make a copy with edited value, or let old if unchanged (i.e. null)
            val newUser = copy(
                displayName = profileEditedValuesViewModel.displayName ?: displayName,
                phone = profileEditedValuesViewModel.phone ?: phone,
                email = profileEditedValuesViewModel.email ?: email,
                description = profileEditedValuesViewModel.description ?: description
            )

            // set the new user as the current user, keep current profile picture in the case
            // the user don't modify it, so that it's not set to null
            connectedUserViewModel.setCurrentUser(newUser, true)

            profileEditedValuesViewModel.profilePicture?.also {
                // set the taken picture to the current user profile picture
                connectedUserViewModel.setCurrentUserProfilePicture(it, id)
            }

            lifecycleScope.launch {
                val metadata = profileEditedValuesViewModel.profilePicture?.let {
                    db.storeUserProfilePicture(
                        it, id,
                        ProfilePhotoMetadata(takenBy = id, takenOn = ParcelableDate(Date()))
                    )
                }
                // new user with
                val newUserWithMetadata = newUser.copy(
                    profilePictureMetadata = metadata ?: profilePictureMetadata
                )
                // store the new User in firebase
                db.addUser(newUserWithMetadata, id)
                // once stored, set again the new user along with his metadata in current
                // user, for consistency
                connectedUserViewModel.setCurrentUser(newUser, true)
            }
        }
    }

    /**
     * Helper function to toggle all element view depending on whether we are in edit mode,
     * or if we are on "display" mode.
     *
     * @param editVisibility The visibility to pass to object that should be visible in edit mode
     * @param saveVisibility The visibility to pass to object that should be visible in display mode
     * @param view The view in which to look for those components
     */
    private fun toggleVisibleElement(
        editVisibility: Int,
        saveVisibility: Int,
        view: View = requireView()
    ) {
        DISPLAY_RELATED_VIEW.forEach {
            view.findViewById<View>(it).visibility = editVisibility
        }
        EDIT_RELATED_VIEW.forEach {
            view.findViewById<View>(it).visibility = saveVisibility
        }
    }

    /**
     * Helper function to register an activity to launch the camera to take a picture
     */
    private fun registerTakePictureActivity() =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
            testActivityRegistry ?: requireActivity().activityResultRegistry
        ) { profileEditedValuesViewModel.profilePicture = it }

    /**
     * Helper function to register an activity to request the camera permission
     *
     * @param takePicture The registered activity to launch to take a picture
     */
    private fun registerRequestCameraPermission(takePicture: ActivityResultLauncher<Void?>) =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // If permission is granted then directly launch the camera
            if (it) {
                takePicture.launch()
            } else {
                //TODO Handle this case better in the future, by using a popup for example
                // to explain to the user that he won't be able to take picture if he
                // doesn't accept
                Log.d(
                    "Camera permission not granted",
                    "The user did not grant camera permission"
                )
                // just display a toast for now
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.fragment_profile_details_camera_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    /**
     * Helper function to setup the take picture button listener
     *
     * Check if we have the permission, if yes take picture, if not ask them to the user
     *
     * @param button The image button for which to setup the listener
     */
    private fun setupTakePictureButton(button: ImageButton) {
        val takePicture = registerTakePictureActivity()
        val requestPermissionLauncher = registerRequestCameraPermission(takePicture)

        button.setOnClickListener {
            if (hasPermissions(requireContext(), Manifest.permission.CAMERA))
                takePicture.launch()
            else
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
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
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_USER = "USER"
        private const val PROFILE_PICTURE_DIM = 400

        // View that should be displayed when simply displaying user info
        private val DISPLAY_RELATED_VIEW = listOf(
            R.id.fragment_profile_details_edit_profile,
            R.id.fragment_profile_details_profile_image,
            R.id.fragment_profile_details_profile_name,
            R.id.fragment_profile_details_profile_email,
            R.id.fragment_profile_details_profile_phone,
            R.id.fragment_profile_details_profile_description
        )

        // View that should be displayed when editing
        private val EDIT_RELATED_VIEW = listOf(
            R.id.fragment_profile_details_save_profile,
            R.id.fragment_profile_details_cancel_modification,
            R.id.fragment_profile_details_take_picture,
            R.id.fragment_profile_details_profile_name_edit,
            R.id.fragment_profile_details_profile_email_edit,
            R.id.fragment_profile_details_profile_phone_edit,
            R.id.fragment_profile_details_profile_description_edit
        )
        private const val ARG_RECENT_POSTS = "recent_posts"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user user to show details.
         * @param photos recent posts of the user.
         * @return A new instance of fragment ProfileDetailsFragment.
         */
        @JvmStatic
        fun newInstance(user: User, photos: List<PhotoMetadata>) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                    putParcelableArrayList(ARG_RECENT_POSTS, photos.toCollection(ArrayList()))
                }
            }
    }
}