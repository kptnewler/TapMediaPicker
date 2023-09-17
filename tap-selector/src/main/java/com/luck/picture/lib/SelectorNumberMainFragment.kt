package com.luck.picture.lib

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.adapter.MainPreviewBottomBarAdapter
import com.luck.picture.lib.adapter.MediaListNewAdapter
import com.luck.picture.lib.adapter.base.BaseMediaListAdapter
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.entity.LocalMedia
import com.tap.intl.lib.intl_widget.widget.button.TapButton

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelector num template style
 */
class SelectorNumberMainFragment : SelectorMainFragment() {
    private lateinit var tvNext: TapButton
    private lateinit var rvPreview: RecyclerView

    private val mainPreviewBottomBarAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MainPreviewBottomBarAdapter().apply {
            onDeletePreviewListener = object : MainPreviewBottomBarAdapter.OnDeletePreviewListener {
                override fun delete(localMedia: LocalMedia?, position: Int) {
                    localMedia?.let {
                        confirmSelect(it, true)
                    }
                }
            }
        }
    }
    override fun getFragmentTag(): String {
        return SelectorNumberMainFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return config.layoutSource[LayoutSource.SELECTOR_NUMBER_MAIN]
            ?: R.layout.ps_fragment_number_selector
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvNext = view.findViewById<TapButton>(R.id.bt_next)
        rvPreview = view.findViewById(R.id.rv_preview)

        initPreviewRecyclerView()

        tvNext.setOnClickListener {
            if (config.showNext) {
                onStartPreview(0, true, getSelectResult())
            } else {
                onCompleteClick(it)
            }
        }

        tvNext.text = if (config.showNext) {
            "${getString(R.string.ps_select_bottom_next)}(${getSelectResult().size})"
        } else {
            getString(R.string.ps_completed)
        }

        if (config.switchToCropPreview) {
            onStartPreview(0, true, getSelectResult())
        }
    }

    private fun initPreviewRecyclerView() {
        rvPreview.adapter = mainPreviewBottomBarAdapter
        rvPreview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun createMediaAdapter(): BaseMediaListAdapter {
        return MediaListNewAdapter()
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
        super.onSelectionResultChange(change)
        // Label the selection order
        val selectResult = getSelectResult()
        if (!selectResult.contains(change)) {
            val currentItem = mAdapter.getData().indexOf(change)
            if (currentItem >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) currentItem + 1 else currentItem)
            }
        }
        selectResult.forEach { media ->
            val position = mAdapter.getData().indexOf(media)
            if (position >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
        }

        if (selectResult.isEmpty()) {
            mBottomNarBar?.visibility = View.GONE
        } else {
            mBottomNarBar?.visibility = View.VISIBLE
        }

        mainPreviewBottomBarAdapter.setList(getSelectResult())

        if (::tvNext.isInitialized) {
            tvNext.text = "${getString(R.string.ps_select_bottom_next)}(${selectResult.size})"
        }
    }
}