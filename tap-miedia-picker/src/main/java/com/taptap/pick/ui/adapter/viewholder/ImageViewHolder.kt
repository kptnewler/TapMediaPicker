package com.taptap.pick.ui.adapter.viewholder

import android.view.View
import com.taptap.pick.data.bean.LocalMedia

/**
 * @author：luck
 * @date：2022/11/30 3:33 下午
 * @describe：ImageViewHolder
 */
open class ImageViewHolder(itemView: View) : ListMediaViewHolder(itemView) {
    override fun bindData(media: LocalMedia, position: Int) {
        onMergeEditor(media)
    }

    open fun onMergeEditor(media: LocalMedia) {
        val selectResult = mGetSelectResultListener?.onSelectResult()
        if (!selectResult.isNullOrEmpty()) {
            if (!media.isEditor()) {
                val position = selectResult.indexOf(media)
                if (position >= 0) {
                    val existsMedia = selectResult[position]
                    if (existsMedia.isEditor()) {
                        media.cropWidth = existsMedia.cropWidth
                        media.cropHeight = existsMedia.cropHeight
                        media.editorPath = existsMedia.editorPath
                        media.editorData = existsMedia.editorData
                        media.cropOffsetX = existsMedia.cropOffsetX
                        media.cropOffsetY = existsMedia.cropOffsetY
                        media.cropAspectRatio = existsMedia.cropAspectRatio
                    }
                }
            }
        }
    }
}