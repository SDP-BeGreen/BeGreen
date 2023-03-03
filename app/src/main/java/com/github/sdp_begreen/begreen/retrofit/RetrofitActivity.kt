package com.github.sdp_begreen.begreen.retrofit


//import android.app.ProgressDialog
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.github.sdp_begreen.begreen.R
//import com.github.sdp_begreen.begreen.retrofit.RetrofitClientInstance.retrofitInstance
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//
////https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
//
//class RetrofitActivity : AppCompatActivity() {
//    private var adapter: CustomAdapter? = null
//    private var recyclerView: RecyclerView? = null
//    var progressDoalog: ProgressDialog? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_retrofit)
//        progressDoalog = ProgressDialog(this@RetrofitActivity)
//        progressDoalog!!.setMessage("Loading....")
//        progressDoalog!!.show()
//
//        /*Create handle for the RetrofitInstance interface*/
//        val service = retrofitInstance!!.create(
//            GetDataService::class.java
//        )
//        val call: Call<List<RetroPhoto?>?>? = service.allPhotos
//        call?.enqueue(object : Callback<List<RetroPhoto?>?> {
//            override fun onResponse(
//                call: Call<List<RetroPhoto?>?>?,
//                response: Response<List<RetroPhoto?>?>
//            ) {
//                progressDoalog!!.dismiss()
//                generateDataList(response.body() as List<RetroPhoto>)
//            }
//
//            override fun onFailure(call: Call<List<RetroPhoto?>?>?, t: Throwable?) {
//                progressDoalog!!.dismiss()
//                Toast.makeText(
//                    this@RetrofitActivity,
//                    "Something went wrong...Please try later!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
//    }
//
//    /*Method to generate List of data using RecyclerView with custom adapter*/
//    private fun generateDataList(photoList: List<RetroPhoto>) {
//        recyclerView = findViewById<RecyclerView>(R.id.customRecyclerView)
//        adapter = CustomAdapter(this, photoList)
//        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@RetrofitActivity)
//        recyclerView.setLayoutManager(layoutManager)
//        recyclerView.setAdapter(adapter)
//    }
//}