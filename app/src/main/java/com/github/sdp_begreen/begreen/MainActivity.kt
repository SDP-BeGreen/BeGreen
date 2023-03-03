package com.github.sdp_begreen.begreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import retrofit2.*
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

        //------------Retrofit Bootcamp--------------------------
        //baseUrl of the API and the addConverterFactory will convert the response
        //to a BoredActivity Object
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //instance of boredApi
        val boredApi = retrofit.create(BoredApi::class.java)





        val btnConnect : Button = findViewById(R.id.create_api)
        val btnSen : Button = findViewById(R.id.send_request)
        val textResponse : TextView = findViewById(R.id.text_response)
        btnSen.setOnClickListener {
            boredApi.getActivity().enqueue(
                object : Callback<BoredActivity>{
                    override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>
                    ) {
                        if(response.isSuccessful){
                            textResponse.text = response.body()?.activity
                        }else{
                            textResponse.text = response.errorBody().toString()
                        }

                    }

                    override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                        textResponse.text = t.message.toString()
                    }
                }
            )
        }


        btnConnect.setOnClickListener{

        }
    }
}