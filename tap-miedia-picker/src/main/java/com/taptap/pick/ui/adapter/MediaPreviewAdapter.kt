package com.taptap.pick.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newler.tap_miedia_picker.R
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.constant.MediaAdapterType
import com.taptap.pick.providers.SelectorProviders
import com.taptap.pick.ui.adapter.viewholder.BasePreviewMediaHolder

/**
 * @author：luck
 * @date：2023/1/4 4:58 下午
 * @describe：MediaPreviewAdapter
 */
open class MediaPreviewAdapter : RecyclerView.Adapter<BasePreviewMediaHolder>() {
    private lateinit var mData: MutableList<LocalMedia>
    private val config = SelectorProviders.getConfig()
    private val mViewHolderCache = LinkedHashMap<Int, BasePreviewMediaHolder>()
    private var isFirstAttachedToWindow = false
    open fun getCurrentViewHolder(position: Int): BasePreviewMediaHolder? {
        return mViewHolderCache[position]
    }

    fun getData(): MutableList<LocalMedia> {
        return mData
    }

    fun setDataNotifyChanged(data: MutableList<LocalMedia>) {
        this.mData = data
        this.notifyItemRangeChanged(0, mData.size)
    }

    /**
     * User can rewrite to realize user-defined requirements
     */
    open fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): BasePreviewMediaHolder {
        val resource = R.layout.tmp_preview_image_layout

        val itemView = inflater.inflate(resource, parent, false)
        return PreviewImageHolder(itemView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePreviewMediaHolder {
        val inflater = LayoutInflater.from(parent.context)
        return onCreateImageViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: BasePreviewMediaHolder, position: Int) {
        mViewHolderCache[position] = holder
        holder.onPreviewListener = onPreviewListener
        holder.setOnLongClickListener(mLongClickListener)
        holder.bindData(mData[position], position)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        val mimeType = mData[position].mimeType
        return MediaAdapterType.TYPE_IMAGE
    }

    override fun onViewAttachedToWindow(holder: BasePreviewMediaHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
        if (!isFirstAttachedToWindow) {
            onAttachedToWindowListener?.onViewAttachedToWindow(holder)
            isFirstAttachedToWindow = true
        }
    }

    override fun onViewDetachedFromWindow(holder: BasePreviewMediaHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    private var onAttachedToWindowListener: OnAttachedToWindowListener? = null

    fun setOnFirstAttachedToWindowListener(l: OnAttachedToWindowListener) {
        this.onAttachedToWindowListener = l
    }

    interface OnAttachedToWindowListener {
        fun onViewAttachedToWindow(holder: BasePreviewMediaHolder)
    }

    private var mClickListener: OnClickListener? = null

    fun setOnClickListener(l: OnClickListener?) {
        this.mClickListener = l
    }

    interface OnClickListener {
        fun onClick(media: LocalMedia)
    }

    private var mLongClickListener: OnLongClickListener<LocalMedia>? = null

    fun setOnLongClickListener(l: OnLongClickListener<LocalMedia>?) {
        this.mLongClickListener = l
    }

    var onPreviewListener: OnPreviewListener? = null

    open fun destroy() {
        for (key in mViewHolderCache.keys) {
            mViewHolderCache[key]?.release()
        }
    }
}