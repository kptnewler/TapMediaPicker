package com.newler.tapmediapicker

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.model.PictureSelector
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.MediaUtils.getAssignFileMedia
import com.luck.picture.lib.utils.SelectorLogUtils
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val TAG = "PictureSelectorTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.open_preview).setOnClickListener {
            val list = mutableListOf<String>(

                "/storage/emulated/0/DCIM/Camera/IMG_20221106_141111.jpg",
                "/storage/emulated/0/snowball/xueqiu/xueqiu_1667696065952.jpg",
                "/storage/emulated/0/DCIM/Screenshots/Screenshot_2022_1105_125815.jpg",
                "/storage/emulated/0/DCIM/Screenshots/Screenshot_20221030_231431.jpg"
            )

            val source: MutableList<LocalMedia> = mutableListOf()
            runBlocking {
                list.forEach { path ->
                    val path = when {
                        MediaUtils.isContent(path) -> {
                            val absolutePath = MediaUtils.getPath(this@MainActivity, Uri.parse(path))
                            absolutePath
                        }
                        else -> {
                            path
                        }
                    }
                    if (!TextUtils.isEmpty(path)) {
                        val media = getAssignFileMedia(this@MainActivity, path?:"")
                        source.add(media)
                    }
                }
            }


            PictureSelector.create(it.getContext())
                .openGallery(MediaType.IMAGE)
                .isDisplayCamera(true)
                .isNewNumTemplate(true)
                .setShowNext(true)
                .isPreviewFullScreenMode(true)
                .setImageEngine(CoilEngine())
                .setSelectedData(source)
                .setSwitchToCropPreview(true)
                .buildLaunch(
                    R.id.fragment_container,
                    object : OnResultCallbackListener {
                        override fun onResult(result: List<LocalMedia>) {
                            val stringBuilder = StringBuilder()
                            result.forEach { media ->
                                stringBuilder.append(media.absolutePath).append("\n")
                            }
                            Log.d(TAG, "onResult: $stringBuilder")
                        }

                        override fun onCancel() {
                            SelectorLogUtils.info("onCancel")
                        }
                    })
        }
        
        findViewById<TextView>(R.id.open_gallery).setOnClickListener {
            // 方式一
            PictureSelector.create(it.getContext())
                .openGallery(MediaType.IMAGE)
                .isDisplayCamera(true)
                .isNewNumTemplate(true)
                .setShowNext(true)
                .isPreviewFullScreenMode(true)
                .setImageEngine(CoilEngine())
                .setSwitchToCropPreview(false)
                .buildLaunch(
                    R.id.fragment_container,
                    object : OnResultCallbackListener {
                        override fun onResult(result: List<LocalMedia>) {
                            val stringBuilder = StringBuilder()
                            result.forEach { media ->
                                stringBuilder.append(media.absolutePath).append("\n")
                            }
                            Log.d(TAG, "onResult: $stringBuilder")
                        }

                        override fun onCancel() {
                            SelectorLogUtils.info("onCancel")
                        }
                    })
        }
    }

}