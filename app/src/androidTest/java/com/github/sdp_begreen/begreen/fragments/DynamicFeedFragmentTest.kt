package com.github.sdp_begreen.begreen.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.ParcelableDate
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import java.net.URL
import java.util.Date

class DynamicFeedFragmentTest {

    companion object {
        private val db: DB = Mockito.mock(DB::class.java)
        private val user1 = User("user1", 100, "user1", 100)
        private val user2 = User("user1", 100, "user1", 100, posts = listOf(PhotoMetadata("id", "title", ParcelableDate(date = Date()), "user2", "type", "desc")))
        private val user = User("user", 100, "user1", 100,
        "description", "007",
        "mail", 100, listOf(user1),
        listOf(user2))
        val url = URL("https://picsum.photos/400")

        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUp() {
            // The implementation need to be provided before the rule is executed,
            // that's why we do it in the beforeClass method
            runTest {
                Mockito.`when`(db.getUser("user")).thenReturn(user)
                Mockito.`when`(db.getUser("user1")).thenReturn(user1)
                Mockito.`when`(db.getUser("user2")).thenReturn(user2)

                Mockito.`when`(db.getImage(any(), any())).thenReturn(BitmapFactory.decodeStream(
                    withContext(Dispatchers.IO) {
                        withContext(Dispatchers.IO) {
                            url.openConnection()
                        }.getInputStream()
                    }))
            }
        }
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(module {
            single { db }
        })
    )

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun test() {
        // Launch fragment with arguments
        val scenario = FragmentScenario.launchInContainer(DynamicFeedFragment::class.java)
    }

}