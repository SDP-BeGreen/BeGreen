package com.github.sdp_begreen.begreen

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.sdp_begreen.begreen.Database.Companion.db

class DatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
        val emailText = findViewById<EditText>(R.id.emailDB)
        val phoneText = findViewById<EditText>(R.id.phoneDB)

        // Set function
        val setBtn: Button = findViewById(R.id.setButton)
        setBtn.setOnClickListener {
            db[phoneText.text.toString()] = emailText.text.toString()
        }

        // Get function
        val getBtn: Button = findViewById(R.id.getButton)
        getBtn.setOnClickListener {
            db[phoneText.text.toString()].thenAccept {
                emailText.setText(it)
            }
        }

    }
}