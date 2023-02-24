package com.github.sdp_begreen.begreen

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        val tView: TextView = findViewById(R.id.grettingText)
        val name = intent.getStringExtra("name")

        tView.text = getString(R.string.greetingValue, name)
    }
}