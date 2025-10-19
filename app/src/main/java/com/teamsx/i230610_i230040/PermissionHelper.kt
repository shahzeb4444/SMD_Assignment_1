package com.teamsx.i230610_i230040

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    fun hasPermissions(context: Context): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: androidx.appcompat.app.AppCompatActivity, requestCode: Int) {
        if (!hasPermissions(activity)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, requestCode)
        }
    }
}