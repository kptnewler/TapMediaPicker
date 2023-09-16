package com.taptap.pick.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.config.SelectionMode
import com.taptap.pick.data.constant.SelectedState
import com.taptap.pick.data.constant.SelectorConstant
import com.taptap.pick.utils.DensityUtil
import com.taptap.pick.utils.MediaUtils
import com.newler.tap_miedia_picker.R
import com.tap.intl.lib.intl_widget.widget.text.TapText
import info.hellovass.kdrawable.shape.KShape
import info.hellovass.kdrawable.shapeDrawable
import info.hellovass.kdrawable.stateListDrawable

open class ListMediaViewHolder(itemView: View) : BaseListViewHolder(itemView) {
    var tvSelectView: TapText = itemView.findViewById(R.id.tv_pick_index)
    var ivCover: ImageView = itemView.findViewById(R.id.iv_cover)

    init {
        tvSelectView.background = stateListDrawable {
            selectedState {
                shapeDrawable {
                    shape = KShape.Oval
                    solidColor = ContextCompat.getColor(itemView.context, com.tap.intl.lib.intl_widget.R.color.green_primary)
                }
            }

            unselectedState {
                shapeDrawable {
                    solidColor = ContextCompat.getColor(itemView.context, com.tap.intl.lib.intl_widget.R.color.black_opacity60)
                    shape = KShape.Oval
                    stroke {
                        width = DensityUtil.dip2px(itemView.context, 3f)
                        color = ContextCompat.getColor(itemView.context, com.tap.intl.lib.intl_widget.R.color.white_primary)
                    }
                }
            }
        }
    }


    open fun bindData(media: LocalMedia, position: Int) {
        tvSelectView.visibility =
            if (config.selectionMode == SelectionMode.ONLY_SINGLE) View.GONE else View.VISIBLE

        mGetSelectResultListener?.onSelectResult()?.let { result ->
            isSelectedMedia(result.contains(media))
        }
        config.imageEngine?.loadListImage(ivCover.context, media.getAvailablePath(), ivCover)
        tvSelectView.setOnClickListener {
            if (media.id == SelectorConstant.INVALID_DATA) {
                return@setOnClickListener
            }
            val resultCode =
                mItemClickListener?.onSelected(
                    tvSelectView.isSelected,
                    position,
                    media
                )
            if (resultCode == SelectedState.INVALID) {
                return@setOnClickListener
            }

            mGetSelectResultListener?.onSelectResult()?.let { result ->
                isSelectedMedia(result.contains(media))
            }
        }

        itemView.setOnClickListener {
            if (media.id == SelectorConstant.INVALID_DATA) {
                return@setOnClickListener
            }
            val isPreview = when {
                MediaUtils.hasMimeTypeOfImage(media.mimeType) -> {
                    config.isEnablePreviewImage
                }
                else -> {
                    false
                }
            }
            when {
                isPreview -> {
                    mItemClickListener?.onItemClick(tvSelectView, position, media)
                }
                config.selectionMode == SelectionMode.ONLY_SINGLE -> {
                    mItemClickListener?.onComplete(
                        tvSelectView.isSelected, position,
                        media
                    )
                }
                else -> {
                    tvSelectView.performClick()
                }
            }
        }
        itemView.setOnLongClickListener { v ->
            mItemClickListener?.onItemLongClick(v, position, media)
            false
        }
    }

    /**
     * selectedMedia
     *
     * @param isSelected
     */
    private fun isSelectedMedia(isSelected: Boolean) {
        if (tvSelectView.isSelected != isSelected) {
            tvSelectView.isSelected = isSelected
        }
    }
}
