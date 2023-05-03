package com.github.sdp_begreen.begreen.rules

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Rules used to start the firebase emulator
 */
class FirebaseEmulatorRule: TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        try {
            Firebase.database.useEmulator("10.0.2.2", 9000)
            Firebase.storage.useEmulator("10.0.2.2", 9199)
            Firebase.auth.useEmulator("10.0.2.2", 9099)
        } catch (_:java.lang.IllegalStateException){}
    }
}