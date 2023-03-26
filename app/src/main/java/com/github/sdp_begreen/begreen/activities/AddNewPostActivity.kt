package com.github.sdp_begreen.begreen.activities

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.sdp_begreen.begreen.R


class AddNewPostActivity : AppCompatActivity() {

    private lateinit var addNewPostBtn : Button
    private lateinit var cameraActivityLauncher : ActivityResultLauncher<Intent>

    companion object {
        const val PERMISSION_CAMERA_REQUEST_CODE = 100
        const val EXTRA_IMAGE_BITMAP = "image_bitmap"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_post)

        setupAddNewPostBtn()
    }

    /**
     * Helper function to setup the behavior of the "Add new post" button
     */
    private fun setupAddNewPostBtn() {

        // If the user clicks on the "Add new post" button it will ask him to take a picture
        addNewPostBtn = findViewById(R.id.addNewPostBtn)
        addNewPostBtn.setOnClickListener {
            startCameraIntent()
        }

        // Set up the camera Activity result launcher
        cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onCameraActivityResult(result)
        }
    }

    /**
     * Helper function to start the camera intent, or ask for permission if not granted.
     */
    private fun startCameraIntent() {

        // If the camera permission is not granted, ask for it. Otherwise start the camera intent.
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_CAMERA_REQUEST_CODE)

        } else {

            // Start the camera intent. Actually, it handles the permission checks by its own so the previous
            // verification is not mandatory. But keep it if android decides to change the behavior of the camera permission.
            cameraActivityLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }

    /**
     * Helper function to start the SharePost activity
     *
     * @param image The photo to share
     */
    private fun startSharePostActivity(image: Bitmap) {

        // Send the image to the SharePostActivity
        val intent = Intent(this, SharePostActivity::class.java)
        intent.putExtra(EXTRA_IMAGE_BITMAP, image)
        startActivity(intent)
    }

    /**
     * Callback function that will be executed once the Camera Activity will return a result after being launched.
     * While this method could be private, we preferred to remove the private access control so it can be tested.
     *
     * @param result The result sent by the camera activity
     */
    fun onCameraActivityResult(result : ActivityResult) {

        // When we receive the photo from the camera, we start a new activity to share it
        if (result.resultCode == RESULT_OK && result.data != null) {

            val image : Bitmap?

            // Get the image from the camera activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                image = result.data!!.extras?.getParcelable("data", Bitmap::class.java)

            } else {
                @Suppress("DEPRECATION")
                image = result.data!!.extras?.get("data") as? Bitmap
            }


            if (image != null) {

                // Start the SharePost activity with the taken image
                startSharePostActivity(image)
            }
        }
    }
}
