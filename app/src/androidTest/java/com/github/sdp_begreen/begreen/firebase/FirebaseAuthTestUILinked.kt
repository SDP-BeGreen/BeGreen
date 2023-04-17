package com.github.sdp_begreen.begreen.firebase

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.activities.MainActivity
import com.github.sdp_begreen.begreen.rules.KoinTestRule
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.BeforeClass
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
        @BeforeClass
        @JvmStatic fun setup() {
            try {
                Firebase.database.useEmulator("10.0.2.2", 9000)
                Firebase.storage.useEmulator("10.0.2.2", 9199)
                Firebase.auth.useEmulator("10.0.2.2", 9099)
            } catch (_:java.lang.IllegalStateException){}
        }
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