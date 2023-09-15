package com.taptap.pick.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import com.newler.tap_miedia_picker.R

open class CameraViewHolder(itemView: View) : BaseListViewHolder(itemView) {
    private var tvCamera: ImageView = itemView.findViewById(R.id.iv_camera)
    fun bindData(position: Int) {
        itemView.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener?.openCamera()
            }
        }

        itemView.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener?.openCamera()
            }
        }

    }
}