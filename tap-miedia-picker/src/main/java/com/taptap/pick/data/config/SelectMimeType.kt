package com.taptap.pick.data.config

object SelectMimeType {
    private const val TYPE_ALL = 0
    private const val TYPE_IMAGE = 1
    private const val TYPE_VIDEO = 2

    const val SYSTEM_IMAGE = "image/*"

    const val SYSTEM_VIDEO = "video/*"

    const val SYSTEM_ALL: String = "$SYSTEM_IMAGE,$SYSTEM_VIDEO"

    /**
     * 图片 + 视频
     *
     */
    fun ofAll(): Int {
        return TYPE_ALL
    }

    fun ofImage(): Int {
        return TYPE_IMAGE
    }

    fun ofVideo(): Int {
        return TYPE_VIDEO
    }

}