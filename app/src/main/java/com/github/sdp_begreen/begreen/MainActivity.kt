package com.github.sdp_begreen.begreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.mainButton)
        btn.setOnClickListener {
            val textVal: TextView = findViewById(R.id.mainName)
            val intent = Intent(this, GreetingActivity::class.java)
            intent.putExtra("name", textVal.text.toString())
            startActivity(intent)
        }

        val btnDB: Button = findViewById(R.id.buttonDB)
        btnDB.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            startActivity(intent)
        }

    }
}