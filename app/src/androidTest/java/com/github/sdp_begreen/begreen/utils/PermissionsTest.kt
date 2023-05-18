package com.github.sdp_begreen.begreen.utils

import android.Manifest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.sdp_begreen.begreen.activities.SignInActivity
import com.github.sdp_begreen.begreen.utils.Permissions.hasPermissions
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class PermissionsTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignInActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Test
    fun hasPermissionsForCameraReturnsTrueWhenCameraPermissionsAreGranted() {
        activityRule.scenario.onActivity {
            assertThat(
                hasPermissions(it.applicationContext, Manifest.permission.CAMERA),
                `is`(equalTo(true))
            )
        }
    }

    @Test
    fun hasPermissionsForBatteryStatsReturnsFalseWhenBatteryStatsPermissionsAreNotGranted() {
        activityRule.scenario.onActivity {
            assertThat(
                hasPermissions(it.applicationContext, Manifest.permission.BATTERY_STATS),
                `is`(equalTo(false))
            )
        }
    }

}