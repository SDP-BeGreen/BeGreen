package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

//argument constant
private const val ARG_URI = "uri"
private const val ARG_TEST = "test"

class SendPostFragment : Fragment() {
    private var param_uri: String? = null
    private var param_test: Boolean = false
    private val db by inject<DB>()
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param_uri = it.getString(ARG_URI)
            param_test = it.getBoolean(ARG_TEST)
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
        initView()
    }

    private fun initView(){
        //load image
        Picasso.Builder(requireContext()).build().load(param_uri).into(view?.findViewById(R.id.preview))
        setUpCancel()
        setUpShare()
    }

    private fun setUpCancel(){
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

    private fun setUpShare(){
        val shareBtn = view?.findViewById<ImageView>(R.id.send_post)
        shareBtn?.setOnClickListener {

            // fetch current user. He is necessarily not null
            connectedUserViewModel.currentUser.value?.let{user ->

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

                    metadata = TrashPhotoMetadata(null, ParcelableDate.now, user.id, caption, category)

                }

                // Update the user and return
                lifecycleScope.launch {
                    updateUser(metadata, user)
                    if (!param_test) returnToCamera()
                }
            }
        }
    }

    /**
     * Helper function to update the user after posting a photo
     */
    private suspend fun updateUser(metadata: TrashPhotoMetadata?, user: User) {
        view?.findViewById<ImageView>(R.id.preview)?.drawable?.toBitmap()?.let { bitmap ->

            // Get the stored metadata
            val storedMetadata = metadata?.let {

                //sharePhoto(bitmap, it)
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
                Toast.makeText(requireContext(), R.string.photo_shared_success, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Companion object to create fragment
     * with arguments
     */
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Boolean = false) =
            SendPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, param1)
                    putBoolean(ARG_TEST, param2)
                }
            }
    }
}