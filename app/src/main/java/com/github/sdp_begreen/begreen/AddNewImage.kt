package com.example.mapwithmarker

import android.Manifest
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AddNewImage : AppCompatActivity() {

    private lateinit var addNewImageBtn : Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_image)

        // Request for camera runtime permission
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), 100)
        }

        // If the user clicks on the share button it will ask him to take a picture
        addNewImageBtn = findViewById(R.id.addNewImageBtn);
        addNewImageBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    // Uncomment this if we want to open the camera right after the user has accepted the permissions
    /*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // When we receive the photo from the camera, we start a new activity to share the photo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            // Get the image from the camera acivity
            val image = data.extras?.get("data") as Bitmap

            // Send the image to the share activity
            val intent = Intent(this, ShareImage::class.java)
            intent.putExtra("image", image)
            startActivity(intent)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}