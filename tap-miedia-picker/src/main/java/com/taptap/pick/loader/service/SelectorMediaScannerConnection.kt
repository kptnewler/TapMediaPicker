package com.taptap.pick.loader.service

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.text.TextUtils
import java.lang.ref.SoftReference

class SelectorMediaScannerConnection(context: Context, path: String?, l: ScanListener?) :
    MediaScannerConnection.MediaScannerConnectionClient {

    private var mPath: String? = null
    private var mMs: MediaScannerConnection? = null
    private var softReference: SoftReference<ScanListener>

    init {
        this.mPath = path
        this.softReference = SoftReference<ScanListener>(l)
        if (path == null || TextUtils.isEmpty(path)) {
            this.softReference.get()?.onScanFinish()
        } else {
            this.mMs = MediaScannerConnection(context.applicationContext, this)
            this.mMs?.connect()
        }
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        mMs?.disconnect()
        this.softReference.get()?.onScanFinish()
    }

    override fun onMediaScannerConnected() {
        if (!TextUtils.isEmpty(mPath)) {
            mMs?.scanFile(mPath, null)
        }
    }
}