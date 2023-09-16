package com.taptap.pick.data.bean

import android.os.Parcelable
import android.text.TextUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalMedia(
    var id: Long = 0,
    var bucketId: Long = 0,
    var displayName: String? = null,
    var bucketDisplayName: String? = null,
    var path: String? = null,
    var absolutePath: String? = null,
    var mimeType: String? = null,
    var width: Int = 0,
    var height: Int = 0,
    var cropPath: String? = null,
    var editorPath: String? = null,
    var cropWidth: Int = 0,
    var cropHeight: Int = 0,
    var cropOffsetX: Int = 0,
    var cropOffsetY: Int = 0,
    var cropAspectRatio: Float = 0F,
    var duration: Long = 0,
    var size: Long = 0,
    var dateAdded: Long = 0,
    var orientation: Int = 0,
    var editorData: String? = null,
    var sandboxPath: String? = null,
    var originalPath: String? = null,
    var compressPath: String? = null,
    var watermarkPath: String? = null,
    var customizeExtra: String? = null,
) : Parcelable {
    fun isCrop(): Boolean {
        return !TextUtils.isEmpty(cropPath)
    }

    fun isEditor(): Boolean {
        return !TextUtils.isEmpty(editorPath)
    }

    fun isCompress(): Boolean {
        return !TextUtils.isEmpty(compressPath)
    }

    fun isOriginal(): Boolean {
        return !TextUtils.isEmpty(originalPath)
    }

    fun isWatermark(): Boolean {
        return !TextUtils.isEmpty(watermarkPath)
    }

    fun isCopySandbox(): Boolean {
        return !TextUtils.isEmpty(sandboxPath)
    }

    fun getAvailablePath(): String? {
        return when {
            isCrop() -> {
                cropPath
            }

            isEditor() -> {
                editorPath
            }

            isCompress() -> {
                compressPath
            }

            isCopySandbox() -> {
                sandboxPath
            }

            isOriginal() -> {
                originalPath
            }

            isWatermark() -> {
                watermarkPath
            }

            else -> path
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalMedia) return false
        return (id == other.id || TextUtils.equals(path, other.path)
                || TextUtils.equals(absolutePath, other.absolutePath))
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + bucketId.hashCode()
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (bucketDisplayName?.hashCode() ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + (absolutePath?.hashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + (cropPath?.hashCode() ?: 0)
        result = 31 * result + (editorPath?.hashCode() ?: 0)
        result = 31 * result + cropWidth
        result = 31 * result + cropHeight
        result = 31 * result + cropOffsetX
        result = 31 * result + cropOffsetY
        result = 31 * result + cropAspectRatio.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + orientation
        result = 31 * result + (editorData?.hashCode() ?: 0)
        result = 31 * result + (sandboxPath?.hashCode() ?: 0)
        result = 31 * result + (originalPath?.hashCode() ?: 0)
        result = 31 * result + (compressPath?.hashCode() ?: 0)
        result = 31 * result + (watermarkPath?.hashCode() ?: 0)
        result = 31 * result + (customizeExtra?.hashCode() ?: 0)
        return result
    }
}
