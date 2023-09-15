package com.taptap.pick.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.providers.SelectorProviders
import com.taptap.pick.ui.adapter.OnLongClickListener
import com.taptap.pick.ui.adapter.OnPreviewListener
import com.taptap.pick.utils.DensityUtil
import com.newler.tap_miedia_picker.R

abstract class BasePreviewMediaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected val config = SelectorProviders.getConfig()
    protected var screenWidth = DensityUtil.getRealScreenWidth(itemView.context)
    protected var screenHeight = DensityUtil.getScreenHeight(itemView.context)
    protected var screenAppInHeight = DensityUtil.getRealScreenHeight(itemView.context)
    val imageCover: ImageView = itemView.findViewById(R.id.iv_preview_cover)

    /**
     * Item Title Content Switching
     */
    var onPreviewListener: OnPreviewListener? = null

    open fun getRealSizeFromMedia(media: LocalMedia): IntArray {
        return if ((media.isCrop() || media.isEditor()) && media.cropWidth > 0 && media.cropHeight > 0) {
            intArrayOf(media.cropWidth, media.cropHeight)
        } else {
            intArrayOf(media.width, media.height)
        }
    }

    abstract fun loadCover(media: LocalMedia)

    abstract fun coverScaleType(media: LocalMedia)

    abstract fun coverLayoutParams(media: LocalMedia)

    /**
     * onViewAttachedToWindow
     */
    abstract fun onViewAttachedToWindow()

    /**
     * onViewDetachedFromWindow
     */
    abstract fun onViewDetachedFromWindow()

    /**
     * bind data
     */
    open fun bindData(media: LocalMedia, position: Int) {
        loadCover(media)
        coverScaleType(media)
        coverLayoutParams(media)
        imageCover.setOnClickListener {
            setClickEvent(media)
        }
        imageCover.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    /**
     * release
     */
    abstract fun release()

    open fun setClickEvent(media: LocalMedia) {
        onPreviewListener?.onClick(media)
    }

    /**
     * Item Long press click
     */
    private var onLongClickListener: OnLongClickListener<LocalMedia>? = null
    fun setOnLongClickListener(l: OnLongClickListener<LocalMedia>?) {
        this.onLongClickListener = l
    }

    open fun setLongClickEvent(holder: RecyclerView.ViewHolder, position: Int, media: LocalMedia) {
        onLongClickListener?.onLongClick(holder, position, media)
    }

}