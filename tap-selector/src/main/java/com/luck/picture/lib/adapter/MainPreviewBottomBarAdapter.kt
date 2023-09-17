package com.luck.picture.lib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.DensityUtil
import info.hellovass.kdrawable.shapeDrawable

class MainPreviewBottomBarAdapter : RecyclerView.Adapter<MainPreviewBottomBarAdapter.ViewHolder>() {
    private val datas = mutableListOf<LocalMedia>()

    var onDeletePreviewListener: OnDeletePreviewListener? = null
    private val imageEngine = SelectorProviders.getInstance().getConfig().imageEngine

    fun setList(datas: List<LocalMedia>?) {
        this.datas.clear()
        if (datas != null) {
            this.datas.addAll(datas)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPreview: ImageView
        private val ivDelete: ImageView
        private var data: LocalMedia? = null

        init {
            ivPreview = itemView.findViewById<ImageView>(R.id.iv_preview)
            ivDelete = itemView.findViewById<ImageView>(R.id.iv_delete)

            ivDelete.setOnClickListener {
                if (data != null && onDeletePreviewListener != null) {
                    onDeletePreviewListener?.delete(data, adapterPosition)
                }
            }

            ivPreview.background = shapeDrawable {
                cornerRadius = DensityUtil.dip2px(itemView.context, 8f).toFloat()
            }
        }

        fun setData(localMedia: LocalMedia?) {
            if (localMedia == null) return
            this.data = localMedia
            imageEngine?.loadRoundImage(
                itemView.context,
                localMedia.getAvailablePath(),
                DensityUtil.dip2px(itemView.context, 60f),
                DensityUtil.dip2px(itemView.context, 60f),
                ivPreview,
                DensityUtil.dip2px(itemView.context, 8f).toFloat()
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ps_item_main_preview_bottom_bar, parent, false)
        )
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(datas.getOrNull(position))
    }

    interface OnDeletePreviewListener {
        fun delete(localMedia: LocalMedia?, position: Int)
    }
}
