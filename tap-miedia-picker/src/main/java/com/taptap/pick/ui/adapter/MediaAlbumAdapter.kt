package com.taptap.pick.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taptap.pick.data.bean.LocalMediaAlbum
import com.taptap.pick.data.config.PickerConfig
import com.taptap.pick.utils.DensityUtil
import com.newler.tap_miedia_picker.R
import com.newler.tap_miedia_picker.databinding.TmpMediaAlbumItemLayoutBinding
import info.hellovass.kdrawable.shapeDrawable

/**
 * @author：luck
 * @date：2022/12/13 11:35 上午
 * @describe：MediaAlbumAdapter
 */
open class MediaAlbumAdapter(var config: PickerConfig) :
    RecyclerView.Adapter<MediaAlbumAdapter.ViewHolder>() {
    private var albumList = mutableListOf<LocalMediaAlbum>()
    private var albumMap = mutableMapOf<Long, LocalMediaAlbum>()
    private var lastSelectPosition = 0

    fun setAlbumList(albumList: MutableList<LocalMediaAlbum>) {
        this.albumList.addAll(albumList)
        this.albumList.forEach { mediaAlbum ->
            this.albumMap[mediaAlbum.bucketId] = mediaAlbum
        }
        this.notifyItemRangeChanged(0, this.albumList.size)
    }

    fun getAlbumList(): MutableList<LocalMediaAlbum> {
        return albumList
    }

    fun getAlbum(bucketId: Long): LocalMediaAlbum? {
        return albumMap[bucketId]
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.tmp_media_album_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        albumList.getOrNull(position)?.let {
            holder.setData(it)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TmpMediaAlbumItemLayoutBinding.bind(itemView)

        private var data: LocalMediaAlbum? = null

        init {
            binding.ivAlbumCover.background = shapeDrawable {
                cornerRadius = DensityUtil.dip2px(itemView.context, 8f).toFloat()
            }

            itemView.setOnClickListener {
                if (itemCount > lastSelectPosition) {
                    albumList[lastSelectPosition].isSelected = false
                }
                notifyItemChanged(lastSelectPosition)
                data?.let {
                    data?.isSelected = true
                    lastSelectPosition = adapterPosition
                    notifyItemChanged(lastSelectPosition)
                    mItemClickListener?.onItemClick(adapterPosition, it)
                }
            }
        }

        fun setData(mediaAlbum: LocalMediaAlbum) {
            this.data = mediaAlbum
            config.imageEngine?.loadAlbumCover(
                itemView.context,
                mediaAlbum.bucketDisplayCover,
                binding.ivAlbumCover
            )

            binding.tvAlbumName.text = mediaAlbum.bucketDisplayName
            binding.tvAlbumCount.text = mediaAlbum.totalCount.toString()
        }
    }

    private var mItemClickListener: OnItemClickListener<LocalMediaAlbum>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<LocalMediaAlbum>?) {
        this.mItemClickListener = listener
    }

}