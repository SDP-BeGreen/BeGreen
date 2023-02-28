package com.github.sdp_begreen.begreen.okhttp

import okhttp3.MediaType.Companion.toMediaType

//https://square.github.io/okhttp/
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


//https://square.github.io/okhttp/
class postToServer {
    val client = OkHttpClient()
    @Throws(IOException::class)
    fun post(url: String, json: String): String {
        val body: RequestBody = RequestBody.create(JSON, json)

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }

    fun bowlingJson(player1: String, player2: String): String {
        return ("{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}")
    }

    companion object {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val example = postToServer()
            val json = example.bowlingJson("Jesse", "Jake")
            val response = example.post("http://www.roundsapp.com/post", json).also {
                println(it)
            }
        }
    }
}