package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.sdp_begreen.begreen.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CameraFragmentTest {

    @Test
    fun testCameraFragment() {
        launchFragmentInContainer<CameraFragment>()
        onView(withId(R.id.cameraFragmentTextView)).check(matches(withText("Fragment where we can display the camera")))
    }

    @Test
    fun testCameraFragmentWithArgs() {


        val bundle = Bundle()
        bundle.putString("param1", "Param 1")
        bundle.putString("param2", "Param 2")

        // Still need to pass the bundle, doesn't work in test to only call the factory from companion object
        // https://github.com/android/android-test/issues/442
        launchFragmentInContainer(bundle) {
            CameraFragment.newInstance("", "")
        }
        onView(withId(R.id.cameraFragmentTextView)).check(matches(withText("Fragment where we can display the camera Param 1, Param 2")))

    }
}