package com.taptap.pick.engine

import android.content.Context
import com.taptap.pick.data.bean.LocalMedia

interface MediaConverterEngine {
    /**
     * Media Data Converter
     * 1、Android 10 platform, sandbox processing of external directory files [LocalMedia.sandboxPath]
     * 2、Android 10 platform, original image processing [LocalMedia.originalPath]
     * 3、Video thumbnail [LocalMedia.videoThumbnailPath]
     * 4、Image watermark [LocalMedia.watermarkPath]
     * 5、Image or video compression [LocalMedia.compressPath]
     * ...
     * Customize Other Actions [LocalMedia.customizeExtra]
     */
    suspend fun converter(context: Context, media: LocalMedia): LocalMedia
}