package com.github.sdp_begreen.begreen

import retrofit2.Call
import retrofit2.http.GET

interface BoredApi {
    //We specify its request type (activity is the endpoint of the API)
    @GET("activity")
    //Return a Call object which represents a Retrofit HTTP request.
    //This object will be used to make the request.
    fun getActivity(): Call<BoredActivity>
}