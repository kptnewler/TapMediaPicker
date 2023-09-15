package com.taptap.pick.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.constant.MediaAdapterType
import com.taptap.pick.providers.SelectorProviders
import com.taptap.pick.ui.adapter.viewholder.CameraViewHolder
import com.taptap.pick.ui.adapter.viewholder.ListMediaViewHolder
import com.taptap.pick.ui.adapter.viewholder.BaseListViewHolder
/**
 * @author：luck
 * @date：2022/12/1 1:18 下午
 * @describe：BaseMediaListAdapter
 */
abstract class BaseMediaListAdapter :
    RecyclerView.Adapter<BaseListViewHolder>() {
    val config = SelectorProviders.getConfig()
    private var mData = mutableListOf<LocalMedia>()

    fun getData(): MutableList<LocalMedia> {
        return mData
    }

    private var isDisplayCamera: Boolean = false

    open fun isDisplayCamera(): Boolean {
        return isDisplayCamera
    }

    fun setDisplayCamera(displayCamera: Boolean) {
        this.isDisplayCamera = displayCamera
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataNotifyChanged(data: MutableList<LocalMedia>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun addAllDataNotifyChanged(data: MutableList<LocalMedia>) {
        val positionStart = mData.size
        mData.addAll(data)
        val itemCount = mData.size
        notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder: BaseListViewHolder = when (viewType) {
            MediaAdapterType.TYPE_CAMERA -> onCreateCameraViewHolder(inflater, parent)
            else -> onCreateImageViewHolder(inflater, parent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseListViewHolder, position: Int) {
        holder.setOnItemClickListener(mItemClickListener)
        holder.setOnGetSelectResultListener(mGetSelectResultListener)
        if (getItemViewType(position) == MediaAdapterType.TYPE_CAMERA) {
            (holder as? CameraViewHolder)?.bindData(position)
        } else {
            val adapterPosition = if (isDisplayCamera) position - 1 else position
            bindData(holder as ListMediaViewHolder, mData[adapterPosition], adapterPosition)
        }
    }

    open fun bindData(holder: ListMediaViewHolder, media: LocalMedia, position: Int) {
        holder.bindData(mData[position], position)
    }

    protected abstract fun onCreateCameraViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BaseListViewHolder

    protected abstract fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListMediaViewHolder

//    protected abstract fun onCreateVideoViewHolder(
//        inflater: LayoutInflater,
//        parent: ViewGroup
//    ): ListMediaViewHolder


    override fun getItemViewType(position: Int): Int {
        return if (isDisplayCamera && position == 0) {
            MediaAdapterType.TYPE_CAMERA
        } else {
            val adapterPosition = if (isDisplayCamera) position - 1 else position
            val mimeType = mData[adapterPosition].mimeType
            MediaAdapterType.TYPE_IMAGE
        }
    }

    override fun getItemCount(): Int {
        return if (isDisplayCamera) mData.size + 1 else mData.size
    }

    var mItemClickListener: OnMediaItemClickListener? = null

    fun setOnItemClickListener(listener: OnMediaItemClickListener?) {
        this.mItemClickListener = listener
    }

    var mGetSelectResultListener: OnGetSelectResultListener? = null

    fun setOnGetSelectResultListener(listener: OnGetSelectResultListener?) {
        this.mGetSelectResultListener = listener
    }

    interface OnGetSelectResultListener {
        fun onSelectResult(): MutableList<LocalMedia>
    }
}