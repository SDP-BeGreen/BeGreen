package com.github.sdp_begreen.begreen.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.sdp_begreen.begreen.Database.Companion.db
import com.github.sdp_begreen.begreen.R

class DatabaseActivity : AppCompatActivity() {

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
            db[phoneText.text.toString()].thenAccept {
                emailText.setText(it)
            }
        }

    }
}