package com.github.sdp_begreen.begreen.firebase

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.github.sdp_begreen.begreen.rules.CoroutineTestRule
import com.github.sdp_begreen.begreen.rules.FirebaseEmulatorRule
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.contains
import org.junit.ClassRule
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
        @get:ClassRule
        @JvmStatic
        val firebaseEmulatorRule = FirebaseEmulatorRule()
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
            launch {
                // drop first value, will contain previously connected user if any
                // only focus on newly emitted values
                assertThat(firebaseAuth.getFlowUserIds().drop(1).take(6).toList(),
                    contains(null, "VaRgQioAuiGtfDlv5uNuosNsACCJ",
                        null,
                        "r32POH2SnXu9dSLTxa1GMOQgg8cp",
                        null,
                        "IvnU7seNMaG8qrx29ps6liiJamrw"))
            }
            // add some delay to be sure that the listener is in place before we start
            // emitting new values
            delay(20)
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user1@email.ch", "123456").await()
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user2@email.com", "123456").await()
            Firebase.auth.signOut()
            Firebase.auth.signInWithEmailAndPassword("user3@email.com", "123456").await()
        }
    }
}