package com.github.sdp_begreen.begreen

import androidx.room.Room
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdp_begreen.begreen.room.Person
import com.github.sdp_begreen.begreen.room.PersonDao
import com.github.sdp_begreen.begreen.room.PersonDatabase
import com.github.sdp_begreen.begreen.room.RoomSkeletonActivity
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class RoomTests {
    private lateinit var personDao: PersonDao
    private lateinit var db: PersonDatabase
    @get:Rule
    val activityRule = ActivityScenarioRule(RoomSkeletonActivity::class.java)

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        db = Room.inMemoryDatabaseBuilder(
            context, PersonDatabase::class.java).build()
        personDao = db.personDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val person: Person = Person(1, "Xavier", "Croy")
        personDao.insertPerson(person)
        val persList = personDao.getPersonList()
        assertThat(persList, equalTo(listOf(person)))
    }
}
