package com.github.sdp_begreen.begreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.mainButton)
        btn.setOnClickListener {
            val textVal: TextView = findViewById(R.id.mainName);
            val intent = Intent(this, GreetingActivity::class.java)
            intent.putExtra("name", textVal.text.toString())
            startActivity(intent)
        }

        val fragmentBtn: Button = findViewById(R.id.fragmentTest)
        fragmentBtn.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
        }
    }
}