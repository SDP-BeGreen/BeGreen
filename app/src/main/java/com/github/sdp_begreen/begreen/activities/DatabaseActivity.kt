package com.github.sdp_begreen.begreen.activities

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import kotlinx.coroutines.launch

/**
 * This file will be deleted when Firebase realtime database gets merged in the project
 */
class DatabaseActivity : AppCompatActivity() {

    private var imageId: PhotoMetadata? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
        val emailText = findViewById<EditText>(R.id.databaseEmail)
        val phoneText = findViewById<EditText>(R.id.databasePhoneNumber)

        // Set button
        findViewById<Button>(R.id.databaseSet).setOnClickListener {
            FirebaseDB[phoneText.text.toString()] = emailText.text.toString()
        }

        // Get button
        findViewById<Button>(R.id.databaseGet).setOnClickListener {
            lifecycleScope.launch {
                FirebaseDB.get(phoneText.text.toString())?.also { emailText.setText(it) }
            }
        }

        // Store image button
        findViewById<Button>(R.id.databaseStorePicture).setOnClickListener {

            // Example image (red square)
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.RED)

            lifecycleScope.launch {
                imageId = FirebaseDB.addImage(bitmap, 3,
                    PhotoMetadata(null, null, null, null,"Plastic bottle",null))
            }
        }

        // Load image button
        findViewById<Button>(R.id.databaseLoadPicture).setOnClickListener {
            lifecycleScope.launch {
                imageId?.also {
                    FirebaseDB.getImage(it, 3).also { bitmap ->
                        findViewById<ImageView>(R.id.databasePicture).setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}