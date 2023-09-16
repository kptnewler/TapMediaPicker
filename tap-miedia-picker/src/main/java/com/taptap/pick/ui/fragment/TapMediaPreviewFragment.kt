package com.taptap.pick.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.newler.tap_miedia_picker.R
import com.newler.tap_miedia_picker.databinding.TmpMediaPreviewFragmentLayoutBinding
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.bean.LocalMediaAlbum
import com.taptap.pick.data.bean.PreviewDataWrap
import com.taptap.pick.data.config.SelectionMode
import com.taptap.pick.data.constant.CropWrap
import com.taptap.pick.data.constant.SelectedState
import com.taptap.pick.data.constant.SelectorConstant
import com.taptap.pick.providers.TempDataProvider
import com.taptap.pick.ui.adapter.MediaPreviewAdapter
import com.taptap.pick.ui.adapter.viewholder.BasePreviewMediaHolder
import com.taptap.pick.utils.DensityUtil
import com.taptap.pick.utils.MediaUtils
import info.hellovass.kdrawable.shape.KShape
import info.hellovass.kdrawable.shapeDrawable
import info.hellovass.kdrawable.stateListDrawable

open class TapMediaPreviewFragment : BaseSelectorFragment() {
    private val isEditor: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getBoolean(PARAMS_IS_EDITOR) ?: false
    }

    companion object {
        private const val PARAMS_IS_EDITOR = "params_is_editor"
        fun newInstance(isEditor: Boolean): TapMediaPreviewFragment {
            return TapMediaPreviewFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(PARAMS_IS_EDITOR, isEditor)
                }
            }
        }
    }

    override fun getFragmentTag(): String {
        return TapMediaPreviewFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return R.layout.tmp_media_preview_fragment_layout
    }

    var screenWidth = 0
    var screenHeight = 0

    private lateinit var mAdapter: MediaPreviewAdapter

    private lateinit var binding: TmpMediaPreviewFragmentLayoutBinding


    open fun getCurrentAlbum(): LocalMediaAlbum {
        return TempDataProvider.getInstance().currentMediaAlbum
    }

    open fun getPreviewWrap(): PreviewDataWrap {
        return TempDataProvider.getInstance().previewWrap
    }

    private fun setPreviewWrap(previewWrap: PreviewDataWrap) {
        TempDataProvider.getInstance().previewWrap = previewWrap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TmpMediaPreviewFragmentLayoutBinding.bind(view)
        screenWidth = DensityUtil.getRealScreenWidth(requireContext())
        screenHeight = DensityUtil.getScreenHeight(requireContext())
        attachPreview()
        initTitleBar()
        initNavbarBar()
        initViewPagerData()
        registerLiveData()
    }

    private fun initView() {
        binding.tvPickIndex.background = stateListDrawable {
            selectedState {
                shapeDrawable {
                    shape = KShape.Oval
                    solidColor = ContextCompat.getColor(requireContext(), com.tap.intl.lib.intl_widget.R.color.green_primary)
                }
            }

            unselectedState {
                shapeDrawable {
                    solidColor = ContextCompat.getColor(requireContext(), com.tap.intl.lib.intl_widget.R.color.black_opacity60)
                    shape = KShape.Oval
                    stroke {
                        width = DensityUtil.dip2px(requireContext(), 3f)
                        color = ContextCompat.getColor(requireContext(), com.tap.intl.lib.intl_widget.R.color.white_primary)
                    }
                }
            }
        }
    }

    private fun registerLiveData() {
        globalViewMode.getSelectResultLiveData().observe(viewLifecycleOwner) { change ->
            onSelectionResultChange(change)
        }
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { result ->
            onMediaSourceChange(result)
        }
    }

    open fun attachPreview() {
        if (config.previewWrap.source.isNotEmpty()) {
            setPreviewWrap(config.previewWrap.copy())
            viewModel.page = getPreviewWrap().page
            config.previewWrap.source.clear()
        }
    }


    open fun initTitleBar() {
        binding.ivBack.setOnClickListener {
            onBackClick(it)
        }
        binding.tvPickIndex.setOnClickListener {
            onSelectedClick(it)
        }
    }

    open fun initNavbarBar() {
        val media = getPreviewWrap().source[getPreviewWrap().position]
        binding.ivCrop.visibility = if (!MediaUtils.hasMimeTypeOfAudio(media.mimeType) && isEditor) View.VISIBLE else View.GONE
        binding.ivCrop.setOnClickListener {
            onEditorClick(it)
        }
        binding.btDone.setOnClickListener {
            onCompleteClick(it)
        }
    }


    open fun onBackClick(v: View) {
        onBackPressed()
    }

    open fun onSelectedClick(v: View) {
        val media = getPreviewWrap().source[binding.vpList.currentItem]
        val resultCode =
            confirmSelect(media, v.isSelected)
        if (resultCode == SelectedState.INVALID) {
            return
        }
        val isSelected = resultCode == SelectedState.SUCCESS
        v.isSelected = isSelected
        binding.tvPickIndex.text = if (isSelected) getSelectResult().size.toString() else ""
        if (config.selectionMode == SelectionMode.ONLY_SINGLE) {
            handleSelectResult()
        }
    }

    open fun onFirstViewAttachedToWindow(holder: BasePreviewMediaHolder) {
        if (isSavedInstanceState) {
            return
        }
    }

    open fun onCompleteClick(v: View) {
        handleSelectResult()
    }

    open fun onEditorClick(v: View) {
        config.listenerInfo?.onEditorMediaListener?.onEditorMedia(
            this,
            getPreviewWrap().source[binding.vpList.currentItem],
            SelectorConstant.REQUEST_EDITOR_CROP
        )
    }

    open fun onPreviewItemClick(media: LocalMedia) {
        onBackPressed()
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
    }

    override fun onKeyBackAction() {
        onBackPressed()
    }


    /**
     * Users can implement custom preview adapter
     */
    open fun createMediaAdapter(): MediaPreviewAdapter {
        return MediaPreviewAdapter()
    }

    open fun initViewPagerData() {
        mAdapter = createMediaAdapter()
        mAdapter.setDataNotifyChanged(getPreviewWrap().source)
        with(binding.vpList) {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = mAdapter
            val context = requireContext()
            val marginPageTransformer = MarginPageTransformer(DensityUtil.dip2px(context, 3F))
            setPageTransformer(marginPageTransformer)
            registerOnPageChangeCallback(pageChangeCallback)
            setCurrentItem(getPreviewWrap().position, false)
            onSelectionResultChange(null)
            mAdapter.setOnFirstAttachedToWindowListener(object :
                MediaPreviewAdapter.OnAttachedToWindowListener {
                override fun onViewAttachedToWindow(holder: BasePreviewMediaHolder) {
                    onFirstViewAttachedToWindow(holder)
                }
            })
            mAdapter.setOnClickListener(object : MediaPreviewAdapter.OnClickListener {
                override fun onClick(media: LocalMedia) {
                    onPreviewItemClick(media)
                }
            })
        }
    }

    open fun onMediaSourceChange(result: MutableList<LocalMedia>) {
        val oldStartPosition: Int = getPreviewWrap().source.size
        getPreviewWrap().source.addAll(result.toMutableList())
        val itemCount: Int = getPreviewWrap().source.size
        mAdapter.notifyItemRangeChanged(oldStartPosition, itemCount)
    }

    private val pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                onViewPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                onViewPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onViewPageSelected(position)
            }
        }

    /**
     * Called when the scroll state changes. Useful for discovering when the user begins dragging,
     * when a fake drag is started, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle. state can be one of SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING or SCROLL_STATE_SETTLING.
     */
    open fun onViewPageScrollStateChanged(state: Int) {

    }

    /**
     * This method will be invoked when the current page is scrolled, either as part of a programmatically initiated smooth scroll or a user initiated touch scroll.
     * Params:
     * position – Position index of the first page currently being displayed. Page position+1 will be visible if positionOffset is nonzero.
     * positionOffset – Value from [0, 1) indicating the offset from the page at position.
     * positionOffsetPixels – Value in pixels indicating the offset from position.
     */
    open fun onViewPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        if (getPreviewWrap().source.size > position) {
            val currentMedia: LocalMedia =
                if (positionOffsetPixels < screenWidth / 2) getPreviewWrap().source[position]
                else getPreviewWrap().source[position + 1]
            binding.tvPickIndex.isSelected =
                getSelectResult().contains(currentMedia)
        }
    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not necessarily complete.
     * Params:
     * position – Position index of the new selected page.
     */
    open fun onViewPageSelected(position: Int) {
        getPreviewWrap().position = position
        if (isLoadMoreThreshold(position)) {
            loadMediaMore()
        }
    }

    /**
     * Load more thresholds
     */
    open fun isLoadMoreThreshold(position: Int): Boolean {
        if (getCurrentAlbum().totalCount == mAdapter.getData().size) {
            return false
        }
        if (!getPreviewWrap().isBottomPreview && !config.isOnlySandboxDir && !getPreviewWrap().isExternalPreview) {
            return position == (mAdapter.itemCount - 1) - 10 || position == mAdapter.itemCount - 1
        }
        return false
    }

    /**
     * Load more
     */
    open fun loadMediaMore() {
        viewModel.loadMediaMore(getPreviewWrap().bucketId)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectorConstant.REQUEST_EDITOR_CROP) {
                onMergeEditorData(data)
            }
        }
    }

    open fun onMergeEditorData(data: Intent?) {
        val media = getPreviewWrap().source[binding.vpList.currentItem]
        val outputUri = if (data?.hasExtra(CropWrap.CROP_OUTPUT_URI) == true) {
            data.getParcelableExtra(CropWrap.CROP_OUTPUT_URI)
        } else {
            data?.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        }
        media.cropWidth = data?.getIntExtra(CropWrap.CROP_IMAGE_WIDTH, 0) ?: 0
        media.cropHeight = data?.getIntExtra(CropWrap.CROP_IMAGE_HEIGHT, 0) ?: 0
        media.cropOffsetX = data?.getIntExtra(CropWrap.CROP_OFFSET_X, 0) ?: 0
        media.cropOffsetY = data?.getIntExtra(CropWrap.CROP_OFFSET_Y, 0) ?: 0
        media.cropAspectRatio = data?.getFloatExtra(CropWrap.CROP_ASPECT_RATIO, 0F) ?: 0F
        media.editorPath = if (MediaUtils.isContent(outputUri.toString())) {
            outputUri.toString()
        } else {
            outputUri?.path
        }
        media.editorData = data?.getStringExtra(CropWrap.DEFAULT_EXTRA_DATA)
//        if (!getSelectResult().contains(media)) {
//            mTvSelected?.performClick()
//        }
        mAdapter.notifyItemChanged(binding.vpList.currentItem)
        globalViewMode.setEditorLiveData(media)
    }


    override fun onDestroy() {
        mAdapter.destroy()
        binding.vpList.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}
