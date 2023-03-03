package com.github.sdp_begreen.begreen

import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var activityDao: BoredActivityDao
    private lateinit var db: AncientActivityDB

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun createDb() {
        db = Room.databaseBuilder<AncientActivityDB>(
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
            AncientActivityDB::class.java, "database-name"
        ).build()
        activityDao = db.ActivityDao()
    }

    @Test
    fun nameWrittenCorrectly() {
        onView(withId(R.id.mainName))
            .perform(typeText("David"))
            .check(matches(withText("David")))
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeUserAndReadInLDB() {
        val act: BoredActivity = BoredActivity(1, "Jump in a volcano")
        activityDao.insertAll(act)
        val getedAct = activityDao.loadById(1)
        assertThat(getedAct, equalTo(act))
    }
    fun intentCorrectlyFiredWhenButtonPressed() {
        Intents.init()

        // Type the name
        onView(withId(R.id.mainName))
            .perform(typeText("David"))
            .perform(closeSoftKeyboard())

        // Perform the click on the button
        onView(withId(R.id.mainButton))
            .perform(click())

        // Assert correctness of values
        intended(allOf(
            hasExtraWithKey("name"),
            hasExtra("name", "David"),
            hasComponent(GreetingActivity::class.java.name)))

        Intents.release()
    }

}