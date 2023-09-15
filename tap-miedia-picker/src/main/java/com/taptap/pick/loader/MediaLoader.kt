package com.taptap.pick.loader

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.IntRange
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.bean.LocalMediaAlbum

const val DURATION: String = "duration"
const val BUCKET_DISPLAY_NAME = "bucket_display_name"
const val BUCKET_ID = "bucket_id"
const val ORIENTATION = "orientation"
const val NOT_GIF = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/gif')"
const val NOT_WEBP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/webp')"
const val NOT_BMP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/bmp')"
const val NOT_XMS_BMP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/x-ms-bmp')"
const val NOT_VND_WAP_BMP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/vnd.wap.wbmp')"
const val NOT_HEIC = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/heic')"
const val MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE

val PROJECTION = arrayOf(
    MediaStore.Files.FileColumns._ID,
    MediaStore.MediaColumns.DATA,
    MediaStore.MediaColumns.MIME_TYPE,
    MediaStore.MediaColumns.WIDTH,
    MediaStore.MediaColumns.HEIGHT,
    DURATION,
    MediaStore.MediaColumns.SIZE,
    BUCKET_DISPLAY_NAME,
    MediaStore.MediaColumns.DISPLAY_NAME,
    BUCKET_ID,
    MediaStore.MediaColumns.DATE_ADDED,
    ORIENTATION
)

abstract class MediaLoader {

    protected abstract fun getQueryUri(): Uri

    protected abstract fun getProjection(): Array<String>?

    protected abstract fun getAlbumSelection(): String?

    protected abstract fun getSelection(bucketId: Long): String?

    protected abstract fun getSelectionArgs(): Array<String>?

    protected abstract fun getSortOrder(): String?

    protected abstract fun parse(media: LocalMedia, data: Cursor): LocalMedia?

    abstract suspend fun loadMediaAlbum(): MutableList<LocalMediaAlbum>

    abstract suspend fun loadMedia(
        @IntRange(
            from = 1,
            to = Long.MAX_VALUE
        ) pageSize: Int
    ): MutableList<LocalMedia>

    abstract suspend fun loadMedia(
        bucketId: Long,
        @IntRange(
            from = 1,
            to = Long.MAX_VALUE
        ) pageSize: Int
    ): MutableList<LocalMedia>

    abstract suspend fun loadMediaMore(
        bucketId: Long,
        @IntRange(from = 1, to = Long.MAX_VALUE) page: Int,
        @IntRange(from = 1, to = Long.MAX_VALUE) pageSize: Int
    ): MutableList<LocalMedia>

    abstract suspend fun loadAppInternalDir(sandboxDir: String): MutableList<LocalMedia>
}