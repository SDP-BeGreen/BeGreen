package com.github.sdp_begreen.begreen.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.SharePostActivity

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {

    private lateinit var addNewPostBtn : Button
    private lateinit var cameraActivityLauncher : ActivityResultLauncher<Void?>

    companion object {
        const val PERMISSION_CAMERA_REQUEST_CODE = 100
        const val EXTRA_IMAGE_BITMAP = "image_bitmap"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance.
         */
        @JvmStatic
        fun newInstance() = CameraFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddNewPostBtn()
    }

    /**
     * Helper function to setup the behavior of the "Add new post" button
     */
    private fun setupAddNewPostBtn() {

        // If the user clicks on the "Add new post" button it will ask him to take a picture
        addNewPostBtn = requireView().findViewById(R.id.addNewPostBtn)
        addNewPostBtn.setOnClickListener {
            startCameraIntent()
        }

        // Set up the camera Activity result launcher
        cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            onCameraActivityResult(bitmap)
        }
    }

    /**
     * Helper function to start the camera intent, or ask for permission if not granted.
     */
    private fun startCameraIntent() {

        // If the camera permission is not granted, ask for it. Otherwise start the camera intent.
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),
                CameraFragment.PERMISSION_CAMERA_REQUEST_CODE
            )

        } else {

            // Start the camera intent. Actually, it handles the permission checks by its own so the previous
            // verification is not mandatory. But keep it if android decides to change the behavior of the camera permission.
            cameraActivityLauncher.launch()
        }
    }

    /**
     * Helper function to start the SharePost activity
     *
     * @param image The photo to share
     */
    private fun startSharePostActivity(image: Bitmap) {

        // Send the image to the SharePostActivity
        val intent = Intent(requireContext(), SharePostActivity::class.java)
        intent.putExtra(CameraFragment.EXTRA_IMAGE_BITMAP, image)
        startActivity(intent)
    }

    /**
     * Callback function that will be executed once the Camera Activity will return a result after being launched.
     * While this method could be private, we preferred to remove the private access control so it can be tested.
     *
     * @param result The result sent by the camera activity
     */
    private fun onCameraActivityResult(bitmap: Bitmap?) {

        // When we receive the photo from the camera, we start a new activity to share it.
        // bitmap is null if the picture was cancelled (the user has clicked on the "< back" button of the camera to quit)
        if (bitmap != null) {

            // Start the SharePost activity with the taken image
            startSharePostActivity(bitmap)
        }
    }
}