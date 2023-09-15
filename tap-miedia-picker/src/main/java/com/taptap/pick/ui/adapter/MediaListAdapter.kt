package com.taptap.pick.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.ui.adapter.viewholder.BaseListViewHolder
import com.taptap.pick.ui.adapter.viewholder.CameraViewHolder
import com.taptap.pick.ui.adapter.viewholder.ListMediaViewHolder
import com.newler.tap_miedia_picker.R

class MediaListAdapter: BaseMediaListAdapter() {
    override fun onCreateCameraViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BaseListViewHolder {
        return CameraViewHolder(inflater.inflate(R.layout.tmp_media_camera_item_layout, parent, false))
    }

    override fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListMediaViewHolder {
        return ListMediaViewHolder(inflater.inflate(R.layout.tmp_media_list_item_layout, parent, false))
    }

    override fun bindData(holder: ListMediaViewHolder, media: LocalMedia, position: Int) {
        super.bindData(holder, media, position)
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

//    override fun onCreateVideoViewHolder(
//        inflater: LayoutInflater,
//        parent: ViewGroup
//    ): ListMediaViewHolder {
//    }
}