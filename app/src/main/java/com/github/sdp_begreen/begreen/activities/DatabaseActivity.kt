package com.github.sdp_begreen.begreen.activities

import android.graphics.*
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.FirebaseDB.Companion.db
import com.github.sdp_begreen.begreen.R
import kotlinx.coroutines.*

/**
 * This file will be deleted when Firebase realtime database gets merged in the project
 */
class DatabaseActivity : AppCompatActivity() {

    private var imageId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
        val emailText = findViewById<EditText>(R.id.databaseEmail)
        val phoneText = findViewById<EditText>(R.id.databasePhoneNumber)

        // Set function
        val setBtn: Button = findViewById(R.id.databaseSet)
        setBtn.setOnClickListener {
            db[phoneText.text.toString()] = emailText.text.toString()
        }

        // Get function
        val getBtn: Button = findViewById(R.id.databaseGet)
        getBtn.setOnClickListener {
            /*db[phoneText.text.toString()].thenAccept {
                if (it != null) emailText.setText(it)
            }*/
            lifecycleScope.launch {
                db.get(phoneText.text.toString())?.also { emailText.setText(it) }
            }
        }

        val storePictureBtn: Button = findViewById(R.id.databaseStorePicture)
        storePictureBtn.setOnClickListener {

            val size = 100 // size of the square in pixels
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.RED)

            imageId = db.addImage(bitmap, 3)

        }

        val getPictureBtn: Button = findViewById(R.id.databaseLoadPicture)
        getPictureBtn.setOnClickListener {

            /*imageId?.also {

                db.getImage(it, 3).thenAccept {bitmap ->
                    val image: ImageView = findViewById(R.id.databasePicture)
                    image.setImageBitmap(bitmap)
                }
            }*/
            lifecycleScope.launch {
                imageId?.also {
                    db.getImage(it, 3).also{bitmap ->
                        findViewById<ImageView>(R.id.databasePicture).setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}