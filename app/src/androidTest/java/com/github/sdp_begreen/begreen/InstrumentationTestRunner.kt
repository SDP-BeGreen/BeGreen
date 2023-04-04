package com.github.sdp_begreen.begreen

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Application to use in test to avoid creating two instance of koin
 */
class TestApplication : Application()

/**
 * Runner used to run our android test
 */
@Suppress("Unused")
class InstrumentationTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}