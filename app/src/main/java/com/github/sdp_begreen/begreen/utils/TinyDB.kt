package com.github.sdp_begreen.begreen.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.util.*

// This class is a utility to simplify the use of SharedPreferences for Android.
class TinyDB(appContext: Context) {

    /// Instance of SharedPreferences
    private val preferences: SharedPreferences

    // Initialization of the SharedPreferences object
    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    fun getString(key: String?): String? {
        return preferences.getString(key, "")
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    fun getListString(key: String?): ArrayList<String> {
        val splitStringArray = TextUtils.split(preferences.getString(key, ""), "‚‗‚")
        return arrayListOf(*splitStringArray)
    }

    /**
     * Get parsed ArrayList of Objects from SharedPreferences at 'key'
     * @param key SharedPreferences key
     * @param mClass a class object
     * @return ArrayList of String
     */
    fun <K> getListObject(key: String?, mClass: Class<K>): List<K> {
        val gson = Gson()
        val objStrings = getListString(key)
        val objects = ArrayList<K>()

        for (jObjString in objStrings) {
            val jsonElement = JsonParser.parseString(jObjString)

            if (jsonElement.isJsonObject) {
                val value = gson.fromJson(jsonElement, mClass)
                objects.add(value)
            } else if (jsonElement.isJsonArray) {
                val jsonArray = jsonElement.asJsonArray
                for (jsonObject in jsonArray) {
                    val value = gson.fromJson(jsonObject, mClass)
                    objects.add(value)
                }
            }
        }
        return objects
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     * @param key SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    fun putListString(key: String, stringList: ArrayList<String>) {
        preferences.edit().putString(key, stringList.joinToString { "‚‗‚" }).apply()
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     * @param key SharedPreferences key
     * @param objArray ArrayList of Any to be added
     */
    fun <K> putListObject(key: String, objArray: List<K>) {
        val gson = Gson()
        putListString(key, ArrayList(objArray.map { gson.toJson(it) }))
    }

    /**
     * Remove SharedPreferences item with 'key'
     * @param key SharedPreferences key
     */
    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    /**
     * Retrieve all values from SharedPreferences. Do not modify collection return by method
     * @return a Map representing a list of key/value pairs from SharedPreferences
     */
    val all: Map<String, *>
        get() = preferences.all
}