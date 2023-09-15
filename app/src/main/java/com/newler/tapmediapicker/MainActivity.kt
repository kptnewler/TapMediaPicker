package com.newler.tapmediapicker

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.luck.picture.lib.app.PictureAppMaster
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.MediaUtils

class MainActivity : AppCompatActivity() {
    private val TAG = "PictureSelectorTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<TextView>(R.id.open_gallery).setOnClickListener {
            // 方式一

            // 方式一
            PictureSelector.create(it.getContext())
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(CoilEngine())
                .buildLaunch(
                    R.id.fragment_container,
                    object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: ArrayList<LocalMedia?>?) {
                            analyticalSelectResults(ArrayList(result?.filterNotNull()))

                        }

                        override fun onCancel() {
                            Log.i(
                                TAG,
                                "PictureSelector Cancel"
                            )
                        }
                    })
        }
    }

    private fun analyticalSelectResults(result: ArrayList<LocalMedia>) {
        val builder = StringBuilder()
        builder.append("Result").append("\n")
        for (media in result) {
            if (media.width == 0 || media.height == 0) {
                if (PictureMimeType.isHasImage(media.mimeType)) {
                    val imageExtraInfo = MediaUtils.getImageSize(this, media.path)
                    media.width = imageExtraInfo.width
                    media.height = imageExtraInfo.height
                } else if (PictureMimeType.isHasVideo(media.mimeType)) {
                    val videoExtraInfo = MediaUtils.getVideoSize(
                        PictureAppMaster.getInstance().appContext,
                        media.path
                    )
                    media.width = videoExtraInfo.width
                    media.height = videoExtraInfo.height
                }
            }
            builder.append(media.availablePath).append("\n")
            Log.i(TAG, "文件名: " + media.fileName)
            Log.i(
                TAG,
                "是否压缩:" + media.isCompressed
            )
            Log.i(TAG, "压缩:" + media.compressPath)
            Log.i(TAG, "原图:" + media.path)
            Log.i(TAG, "绝对路径:" + media.realPath)
            Log.i(TAG, "是否裁剪:" + media.isCut)
            Log.i(TAG, "裁剪:" + media.cutPath)
            Log.i(
                TAG,
                "是否开启原图:" + media.isOriginal
            )
            Log.i(
                TAG,
                "原图路径:" + media.originalPath
            )
            Log.i(
                TAG,
                "沙盒路径:" + media.sandboxPath
            )
            Log.i(
                TAG,
                "原始宽高: " + media.width + "x" + media.height
            )
            Log.i(
                TAG,
                "裁剪宽高: " + media.cropImageWidth + "x" + media.cropImageHeight
            )
            Log.i(TAG, "文件大小: " + media.size)
        }
//        tvResult.setText(builder.toString())
    }
}