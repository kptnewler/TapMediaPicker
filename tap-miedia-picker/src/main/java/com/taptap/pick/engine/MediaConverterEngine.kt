package com.taptap.pick.engine

import android.content.Context
import com.taptap.pick.data.bean.LocalMedia

interface MediaConverterEngine {

    suspend fun converter(context: Context, media: LocalMedia): LocalMedia
}