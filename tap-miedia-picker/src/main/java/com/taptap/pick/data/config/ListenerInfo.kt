package com.taptap.pick.data.config

import android.content.Context
import androidx.fragment.app.Fragment
import com.taptap.pick.data.bean.LocalMedia

class ListenerInfo {
    var onResultCallbackListener: OnResultCallbackListener? = null
    var onPermissionApplyListener: OnPermissionApplyListener? = null
    var onPermissionDescriptionListener: OnPermissionDescriptionListener? = null
    var onPermissionDeniedListener: OnPermissionDeniedListener? = null
    var onConfirmListener: OnConfirmListener? = null
    var onEditorMediaListener: OnEditorMediaListener? = null
}

interface OnConfirmListener {
    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param context
     * @param result select result
     * @return the boolean result
     */
    fun onConfirm(context: Context, result: MutableList<LocalMedia>): Boolean
}

interface OnPermissionDescriptionListener {
    /**
     * Permission description
     *
     * @param fragment
     * @param permissionArray
     */
    fun onDescription(fragment: Fragment, permissionArray: Array<String>)

    /**
     * onDismiss
     */
    fun onDismiss(fragment: Fragment)
}

interface OnResultCallbackListener {
    fun onResult(result: List<LocalMedia>)

    fun onCancel()
}

interface OnPermissionApplyListener {
    fun requestPermission(
        fragment: Fragment,
        permissionArray: Array<String>,
        call: OnRequestPermissionListener
    )

    fun hasPermissions(fragment: Fragment, permissionArray: Array<String>): Boolean
}

interface OnRequestPermissionListener {
    fun onCall(permission: Array<String>, isResult: Boolean)
}

interface OnCallbackListener<T> {
    fun onCall(data: T)
}

interface OnPermissionDeniedListener {
    fun onDenied(fragment: Fragment, permissionArray: Array<String>, requestCode: Int, call: OnCallbackListener<Boolean>)
}


interface OnEditorMediaListener {
    fun onEditorMedia(fragment: Fragment, media: LocalMedia, requestCode: Int)
}