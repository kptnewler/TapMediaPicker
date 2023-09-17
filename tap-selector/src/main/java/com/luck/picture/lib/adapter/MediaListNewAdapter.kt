package com.luck.picture.lib.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DensityUtil
import info.hellovass.kdrawable.shape.KShape
import info.hellovass.kdrawable.shapeDrawable
import info.hellovass.kdrawable.stateListDrawable

/**
 * @author：luck
 * @date：2022/12/1 1:18 下午
 * @describe：MediaListNewAdapter
 */
class MediaListNewAdapter : MediaListAdapter() {

    override fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListMediaViewHolder {
        return super.onCreateImageViewHolder(inflater, parent).apply {
            (tvSelectView.layoutParams as? RelativeLayout.LayoutParams)?.let {
                it.width = DensityUtil.dip2px(itemView.context, 24f)
                it.height = DensityUtil.dip2px(itemView.context, 24f)
                it.setMargins(0, DensityUtil.dip2px(itemView.context, 8f), DensityUtil.dip2px(itemView.context, 8f), 0)
            }


            tvSelectView.background = stateListDrawable {
                selectedState {
                    shapeDrawable {
                        shape = KShape.Oval
                        solidColor = ContextCompat.getColor(itemView.context, com.tap.intl.lib.intl_widget.R.color.green_opacity40)
                    }
                }

                unselectedState {
                    shapeDrawable {
                        solidColor = ContextCompat.getColor(itemView.context, com.tap.intl.lib.intl_widget.R.color.overlay)
                        shape = KShape.Oval
                        stroke {
                            width = DensityUtil.dip2px(itemView.context, 1f)
                            color = ContextCompat.getColor(itemView.context, com.tap.intl.lib.intl_widget.R.color.white_primary)
                        }
                    }
                }
            }
        }
    }

    override fun bindData(holder: ListMediaViewHolder, media: LocalMedia, position: Int) {
        super.bindData(holder, media, position)
//        holder.tvSelectView.setBackgroundResource(R.drawable.ps_default_num_selector)

        notifySelectNumberStyle(holder, media)
    }

    @SuppressLint("SetTextI18n")
    private fun notifySelectNumberStyle(holder: ListMediaViewHolder, currentMedia: LocalMedia) {
        holder.tvSelectView.text = ""
        val selectResult = mGetSelectResultListener?.onSelectResult() ?: mutableListOf()
        val position = selectResult.indexOf(currentMedia)
        if (position >= 0) {
            holder.tvSelectView.text = "${position + 1}"
        }
    }
}