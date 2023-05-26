package com.github.sdp_begreen.begreen.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TinyDBTest {
    private lateinit var tinyDB: TinyDB

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        Mockito.`when`(mockContext.applicationContext).thenReturn(mockContext)
        Mockito.`when`(PreferenceManager.getDefaultSharedPreferences(mockContext)).thenReturn(mockSharedPreferences)

        tinyDB = TinyDB(mockContext)
    }

    @After
    fun tearDown() {
        Mockito.reset(mockSharedPreferences)
    }

    @Test
    fun testGetString() {
        val key = "test_key"
        val defaultValue = "default_value"
        val expectedValue = "test_value"

        Mockito.`when`(mockSharedPreferences.getString(key, defaultValue)).thenReturn(expectedValue)

        val value = tinyDB.getString(key)

        assertEquals(expectedValue, value)
    }

    @Test
    fun testGetListString() {
        val key = "test_key"
        val defaultValue = ""
        val expectedList = arrayListOf("value1", "value2")

        Mockito.`when`(mockSharedPreferences.getString(key, defaultValue)).thenReturn("value1‚‗‚value2")

        val list = tinyDB.getListString(key)

        assertEquals(expectedList, list)
    }

    @Test
    fun testGetListObject() {
        val key = "test_key"
        val defaultValue = ""
        val gson = Gson()
        val expectedObject1 = TestObject("value1")
        val expectedObject2 = TestObject("value2")
        val expectedList = arrayListOf(expectedObject1, expectedObject2)

        Mockito.`when`(mockSharedPreferences.getString(key, defaultValue)).thenReturn(gson.toJson(expectedList))

        val list = tinyDB.getListObject(key, TestObject::class.java)

        assertEquals(expectedList, list)
    }


    @Test
    fun testPutListString() {
        val key = "test_key"
        val stringList = arrayListOf("value1", "value2")

        val mockEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.apply()).thenReturn(Unit)
        Mockito.`when`(mockSharedPreferences.edit()).thenReturn(mockEditor)

        tinyDB.putListString(key, stringList)

        Mockito.verify(mockSharedPreferences).edit()
        Mockito.verify(mockSharedPreferences.edit()).putString(key, "value1‚‗‚value2")
        Mockito.verify(mockSharedPreferences.edit()).apply()
    }

    @Test
    fun testPutListObject() {
        val key = "key"
        val objList = ArrayList<Any>().apply {
            add("value1")
            add("value2")
        }

        val mockEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.apply()).thenReturn(Unit)
        Mockito.`when`(mockSharedPreferences.edit()).thenReturn(mockEditor)

        val mockSharedPreferences = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(mockSharedPreferences.edit()).thenReturn(mockEditor)

        val mockContext = Mockito.mock(Context::class.java)
        Mockito.`when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)

        val tinyDB = TinyDB(mockContext)

        tinyDB.putListObject(key, objList)

        // Verify that putString method is called for each object in the list
        for (i in objList.indices) {
            Mockito.verify(mockEditor).putString("$key$i", objList[i].toString())
        }

        // Verify that apply method is called once
        Mockito.verify(mockEditor).apply()
    }

    @Test
    fun testRemove() {
        val key = "test_key"

        val mockEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.apply()).thenReturn(Unit)
        Mockito.`when`(mockSharedPreferences.edit()).thenReturn(mockEditor)

        tinyDB.remove(key)

        Mockito.verify(mockSharedPreferences).edit()
        Mockito.verify(mockSharedPreferences.edit()).remove(key)
        Mockito.verify(mockSharedPreferences.edit()).apply()
    }


    @Test
    fun testAll() {
        val expectedMap = HashMap<String, String>()
        expectedMap["key1"] = "value1"
        expectedMap["key2"] = "value2"

        Mockito.`when`(mockSharedPreferences.all).thenReturn(expectedMap as Map<String, *>)

        val all = tinyDB.all

        assertEquals(expectedMap, all)
    }

    private data class TestObject(val value: String)
}