package com.taptap.pick.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.IntDef
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.fragment.app.Fragment
import com.taptap.pick.data.bean.CaptureModel
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraUtils(activity: Activity) {
    private var mContext: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment>? = null
    private var mCurrentPhotoUri: Uri? = null
    var mCurrentPhotoPath: String? = null
    private var mCaptureModel: CaptureModel? = null
    val PHOTO = 1
    val VIDEO = 2

   init {
       mContext = WeakReference(activity)
       mFragment = null
   }

    fun hasCameraFeature(context: Context): Boolean {
        val numberOfCameras = Camera.getNumberOfCameras()
        val pm = context.applicationContext.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && numberOfCameras > 0
    }

    fun setCaptureModel(mCaptureModel: CaptureModel?) {
        this.mCaptureModel = mCaptureModel
    }

    fun dispatchCaptureIntent(context: Context, requestCode: Int,  mod: Int) {
        val captureIntent = Intent(
            if (mod == PHOTO) MediaStore.ACTION_IMAGE_CAPTURE else MediaStore.ACTION_VIDEO_CAPTURE
        )
        if (captureIntent.resolveActivity(context.packageManager) != null) {
            var captureFile: File? = null
            try {
                captureFile = if (mod == PHOTO) {
                    createImageFile()
                } else {
                    createVideoFile()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (captureFile != null) {
                mCurrentPhotoPath = captureFile.absolutePath
                mCurrentPhotoUri = FileProvider.getUriForFile(
                    mContext!!.get()!!,
                    mCaptureModel!!.authority, captureFile
                )
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri)
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                if (mFragment != null) {
                    mFragment?.get()?.startActivityForResult(captureIntent, requestCode)
                } else {
                    mContext!!.get()?.startActivityForResult(captureIntent, requestCode)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // 创建一个图片
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = String.format("JPEG_%s.jpg", timeStamp)
        var storageDir: File?
        if (mCaptureModel!!.isPublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
        } else {
            storageDir = mContext!!.get()!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }
        if (mCaptureModel!!.directory != null) {
            storageDir = File(storageDir, mCaptureModel!!.directory)
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
        }
        val tempFile = File(storageDir, imageFileName)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) {
            null
        } else tempFile
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File? {
        // 创建一个图片
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = String.format("TapVideo_%s.mp4", timeStamp)
        var storageDir: File?
        if (mCaptureModel!!.isPublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            )
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
        } else {
            storageDir = mContext!!.get()!!.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        }
        if (mCaptureModel!!.directory != null) {
            storageDir = File(storageDir, mCaptureModel!!.directory)
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
        }
        val tempFile = File(storageDir, imageFileName)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) {
            null
        } else tempFile
    }

    fun mediaScan(context: Context?, path: String?) {
        if (TextUtils.isEmpty(path) || context == null) {
            return
        }
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, path)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(File(path?:""))
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun getCurrentPhotoUri(): Uri? {
        return mCurrentPhotoUri
    }

    fun getCurrentPhotoPath(): String? {
        return mCurrentPhotoPath
    }
}