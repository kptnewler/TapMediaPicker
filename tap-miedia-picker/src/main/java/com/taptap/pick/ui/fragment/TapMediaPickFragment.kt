package com.taptap.pick.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.newler.tap_miedia_picker.R
import com.newler.tap_miedia_picker.databinding.TmpMediaPickFragmentLayoutBinding
import com.tap.intl.lib.intl_widget.ext.gone
import com.tap.intl.lib.intl_widget.ext.visible
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.bean.LocalMediaAlbum
import com.taptap.pick.data.bean.PreviewDataWrap
import com.taptap.pick.data.config.MediaType
import com.taptap.pick.data.config.OnRequestPermissionListener
import com.taptap.pick.data.config.SelectionMode
import com.taptap.pick.data.constant.SelectedState
import com.taptap.pick.data.constant.SelectorConstant
import com.taptap.pick.permissions.PermissionChecker
import com.taptap.pick.providers.TempDataProvider
import com.taptap.pick.ui.adapter.BaseMediaListAdapter
import com.taptap.pick.ui.adapter.MediaListAdapter
import com.taptap.pick.ui.adapter.OnItemClickListener
import com.taptap.pick.ui.adapter.OnMediaItemClickListener
import com.taptap.pick.ui.widget.AlbumListPopWindow
import com.taptap.pick.ui.widget.GridSpacingItemDecoration
import com.taptap.pick.ui.widget.SlideSelectTouchListener
import com.taptap.pick.ui.widget.WrapContentGridLayoutManager
import com.taptap.pick.utils.DensityUtil
import com.taptap.pick.utils.MediaUtils
import com.taptap.pick.utils.SdkVersionUtils
import kotlinx.coroutines.launch
import java.io.File

class TapMediaPickFragment: BaseSelectorFragment() {
    override fun getFragmentTag(): String {
        return TapMediaPickFragment::class.java.simpleName
    }

    private lateinit var binding: TmpMediaPickFragmentLayoutBinding

    override fun getResourceId() = R.layout.tmp_media_pick_fragment_layout

    private val anyLock = Any()

    private var isCameraCallback = false

    lateinit var mAlbumWindow: AlbumListPopWindow
    lateinit var mAdapter: BaseMediaListAdapter

    private var intervalClickTime: Long = 0

    private var mDragSelectTouchListener: SlideSelectTouchListener? = null

    open fun getCurrentAlbum(): LocalMediaAlbum {
        return TempDataProvider.getInstance().currentMediaAlbum
    }

    private fun setCurrentAlbum(album: LocalMediaAlbum) {
        TempDataProvider.getInstance().currentMediaAlbum = album
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TmpMediaPickFragmentLayoutBinding.bind(view)
        onMergeSelectedSource()
        initAlbumWindow()
        initTitleBar()
        initNavbarBar()
        initMediaAdapter()
        checkPermissions()
        registerLiveData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        TempDataProvider.getInstance().albumSource = mAlbumWindow.getAlbumList()
        TempDataProvider.getInstance().mediaSource = mAdapter.getData().toMutableList()
    }

    open fun onMergeSelectedSource() {
        if (config.selectedSource.isNotEmpty()) {
            getSelectResult().addAll(config.selectedSource.toMutableList())
            config.selectedSource.clear()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun registerLiveData() {
        globalViewMode.getEditorLiveData().observe(viewLifecycleOwner) { media ->
            val position = mAdapter.getData().indexOf(media)
            if (position >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
        }
        globalViewMode.getSelectResultLiveData().observe(viewLifecycleOwner) { media ->
            val position = mAdapter.getData().indexOf(media)
            if (checkNotifyStrategy(getSelectResult().indexOf(media) != -1)) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
                Looper.myQueue().addIdleHandler {
                    mAdapter.notifyDataSetChanged()
                    return@addIdleHandler false
                }
            } else {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
            // update selected tag
            mAlbumWindow.notifyChangedSelectTag(getSelectResult())
            onSelectionResultChange(media)
        }
        viewModel.albumLiveData.observe(viewLifecycleOwner) { albumList ->
            onAlbumSourceChange(albumList)
        }
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { mediaList ->
            onMediaSourceChange(mediaList)
        }
    }

    open fun initTitleBar() {
        setDefaultAlbumTitle(getCurrentAlbum().bucketDisplayName)
        binding.btNext.setOnClickListener {
            onBackClick(it)
        }
        binding.tvAlbumTitle.setOnClickListener {
            onShowAlbumWindowAsDropDown()
        }
    }

    open fun onBackClick(v: View) {
        onBackPressed()
    }

    open fun onShowAlbumWindowAsDropDown() {
        if (mAlbumWindow.getAlbumList().isNotEmpty() && !config.isOnlySandboxDir) {
            binding.tvAlbumTitle.let {
                mAlbumWindow.showAsDropDown(it)
            }
        }
    }

    open fun onOriginalClick(v: View) {
        globalViewMode.setOriginalLiveData(!v.isSelected)
    }


    open fun onCompleteClick(v: View) {
        handleSelectResult()
    }


    /**
     * Users can implement a custom album list PopWindow
     */
    open fun createAlbumWindow(): AlbumListPopWindow {
        return AlbumListPopWindow(requireContext())
    }

    open fun initAlbumWindow() {
        mAlbumWindow = createAlbumWindow()
        mAlbumWindow.setOnItemClickListener(object : OnItemClickListener<LocalMediaAlbum> {
            override fun onItemClick(position: Int, data: LocalMediaAlbum) {
                onAlbumItemClick(position, data)
            }
        })
        mAlbumWindow.setOnWindowStatusListener(object : AlbumListPopWindow.OnWindowStatusListener {
            override fun onShowing(isShowing: Boolean) {
            }
        })
    }

    open fun onAlbumItemClick(position: Int, data: LocalMediaAlbum) {
        mAlbumWindow.dismiss()
        // Repeated clicks ignore
        val oldCurrentAlbum = getCurrentAlbum()
        if (data.isEqualAlbum(oldCurrentAlbum.bucketId)) {
            return
        }
        // Cache the current album data before switching to the next album
        mAlbumWindow.getAlbum(oldCurrentAlbum.bucketId)?.let {
            val source = mAdapter.getData().toMutableList()
            if (source.isNotEmpty() && source.first().id == SelectorConstant.INVALID_DATA) {
                // ignore
            } else {
                it.source = source
                it.cachePage = viewModel.page
            }
        }
        // Update current album
        setCurrentAlbum(data)
        mAdapter.setDisplayCamera(isDisplayCamera())
        setDefaultAlbumTitle(data.bucketDisplayName)
        if (data.cachePage > 0 && data.source.isNotEmpty()) {
            // Album already has cached data，Start loading from cached page numbers
            viewModel.page = data.cachePage
            mAdapter.setDataNotifyChanged(data.source)
            binding.rvList.scrollToPosition(0)
        } else {
            // Never loaded, request data again
            viewModel.loadMedia(data.bucketId)
        }
    }


    open fun initNavbarBar() {
        if (config.selectionMode == SelectionMode.ONLY_SINGLE) {
            binding.llBottomOperation.visibility = View.GONE
        }
    }

    open fun checkNotifyStrategy(isAddRemove: Boolean): Boolean {
        var isNotifyAll = false
        if (config.isMaxSelectEnabledMask) {
            val selectResult = getSelectResult()
            val selectCount = selectResult.size
            if (config.isAllWithImageVideo) {
                val maxSelectCount = config.getSelectCount()
                if (config.selectionMode == SelectionMode.MULTIPLE) {
                    isNotifyAll =
                        selectCount == maxSelectCount || (!isAddRemove && selectCount == maxSelectCount - 1)
                }
            } else {
                isNotifyAll = if (selectCount == 0 ||
                    (if (isAddRemove) config.mediaType == MediaType.ALL && selectCount == 1
                    else selectCount == config.totalCount - 1)
                ) {
                    true
                } else {
                    if (MediaUtils.hasMimeTypeOfVideo(selectResult.first().mimeType)) {
                        selectResult.size == config.maxVideoSelectNum
                    } else {
                        selectResult.size == config.totalCount
                    }
                }
            }
        }
        return isNotifyAll
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
        val selectResult = getSelectResult()
        if (selectResult.isEmpty()) {
            binding.rvPreview.gone()
            binding.llBottomOperation.gone()
            return
        }

        binding.rvPreview.visible()
        binding.llBottomOperation.visible()
        binding.btNext.text = "Next(${selectResult.size})"
    }

    /**
     * Users can implement custom RecyclerView related settings
     */
    open fun initRecyclerConfig() {
        binding.rvPreview.also { view ->
            if (view.itemDecorationCount == 0) {
                view.addItemDecoration(
                    GridSpacingItemDecoration(
                        3,
                        DensityUtil.dip2px(requireContext(), 1F), false
                    )
                )
            }
            view.layoutManager =
                WrapContentGridLayoutManager(requireContext(), 3)
            (view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }


    /**
     * Users can implement custom media lists
     */
    open fun createMediaAdapter(): BaseMediaListAdapter {
        return MediaListAdapter()
    }

    @Suppress("UNCHECKED_CAST")
    open fun initMediaAdapter() {
        initRecyclerConfig()
        mAdapter = createMediaAdapter()
        mAdapter.setDisplayCamera(isDisplayCamera())
        binding.rvList.adapter = mAdapter
        onSelectionResultChange(null)

        mAdapter.setOnGetSelectResultListener(object :
            BaseMediaListAdapter.OnGetSelectResultListener {
            override fun onSelectResult(): MutableList<LocalMedia> {
                return getSelectResult()
            }
        })
        mAdapter.setOnItemClickListener(object : OnMediaItemClickListener {
            override fun openCamera() {
                openSelectedCamera()
            }

            override fun onItemClick(selectedView: View, position: Int, media: LocalMedia) {
                onStartPreview(position, false, mAdapter.getData())
            }

            override fun onComplete(isSelected: Boolean, position: Int, media: LocalMedia) {
                if (confirmSelect(media, isSelected) == SelectedState.SUCCESS) {
                    handleSelectResult()
                }
            }

            override fun onItemLongClick(itemView: View, position: Int, media: LocalMedia) {

            }

            override fun onSelected(isSelected: Boolean, position: Int, media: LocalMedia): Int {
                return confirmSelect(media, isSelected)
            }
        })
    }

    open fun isDisplayCamera(): Boolean {
        return config.isDisplayCamera && getCurrentAlbum().isAllAlbum()
                || (config.isOnlySandboxDir && getCurrentAlbum().isSandboxAlbum())
    }

    /**
     *  duplicate media data
     */
    open fun duplicateMediaSource(result: MutableList<LocalMedia>) {
        if (isCameraCallback && getCurrentAlbum().isAllAlbum()) {
            isCameraCallback = false
            synchronized(anyLock) {
                val data = mAdapter.getData()
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val media = iterator.next()
                    if (data.contains(media)) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    open fun checkPermissions() {
        if (PermissionChecker.isCheckReadStorage(requireContext(), config.mediaType)) {
            if (isNeedRestore()) {
                restoreMemoryData()
            } else {
                requestData()
            }
        } else {
            val permissionArray = PermissionChecker.getReadPermissionArray(
                requireContext(),
                config.mediaType
            )
            showPermissionDescription(true, permissionArray)
            val onPermissionApplyListener = config.listenerInfo?.onPermissionApplyListener
            if (onPermissionApplyListener != null) {
                showCustomPermissionApply(permissionArray)
            } else {
                PermissionChecker.requestPermissions(
                    this,
                    permissionArray,
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            showPermissionDescription(false, permissionArray)
                            requestData()
                        }

                        override fun onDenied() {
                            handlePermissionDenied(permissionArray)
                        }
                    })
            }
        }
    }


    override fun showCustomPermissionApply(permission: Array<String>) {
        config.listenerInfo?.onPermissionApplyListener?.requestPermission(
            this,
            permission,
            object : OnRequestPermissionListener {
                override fun onCall(permission: Array<String>, isResult: Boolean) {
                    if (isResult) {
                        showPermissionDescription(false, permission)
                        if (permission.first() == Manifest.permission.CAMERA) {
                            openSelectedCamera()
                        } else {
                            requestData()
                        }
                    } else {
                        handlePermissionDenied(permission)
                    }
                }
            })
    }

    override fun handlePermissionSettingResult(permission: Array<String>) {
        if (permission.isEmpty()) {
            return
        }
        showPermissionDescription(false, permission)
        val isHasCamera = TextUtils.equals(permission[0], PermissionChecker.CAMERA)
        val onPermissionApplyListener = config.listenerInfo?.onPermissionApplyListener
        val isHasPermissions = onPermissionApplyListener?.hasPermissions(this, permission)
            ?: PermissionChecker.checkSelfPermission(requireContext(), permission)
        if (isHasPermissions) {
            if (isHasCamera) {
                openSelectedCamera()
            } else {
                requestData()
            }
        } else {
            if (isHasCamera) {
//                ToastUtils.showMsg(requireContext(), getString(R.string.ps_camera))
            } else {
//                ToastUtils.showMsg(requireContext(), getString(R.string.ps_jurisdiction))
                onBackPressed()
            }
        }
        TempDataProvider.getInstance().currentRequestPermission = arrayOf()
    }


    /**
     * Start requesting media album data
     */
    open fun requestData() {
        if (config.isOnlySandboxDir) {
            val sandboxDir =
                config.sandboxDir ?: throw NullPointerException("config.sandboxDir cannot be empty")
            val dir = File(sandboxDir)
            setDefaultAlbumTitle(dir.name)
            setCurrentAlbum(LocalMediaAlbum().apply {
                this.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
                this.bucketDisplayName = dir.name
            })
            viewModel.loadAppInternalDir(sandboxDir)
        } else {
            onPreloadFakeData()
            Looper.myQueue().addIdleHandler {
                viewModel.loadMediaAlbum()
                viewModel.loadMedia(getCurrentAlbum().bucketId)
                return@addIdleHandler false
            }
        }
    }

    /**
     * set fake data transition
     */
    open fun onPreloadFakeData() {
        if (isSavedInstanceState) {
            return
        }
        val pre = mutableListOf<LocalMedia>()
        for (i in 0 until SelectorConstant.DEFAULT_MAX_PAGE_SIZE) {
            pre.add(LocalMedia().apply {
                this.id = SelectorConstant.INVALID_DATA
            })
        }
        mAdapter.setDataNotifyChanged(pre)
    }

    /**
     * Restore data after system recycling
     */
    open fun restoreMemoryData() {
        val albumSource = TempDataProvider.getInstance().albumSource.toMutableList()
        onAlbumSourceChange(albumSource)
        val mediaSource = TempDataProvider.getInstance().mediaSource.toMutableList()
        onMediaSourceChange(mediaSource)
        TempDataProvider.getInstance().albumSource.clear()
        TempDataProvider.getInstance().mediaSource.clear()
        if (config.isOnlySandboxDir) {
            val sandboxDir =
                config.sandboxDir ?: throw NullPointerException("config.sandboxDir cannot be empty")
            val dir = File(sandboxDir)
            setDefaultAlbumTitle(dir.name)
            setCurrentAlbum(LocalMediaAlbum().apply {
                this.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
                this.bucketDisplayName = dir.name
            })
        }
    }

    /**
     * Restore Memory Data
     */
    open fun isNeedRestore(): Boolean {
        return isSavedInstanceState
    }

    /**
     * Changes in album data
     * @param albumList album data
     */
    open fun onAlbumSourceChange(albumList: MutableList<LocalMediaAlbum>) {
        if (albumList.isNotEmpty()) {
            setCurrentAlbum(albumList.first())
            albumList.forEach { album ->
                if (album.bucketId == getCurrentAlbum().bucketId) {
                    album.isSelected = true
                    return@forEach
                }
            }
            mAlbumWindow.setAlbumList(albumList)
            mAlbumWindow.notifyChangedSelectTag(getSelectResult())
        }
    }

    /**
     * Changes in media data
     * @param result media data
     */
    open fun onMediaSourceChange(result: MutableList<LocalMedia>) {
        duplicateMediaSource(result)
        if (viewModel.page == 1) {
            mAdapter.setDataNotifyChanged(result.toMutableList())
            binding.rvList.scrollToPosition(0)
        } else {
            mAdapter.addAllDataNotifyChanged(result)
        }
    }

    /**
     * Users can override this method to configure custom previews
     *
     * @param position Preview start position
     * @param isBottomPreview Preview source from bottom
     * @param source Preview Data Source
     */
    open fun onStartPreview(
        position: Int,
        isBottomPreview: Boolean,
        source: MutableList<LocalMedia>
    ) {
        config.previewWrap =
            onWrapPreviewData(viewModel.page, position, isBottomPreview, source)
        val factory = ClassFactory.NewInstance()
        val registry = config.registry
        val instance = factory.create(registry.get(newPreviewInstance()))
        val fragmentTag = instance.getFragmentTag()
        FragmentInjectManager.injectSystemRoomFragment(requireActivity(), fragmentTag, instance)
    }

    /**
     * Users can override this method to configure custom previews fragments
     */
    @Suppress("UNCHECKED_CAST")
    open fun <F : SelectorPreviewFragment> newPreviewInstance(): Class<F> {
        return SelectorPreviewFragment::class.java as Class<F>
    }

    /**
     * Preview data wrap
     * @param page current request page
     * @param position Preview start position
     * @param isBottomPreview Preview source from bottom
     * @param source Preview Data Source
     */
    open fun onWrapPreviewData(
        page: Int,
        position: Int,
        isBottomPreview: Boolean,
        source: MutableList<LocalMedia>
    ): PreviewDataWrap {
        return PreviewDataWrap().apply {
            this.page = page
            this.position = position
            this.bucketId = getCurrentAlbum().bucketId
            this.isBottomPreview = isBottomPreview
            this.isDisplayCamera = mAdapter.isDisplayCamera()
            if (config.isOnlySandboxDir) {
                this.totalCount = source.size
            } else {
                this.totalCount = if (isBottomPreview) source.size else getCurrentAlbum().totalCount
            }
            this.source = source.toMutableList()
        }
    }

    override fun onMergeCameraResult(media: LocalMedia?) {
        if (media != null) {
            isCameraCallback = true
            onCheckDuplicateMedia(media)
            onMergeCameraAlbum(media)
            onMergeCameraMedia(media)
        }
    }

    /**
     * Some models may generate two duplicate photos when taking photos
     */
    open fun onCheckDuplicateMedia(media: LocalMedia) {
        if (SdkVersionUtils.isQ()) {
        } else {
            if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                viewModel.viewModelScope.launch {
                    media.absolutePath?.let {
                        File(it).parent?.let { parent ->
                            val context = requireContext()
                            val id = MediaUtils.getDCIMLastId(context, parent)
                            if (id != -1L) {
                                MediaUtils.remove(context, id)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Merge media data generated by the camera into the album
     */
    open fun onMergeCameraAlbum(media: LocalMedia) {
        // merge album list
        val allMediaAlbum =
            mAlbumWindow.getAlbum(SelectorConstant.DEFAULT_ALL_BUCKET_ID) ?: LocalMediaAlbum()
        allMediaAlbum.bucketId = SelectorConstant.DEFAULT_ALL_BUCKET_ID
        val bucketDisplayName = config.defaultAlbumName
        allMediaAlbum.bucketDisplayName = bucketDisplayName
        allMediaAlbum.bucketDisplayCover = media.path
        allMediaAlbum.bucketDisplayMimeType = media.mimeType
        allMediaAlbum.source.add(0, media)
        allMediaAlbum.totalCount += 1
        val cameraMediaAlbum = mAlbumWindow.getAlbum(media.bucketId) ?: LocalMediaAlbum()
        cameraMediaAlbum.bucketId = media.bucketId
        cameraMediaAlbum.bucketDisplayName = media.bucketDisplayName
        cameraMediaAlbum.bucketDisplayCover = media.path
        cameraMediaAlbum.bucketDisplayMimeType = media.mimeType
        cameraMediaAlbum.source.add(0, media)
        cameraMediaAlbum.totalCount += 1

        if (mAlbumWindow.getAlbumList().isEmpty()) {
            val albumList = mutableListOf<LocalMediaAlbum>()
            albumList.add(0, allMediaAlbum)
            albumList.add(cameraMediaAlbum)
            albumList.first().isSelected = true
            setCurrentAlbum(albumList.first())
            mAlbumWindow.setAlbumList(albumList)
        } else {
            val cameraAlbum = mAlbumWindow.getAlbum(cameraMediaAlbum.bucketId)
            if (cameraAlbum == null) {
                mAlbumWindow.getAlbumList().add(cameraMediaAlbum)
            }
        }
        mAlbumWindow.notifyItemRangeChanged()
    }

    /**
     * Merge camera generated media data into a list
     */
    open fun onMergeCameraMedia(media: LocalMedia) {
        requireActivity().runOnUiThread {
            mAdapter.getData().add(0, media)
            confirmSelect(media, false)
            val position = if (mAdapter.isDisplayCamera()) 1 else 0
            mAdapter.notifyItemInserted(position)
            mAdapter.notifyItemRangeChanged(position, mAdapter.getData().size)
        }
    }


    override fun onDestroy() {
        mDragSelectTouchListener?.stopAutoScroll()
        super.onDestroy()
    }
}