package com.github.sdp_begreen.begreen.firebase

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.contains
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testing [FirebaseAuth] needed to be split in half in order to be tested, due to problem
 * with coroutine test environment and UI modification
 *
 * So this test class is responsible to test getting the user id which doesn't require
 * to start any activities
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
class FirebaseAuthTestNoUILinked {
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
    val coroutineRules = CoroutineTestRule()

    @Test
    fun getConnectedUserIdReturnCorrectId() {
        runTest {
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user1@email.ch", "123456").await()

            assertThat(firebaseAuth.getConnectedUserId(), `is`(equalTo(Firebase.auth.uid)))
        }
    }

    @Test
    fun getConnectedUserIdReturnCorrectIdsMultipleConnection() {
        runTest {
            Firebase.auth.signOut() // ensure signed out
            launch {
                delay(10)
                assertThat(firebaseAuth.getConnectedUserIds().take(6).toList(),
                    contains(null, "VaRgQioAuiGtfDlv5uNuosNsACCJ",
                        null,
                        "r32POH2SnXu9dSLTxa1GMOQgg8cp",
                        null,
                        "IvnU7seNMaG8qrx29ps6liiJamrw"))
            }

            // add some delay to be sure that the listener is in place before we start
            // emitting new values
            delay(20)
            Firebase.auth.signInWithEmailAndPassword("user1@email.ch", "123456").await()
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user2@email.com", "123456").await()
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user3@email.com", "123456").await()
        }
    }
}