package com.github.sdp_begreen.begreen.activities

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.sdp_begreen.begreen.R


class AddNewPostActivity : AppCompatActivity() {

    private lateinit var addNewPostBtn : Button

    companion object {
        const val PERMISSION_CAMERA_REQUEST_CODE = 100
        const val REQUEST_IMAGE_CAPTURE = 1
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
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // When we receive the photo from the camera, we start a new activity to share it
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            // Get the image from the camera activity
            val image = data.extras?.get("data") as? Bitmap

            if (image != null) {

                // Start the SharePost activity with the taken image
                startSharePostActivity(image)
            }
        }
    }
}
