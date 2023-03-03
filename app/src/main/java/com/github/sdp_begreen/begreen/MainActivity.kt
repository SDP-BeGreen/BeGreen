package com.github.sdp_begreen.begreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        //baseUrl of the API and the addConverterFactory will convert the response
        //to a BoredActivity Object
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}