package com.github.sdp_begreen.begreen.fragments

import android.graphics.Bitmap
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
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashCategory
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.viewModels.ConnectedUserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Date

//argument constant
private const val ARG_URI = "uri"

class SendPostFragment : Fragment() {
    private var param_uri: String? = null
    private val db by inject<DB>()
    private val connectedUserViewModel: ConnectedUserViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param_uri = it.getString(ARG_URI)
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
            //create a metadata file
            var metadata : TrashPhotoMetadata?
            //fetch description on UI
            view?.findViewById<TextInputEditText>(R.id.post_description).also {

                val caption = it?.text.toString()

                // TODO : Let the user choose the category like what we did in googlemap for the bin category
                var category = TrashCategory.ORGANIC

                /*
                //fetch category on UI
                view?.findViewById<TextInputEditText>(R.id.post_category)?.also { cat ->
                    category = cat.text.toString()
                }*/

                // fetch current user
                val user = connectedUserViewModel.currentUser.value

                metadata = TrashPhotoMetadata(null, ParcelableDate.now, user?.id, caption, category)

            }

            // Post photo to firebase
            view?.findViewById<ImageView>(R.id.preview)?.drawable?.toBitmap()?.let { bitmap ->
                lifecycleScope.launch {

                    if (metadata != null) {
                        sharePhoto(bitmap, metadata!!)
                    }
                }
            }

            returnToCamera()
        }
    }

    /**
     * Helper function to share a post with the database
     */
    private suspend fun sharePhoto(image: Bitmap, photoMetadata: TrashPhotoMetadata) {

        lifecycleScope.launch {

            // If the photo has correctly been shared, the FirebaseDB returned a non null metadata.
            val hasBeenShared = (FirebaseDB.addImage(image, photoMetadata) != null)

            if (hasBeenShared) {

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
        fun newInstance(param1: String) =
            SendPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, param1)
                }
            }
    }
}