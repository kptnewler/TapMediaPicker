package com.taptap.pick.ui.widget

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.SimpleItemAnimator
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.bean.LocalMediaAlbum
import com.taptap.pick.providers.SelectorProviders
import com.taptap.pick.ui.adapter.MediaAlbumAdapter
import com.taptap.pick.ui.adapter.OnItemClickListener
import com.taptap.pick.utils.DensityUtil
import com.taptap.pick.utils.SdkVersionUtils.isMinM
import com.taptap.pick.utils.SdkVersionUtils.isN
import com.newler.tap_miedia_picker.R
import com.newler.tap_miedia_picker.databinding.TmpMediaAlbumWindowBinding

open class AlbumListPopWindow(context: Context) : PopupWindow() {
    val config = SelectorProviders.getConfig()
    lateinit var mediaAlbumAdapter: MediaAlbumAdapter
    var defaultMaxCount = 10
    var isExecuteDismiss = false

    private val binding = TmpMediaAlbumWindowBinding.inflate(LayoutInflater.from(context))

    init {
        this.contentView = binding.rootView
        this.initViews(contentView)
        this.width = RelativeLayout.LayoutParams.MATCH_PARENT
        this.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        this.animationStyle = 0
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.update()
        binding.rootView.setOnClickListener {
            if (isMinM()) {
                dismiss()
            }
        }
        binding.viewMask.setOnClickListener {
            dismiss()
        }
        this.initRecyclerView()
    }

    open fun initViews(contentView: View) {

    }

    open fun initRecyclerView() {
        with(binding.albumList) {
            layoutManager = WrapContentLinearLayoutManager(context)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            itemAnimator = null
            mediaAlbumAdapter = MediaAlbumAdapter(config)
            adapter = mediaAlbumAdapter
        }
    }

    open fun setAlbumList(albumList: MutableList<LocalMediaAlbum>) {
        mediaAlbumAdapter.setAlbumList(albumList)
        val windowMaxHeight = (DensityUtil.getScreenHeight(binding.rootView.context) * 0.6).toInt()
        val layoutParams = binding.albumList.layoutParams
        layoutParams.height =
            if (albumList.size > defaultMaxCount) windowMaxHeight else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    fun notifyItemRangeChanged() {
        mediaAlbumAdapter.notifyItemRangeChanged(0, this.mediaAlbumAdapter.itemCount)
    }

    fun getAlbumList(): MutableList<LocalMediaAlbum> {
        return mediaAlbumAdapter.getAlbumList()
    }

    fun getAlbum(bucketId: Long): LocalMediaAlbum? {
        return mediaAlbumAdapter.getAlbum(bucketId)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<LocalMediaAlbum>?) {
        this.mediaAlbumAdapter.setOnItemClickListener(listener)
    }

    open fun notifyChangedSelectTag(result: MutableList<LocalMedia>) {
        val albumList = mediaAlbumAdapter.getAlbumList()
        for (i in albumList.indices) {
            val mediaAlbum = albumList[i]
            mediaAlbum.isSelectedTag = false
            mediaAlbumAdapter.notifyItemChanged(i)
            for (j in 0 until result.size) {
                val media = result[j]
                if (TextUtils.equals(mediaAlbum.bucketDisplayName, media.bucketDisplayName)
                    || mediaAlbum.isAllAlbum()
                ) {
                    mediaAlbum.isSelectedTag = true
                    mediaAlbumAdapter.notifyItemChanged(i)
                    break
                }
            }
        }
    }

    override fun showAsDropDown(anchor: View) {
        if (isN()) {
            val location = IntArray(2)
            anchor.getLocationInWindow(location)
            showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] + anchor.height)
        } else {
            super.showAsDropDown(anchor)
        }
        isExecuteDismiss = false
        windowStatusListener?.onShowing(true)
        binding.albumList.startAnimation(showAnimation(anchor.context))
        binding.albumList.animate().alpha(1F).setDuration(binding.albumList.animation.duration).start()
    }

    override fun dismiss() {
        if (isExecuteDismiss) {
            return
        }
        isExecuteDismiss = true
        windowStatusListener?.onShowing(false)
        binding.albumList.startAnimation(hideAnimation(binding.albumList.context))
        binding.viewMask.animate().alpha(0F).setDuration(binding.albumList.animation.duration).start()
        binding.albumList.postDelayed({
            super.dismiss()
            isExecuteDismiss = false
        }, binding.albumList.animation.duration)
    }

    open fun showAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.tmp_anim_album_show)
    }

    open fun hideAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.tmp_anim_album_dismiss)
    }

    private var windowStatusListener: OnWindowStatusListener? = null

    fun setOnWindowStatusListener(listener: OnWindowStatusListener) {
        this.windowStatusListener = listener
    }

    interface OnWindowStatusListener {
        fun onShowing(isShowing: Boolean)
    }
}