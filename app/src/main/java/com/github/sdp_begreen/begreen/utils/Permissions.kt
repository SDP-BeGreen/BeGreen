package com.github.sdp_begreen.begreen.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Permissions {

    fun hasPermissions(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}