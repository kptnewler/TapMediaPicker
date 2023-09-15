package com.taptap.pick.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.taptap.pick.providers.SelectorProviders
import com.taptap.pick.ui.adapter.BaseMediaListAdapter
import com.taptap.pick.ui.adapter.OnMediaItemClickListener

open class BaseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var config = SelectorProviders.getConfig()

    var mItemClickListener: OnMediaItemClickListener? = null

    fun setOnItemClickListener(listener: OnMediaItemClickListener?) {
        this.mItemClickListener = listener
    }

    var mGetSelectResultListener: BaseMediaListAdapter.OnGetSelectResultListener? = null

    fun setOnGetSelectResultListener(listener: BaseMediaListAdapter.OnGetSelectResultListener?) {
        this.mGetSelectResultListener = listener
    }
}