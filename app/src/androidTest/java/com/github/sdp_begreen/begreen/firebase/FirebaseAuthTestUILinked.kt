package com.github.sdp_begreen.begreen.firebase

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testing [FirebaseAuth] needed to be split in half in order to be tested, due to problem
 * with coroutine test environment and UI modification
 *
 * So this test class is responsible to test the sign out method, which is linked to UI
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class FirebaseAuthTestUILinked {
    companion object {
        private val firebaseAuth = FirebaseAuth()
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()
    }

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun signOutCurrentUserCorrectlySignUserOut() {
        runBlocking {
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user1@email.ch", "123456").await()
            assertThat(Firebase.auth.uid, `is`(equalTo("VaRgQioAuiGtfDlv5uNuosNsACCJ")))

            val activityScenario = launchActivity<MainActivity>()

            activityScenario.onActivity {
                firebaseAuth.signOutCurrentUser(it, it.getString(R.string.default_web_client_id))
            }

            assertThat(Firebase.auth.uid, nullValue())

            activityScenario.close()
        }
    }
}