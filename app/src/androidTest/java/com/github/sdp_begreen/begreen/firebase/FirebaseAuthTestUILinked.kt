package com.github.sdp_begreen.begreen.firebase

import androidx.test.core.app.launchActivity
import androidx.test.espresso.matcher.ViewMatchers
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
import org.hamcrest.CoreMatchers
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


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
            ViewMatchers.assertThat(Firebase.auth.uid, CoreMatchers.`is`(CoreMatchers.equalTo("VaRgQioAuiGtfDlv5uNuosNsACCJ")))

            val a = launchActivity<MainActivity>()

            a.onActivity {
                firebaseAuth.signOutCurrentUser(it, it.getString(R.string.default_web_client_id))
            }

            ViewMatchers.assertThat(Firebase.auth.uid, CoreMatchers.nullValue())

            a.close()
        }
    }
}