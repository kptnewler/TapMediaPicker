package com.taptap.pick.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.taptap.pick.data.bean.LocalMedia

interface OnMediaItemClickListener {
    fun openCamera()

    fun onItemClick(selectedView: View, position: Int, media: LocalMedia)

    fun onItemLongClick(itemView: View, position: Int, media: LocalMedia)

    fun onSelected(isSelected: Boolean, position: Int, media: LocalMedia): Int

    fun onComplete(isSelected: Boolean, position: Int, media: LocalMedia)
}

interface OnPreviewListener {
    fun onClick(media: LocalMedia)
}

interface OnLongClickListener<T> {
    fun onLongClick(holder: RecyclerView.ViewHolder, position: Int, data: T)
}

interface OnItemClickListener<T> {
    fun onItemClick(position: Int, data: T)
}