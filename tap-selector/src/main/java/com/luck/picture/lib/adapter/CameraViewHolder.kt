package com.luck.picture.lib.adapter

import android.view.View
import android.widget.ImageView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BaseListViewHolder

/**
 * @author：luck
 * @date：2022/11/30 3:29 下午
 * @describe：CameraViewHolder
 */
open class CameraViewHolder(itemView: View) : BaseListViewHolder(itemView) {
    private var tvCamera: ImageView = itemView.findViewById(R.id.tv_camera)
    fun bindData(position: Int) {
        itemView.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener?.openCamera()
            }
        }
    }
}