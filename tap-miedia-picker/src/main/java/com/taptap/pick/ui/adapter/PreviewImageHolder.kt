package com.taptap.pick.ui.adapter

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.ui.adapter.viewholder.BasePreviewMediaHolder
import com.taptap.pick.utils.BitmapUtils
import com.taptap.pick.utils.MediaUtils

open class PreviewImageHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    override fun onViewAttachedToWindow() {}
    override fun onViewDetachedFromWindow() {}
    override fun release() {}

    override fun coverLayoutParams(media: LocalMedia) {
        if (screenWidth < screenHeight) {
            if (media.width > 0 && media.height > 0) {
                (imageCover.layoutParams as FrameLayout.LayoutParams).apply {
                    this.width = screenWidth
                    this.height = screenAppInHeight
                    this.gravity = Gravity.CENTER
                }
            }
        }
    }

    override fun loadCover(media: LocalMedia) {
        val size = getRealSizeFromMedia(media)
        val mediaComputeSize = BitmapUtils.getComputeImageSize(size[0], size[1])
        val width = mediaComputeSize[0]
        val height = mediaComputeSize[1]
        if (width > 0 && height > 0) {
            config.imageEngine?.loadImage(
                itemView.context,
                media.getAvailablePath(),
                width,
                height,
                imageCover
            )
        } else {
            config.imageEngine?.loadImage(itemView.context, media.getAvailablePath(), imageCover)
        }
    }

    override fun coverScaleType(media: LocalMedia) {
        if (MediaUtils.isLongImage(media.width, media.height)) {
            imageCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }
}