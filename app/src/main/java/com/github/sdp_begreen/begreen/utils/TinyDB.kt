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
    fun getString(key: TinyDBKey): String? {
        return preferences.getString(key.key, "")
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    fun getListString(key: TinyDBKey): List<String> {
        return listOf(*TextUtils.split(preferences.getString(key.key, ""), "‚‗‚"))
    }

    /**
     * Get parsed ArrayList of Objects from SharedPreferences at 'key'
     * @param key SharedPreferences key
     * @param mClass a class object
     * @return ArrayList of String
     */
    fun <K> getListObject(key: TinyDBKey, mClass: Class<K>): List<K> {
        val gson = Gson()
        val objStrings = getListString(key)
        val objects = ArrayList<K>()

        for (jObjString in objStrings) {
            val jsonElement = JsonParser.parseString(jObjString)
            if (jsonElement.isJsonObject) {
                objects.add(gson.fromJson(jsonElement, mClass))
            } else if (jsonElement.isJsonArray) {
                val jsonArray = jsonElement.asJsonArray
                for (jsonObject in jsonArray) {
                    objects.add(gson.fromJson(jsonObject, mClass))
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
    fun putListString(key: TinyDBKey, stringList: List<String>) {
        preferences.edit().putString(key.key, stringList.joinToString { "‚‗‚" }).apply()
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     * @param key SharedPreferences key
     * @param objArray ArrayList of Any to be added
     */
    fun <K> putListObject(key: TinyDBKey, objArray: List<K>) {
        val gson = Gson()
        putListString(key, objArray.map { gson.toJson(it) })
    }

    /**
     * Remove SharedPreferences item with 'key'
     * @param key SharedPreferences key
     */
    fun remove(key: TinyDBKey) {
        preferences.edit().remove(key.key).apply()
    }

    /**
     * Retrieve all values from SharedPreferences. Do not modify collection return by method
     * @return a Map representing a list of key/value pairs from SharedPreferences
     */
    val all: Map<String, *>
        get() = preferences.all
}