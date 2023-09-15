package com.taptap.pick.data.bean

data class PreviewDataWrap(
    var page: Int = 1,
    var position: Int = 0,
    var bucketId: Long = 0,
    var totalCount: Int = 0,
    var isDownload: Boolean = false,
    var isDisplayCamera: Boolean = false,
    var isBottomPreview: Boolean = false,
    var isDisplayDelete: Boolean = false,
    var isExternalPreview: Boolean = false,
    var source: MutableList<LocalMedia> = mutableListOf()
) {
    fun reset() {
        position = 0
        bucketId = 0
        totalCount = 0
        isDownload = false
        isDisplayCamera = false
        isBottomPreview = false
        isDisplayDelete = false
        if (source.isNotEmpty()) {
            source.clear()
        }
    }
}