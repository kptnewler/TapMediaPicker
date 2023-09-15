package com.taptap.pick.data.config

import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.bean.PreviewDataWrap
import com.taptap.pick.data.constant.SelectorConstant
import com.taptap.pick.engine.CropEngine
import com.taptap.pick.engine.ImageEngine
import com.taptap.pick.engine.MediaConverterEngine
import com.taptap.pick.loader.MediaLoader

class PickerConfig {
    var dataLoader: MediaLoader? = null
    var cropEngine: CropEngine? = null
    var imageEngine: ImageEngine? = null
    var mediaConverterEngine: MediaConverterEngine? = null
    var mediaType = MediaType.IMAGE
    var sandboxDir: String? = null
    var imageOutputDir: String? = null
    var videoOutputDir: String? = null
    var audioOutputDir: String? = null
    var isOnlySandboxDir = false
    var isOriginalControl = false
    var isPreviewFullScreenMode = false
    var isActivityResult = false
    var isForegroundService = false
    var isGif = false
    var isWebp = false
    var isBmp = false
    var isHeic = false
    var isEmptyResultBack = false
    var isMaxSelectEnabledMask = false
    var isDisplayCamera = true
    var isEnablePreviewImage = true
    var isEnablePreviewVideo = true
    var isPauseResumePlay = false
    var defaultAlbumName: String? = null
    var filterVideoMaxSecond = 0L
    var filterVideoMinSecond = 0L
    var filterMaxFileSize = 0L
    var filterMinFileSize = 0L
    var onlyQueryImageFormat = hashSetOf<String>()
    var onlyQueryVideoFormat = hashSetOf<String>()
    var selectionMode = SelectionMode.MULTIPLE
    var isAsTotalCount = false
    var totalCount = SelectorConstant.DEFAULT_MAX_SELECT_NUM
    var maxVideoSelectNum = 0
    var isAllWithImageVideo = false
    var pageSize: Int = 0
    var listenerInfo: ListenerInfo? = null
    var minSelectNum = 0
    var allCameraMediaType: MediaType = MediaType.IMAGE
    var skipCropFormat = hashSetOf<String>()
    var selectedSource = mutableListOf<LocalMedia>()
    var previewWrap = PreviewDataWrap()

    fun getSelectCount(): Int {
        return if (isAsTotalCount) totalCount else totalCount + maxVideoSelectNum
    }

    fun destroy() {
        this.dataLoader = null
        this.cropEngine = null
        this.imageEngine = null
        this.mediaConverterEngine = null
    }
}