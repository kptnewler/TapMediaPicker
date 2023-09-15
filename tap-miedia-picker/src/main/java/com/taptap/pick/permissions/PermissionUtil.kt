package com.taptap.pick.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.Size
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtil {
    const val ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION =
        "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION"

    fun hasPermissions(context: Context, @Size(min = 1) vararg perms: String): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            return true
        }
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun isAllGranted(grantResults: IntArray): Boolean {
        var isAllGranted = true
        if (grantResults.size > 0) {
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false
                    break
                }
            }
        } else {
            isAllGranted = false
        }
        return isAllGranted
    }


    /**
     * 跳转到系统设置页面
     */
    fun goIntentSetting(fragment: Fragment, requestCode: Int) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", fragment.activity?.packageName, null)
            intent.data = uri
            fragment.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}