package com.taptap.pick.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.taptap.pick.app.SelectorAppMaster
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.config.MediaType
import com.taptap.pick.data.config.OnCallbackListener
import com.taptap.pick.data.config.SelectionMode
import com.taptap.pick.data.constant.CropWrap
import com.taptap.pick.data.constant.SelectedState
import com.taptap.pick.data.constant.SelectorConstant
import com.taptap.pick.loader.service.ScanListener
import com.taptap.pick.permissions.PermissionChecker
import com.taptap.pick.permissions.PermissionUtil
import com.taptap.pick.providers.SelectorProviders
import com.taptap.pick.providers.TempDataProvider
import com.taptap.pick.ui.viewmodel.GlobalViewModel
import com.taptap.pick.ui.viewmodel.SelectorViewModel
import com.taptap.pick.utils.ActivityCompatHelper
import com.taptap.pick.utils.CameraUtils
import com.taptap.pick.utils.FileUtils
import com.taptap.pick.utils.MediaStoreUtils
import com.taptap.pick.utils.MediaUtils
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：BaseSelectorFragment
 */
abstract class BaseSelectorFragment : Fragment() {

    abstract fun getFragmentTag(): String
    abstract fun getResourceId(): Int
    open fun isNormalDefaultEnter(): Boolean {
        return false
    }

    /**
     * Permission custom apply
     */
    open fun showCustomPermissionApply(permission: Array<String>) {}

    /**
     * Jump to the permission setting interface processing results
     */
    open fun handlePermissionSettingResult(permission: Array<String>) {}

    /**
     * Changes in selection results
     * @param change，If [change] is empty refresh all
     */
    open fun onSelectionResultChange(change: LocalMedia?) {}

    /**
     * tipsDialog
     */
    private var tipsDialog: Dialog? = null

    protected val config = SelectorProviders.getConfig()

    protected var isSavedInstanceState = false

    protected val viewModel by lazy {
        val activity = requireActivity()
        val savedStateViewModelFactory = SavedStateViewModelFactory(activity.application, this)
        return@lazy ViewModelProvider(
            this,
            savedStateViewModelFactory
        )[SelectorViewModel::class.java]
    }

    protected val globalViewMode by lazy {
        val activity = requireActivity()
        val savedStateViewModelFactory = SavedStateViewModelFactory(activity.application, activity)
        return@lazy ViewModelProvider(
            activity,
            savedStateViewModelFactory
        )[GlobalViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getResourceId(), container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSavedInstanceState = savedInstanceState != null
        viewModel.onRestoreInstanceState(savedInstanceState)
        restoreEngine()
        setFragmentKeyBackListener()
    }

    fun getSelectResult(): MutableList<LocalMedia> {
        return TempDataProvider.getInstance().selectResult
    }

    open fun restoreEngine() {
        if (config.imageEngine == null) {
            SelectorAppMaster.getInstance().getSelectorEngine()?.apply {
                config.imageEngine = this.createImageLoaderEngine()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        restoreEngine()
        viewModel.onSaveInstanceState()
    }

    /**
     * Permission Description
     */
    open fun showPermissionDescription(isDisplay: Boolean, permission: Array<String>) {
        val onPermissionDescriptionListener =
            config.listenerInfo?.onPermissionDescriptionListener
        if (onPermissionDescriptionListener != null) {
            if (isDisplay) {
                if (PermissionChecker.checkSelfPermission(requireContext(), permission)) {
//                    SpUtils.putBoolean(requireContext(), permission[0], false)
                } else {
//                    if (!SpUtils.getBoolean(requireContext(), permission[0], false)) {
//                        onPermissionDescriptionListener.onDescription(this, permission)
//                    }
                }
            } else {
                onPermissionDescriptionListener.onDismiss(this)
            }
        }
    }

    /**
     * Permission denied
     */
    open fun handlePermissionDenied(permission: Array<String>) {
        TempDataProvider.getInstance().currentRequestPermission = permission
        val onPermissionDeniedListener = config.listenerInfo?.onPermissionDeniedListener
        if (onPermissionDeniedListener != null) {
            showPermissionDescription(false, permission)
            onPermissionDeniedListener.onDenied(
                this,
                permission,
                SelectorConstant.REQUEST_GO_SETTING,
                object : OnCallbackListener<Boolean> {
                    override fun onCall(data: Boolean) {
                        if (data) {
                            handlePermissionSettingResult(TempDataProvider.getInstance().currentRequestPermission)
                        }
                    }
                })
        } else {
            PermissionUtil.goIntentSetting(this, SelectorConstant.REQUEST_GO_SETTING)
        }
    }

    private fun setFragmentKeyBackListener() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onKeyBackAction()
                return@OnKeyListener true
            }
            false
        })
    }

    open fun onKeyBackAction() {
        onBackPressed()
    }

    open fun onBackPressed() {
        if (!isStateSaved) {
            if (isRootExit()) {
                // Home Exit
                if (this is TapMediaPickFragment) {
                    config.listenerInfo?.onResultCallbackListener?.onCancel()
                }
                if (isNormalDefaultEnter()) {
                    requireActivity().finish()
                } else {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                SelectorProviders.destroy()
            } else {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    open fun isRootExit(): Boolean {
        return this is TapMediaPickFragment
    }

    /**
     * Confirm completion of selection
     */
    open fun onConfirmComplete() {
        requireActivity().runOnUiThread {
            if (!checkCompleteValidity()) {
                return@runOnUiThread
            }
            val selectResult = getSelectResult().toMutableList()
            viewModel.viewModelScope.launch {
                val mediaConverterEngine = config.mediaConverterEngine
                if (mediaConverterEngine != null) {
                    selectResult.forEach { media ->
                        mediaConverterEngine.converter(requireContext(), media)
                    }
                }

                if (config.isActivityResult) {
                    requireActivity().intent?.apply {
                        val result = arrayListOf<LocalMedia>()
                        result.addAll(selectResult)
                        this.putParcelableArrayListExtra(SelectorConstant.KEY_EXTRA_RESULT, result)
                        requireActivity().setResult(Activity.RESULT_OK, this)
                    }
                } else {
                    config.listenerInfo?.onResultCallbackListener?.onResult(selectResult)
                }
                if (!isStateSaved) {
                    if (isNormalDefaultEnter()) {
                        requireActivity().finish()
                    } else {
                        requireActivity().supportFragmentManager.fragments.forEach { _ ->
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
                SelectorProviders.destroy()
            }
        }
    }

    /**
     * Verify legality before completion
     */
    open fun checkCompleteValidity(): Boolean {
        val selectResult = getSelectResult()
        if (config.listenerInfo?.onConfirmListener?.onConfirm(
                requireContext(),
                selectResult
            ) == true
        ) {
            return false
        }
        if (config.minSelectNum > 0 && selectResult.size <= 0) {
//                val msg = getString(
//                    R.string.ps_min_img_num,
//                    config.minSelectNum.toString()
//                )
//                showTipsDialog(msg)
            return false
        }
        return true
    }

    /**
     * Turn on the camera
     */
    open fun openSelectedCamera() {
        startCameraAction(config.mediaType)
    }

    /**
     * Activate camera intent based on [MediaType]
     */
    open fun startCameraAction(mode: MediaType) {
        val permission = arrayOf(Manifest.permission.CAMERA)
        if (PermissionChecker.checkSelfPermission(requireContext(), permission)) {
            takePictures()
        } else {
            showPermissionDescription(true, permission)
            val onPermissionApplyListener = config.listenerInfo?.onPermissionApplyListener
            if (onPermissionApplyListener != null) {
                showCustomPermissionApply(permission)
            } else {
                PermissionChecker.requestPermissions(this, permission,
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            showPermissionDescription(false, permission)
                            takePictures()
                        }

                        override fun onDenied() {
                            handlePermissionDenied(permission)
                        }
                    })
            }
        }
    }

    private val cameraUtils by lazy(LazyThreadSafetyMode.NONE) {
        CameraUtils(requireActivity())
    }

    /**
     * System camera takes pictures
     */
    open fun takePictures() {
        val context = requireContext()
        val outputDir = config.imageOutputDir
        val defaultFileName = "${FileUtils.createFileName("IMG")}.jpg"
        val fileName = defaultFileName
        if (TextUtils.isEmpty(outputDir)) {
            // Use default storage path
            viewModel.outputUri = MediaStoreUtils.insertImage(context, fileName)
        } else {
            // Use custom storage path
            val outputFile = File(outputDir, fileName)
            viewModel.outputUri = Uri.fromFile(outputFile)
        }

        cameraUtils.dispatchCaptureIntent(requireContext(), SelectorConstant.REQUEST_CAMERA, 1)
    }

    open fun onSelectedOnlyCameraDialog() {
        startCameraAction(MediaType.IMAGE)
    }

    /**
     * Confirm media selection
     * @param media The media object for the current operation
     * @param isSelected Select Status
     */
    open fun confirmSelect(media: LocalMedia, isSelected: Boolean): Int {
        if (!isSelected) {
            if (config.selectionMode == SelectionMode.MULTIPLE) {
                if (onCheckSelectValidity(media, isSelected) != SelectedState.SUCCESS) {
                    return SelectedState.INVALID
                }
            }
        }
        return if (isSelected) {
            if (getSelectResult().contains(media)) {
                getSelectResult().remove(media)
                globalViewMode.setSelectResultLiveData(media)
            }
            SelectedState.REMOVE
        } else {
            if (config.selectionMode == SelectionMode.SINGLE) {
                if (getSelectResult().isNotEmpty()) {
                    globalViewMode.setSelectResultLiveData(getSelectResult().first())
                    getSelectResult().clear()
                }
            }
            if (!getSelectResult().contains(media)) {
                getSelectResult().add(media)
                globalViewMode.setSelectResultLiveData(media)
            }
            SelectedState.SUCCESS
        }
    }

    /**
     * Verify the legitimacy of selecting media
     * @param media The media object for the current operation
     * @param isSelected Select Status
     */
    open fun onCheckSelectValidity(media: LocalMedia, isSelected: Boolean): Int {
        val count = getSelectResult().size
        if (count >= config.totalCount) {
//                    showTipsDialog(
//                        getString(
//                            R.string.ps_message_max_num, config.totalCount.toString()
//                        )
//                    )
            return SelectedState.INVALID
        }
        return SelectedState.SUCCESS
    }

    /**
     * Process the selection results based on user API settings
     */
    open fun handleSelectResult() {
        val cropEngine = config.cropEngine
        if (cropEngine != null && isCrop()) {
            cropEngine.onCrop(
                this,
                getSelectResult(),
                SelectorConstant.REQUEST_CROP
            )
        } else {
            onConfirmComplete()
        }
    }

    /**
     * Media types that support cropping
     */
    open fun isCrop(): Boolean {
        getSelectResult().forEach continuing@{ media ->
            if (config.skipCropFormat.contains(media.mimeType)) {
                return@continuing
            }
            if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                return true
            }
        }
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mPermissionResultListener != null) {
            PermissionChecker.onRequestPermissionsResult(grantResults, mPermissionResultListener)
            mPermissionResultListener = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val context = requireContext()
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectorConstant.REQUEST_CAMERA) {
                val schemeFile = viewModel.outputUri?.scheme.equals("file")
                val outputUri = if (schemeFile) {
                    viewModel.outputUri
                } else {
                    data?.getParcelableExtra(MediaStore.EXTRA_OUTPUT) ?: data?.data
                    ?: viewModel.outputUri
                }
                if (outputUri != null) {
                    analysisCameraData(outputUri)
                } else {
                    throw IllegalStateException("Camera output uri is empty")
                }
            } else if (requestCode == SelectorConstant.REQUEST_CROP) {
                val selectResult = getSelectResult()
                if (selectResult.isNotEmpty()) {
                    if (selectResult.size == 1) {
                        mergeSingleCrop(data, selectResult)
                    } else {
                        mergeMultipleCrop(data, selectResult)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            onResultCanceled(requestCode, resultCode)
        }
    }

    open fun setDefaultAlbumTitle(title: String?) {

    }

    /**
     * Analyzing Camera Generated Data
     */
    open fun analysisCameraData(uri: Uri) {
        val context = requireContext()
        val isContent = uri.scheme.equals("content")
        val realPath = if (isContent) {
            MediaUtils.getPath(context, uri)
        } else {
            uri.path
        }
        if (TextUtils.isEmpty(realPath)) {
            return
        }
        viewModel.scanFile(if (isContent) realPath else null, object : ScanListener {
            override fun onScanFinish() {
                viewModel.viewModelScope.launch {
                    val media = if (isContent) {
                        MediaUtils.getAssignPathMedia(context, realPath!!)
                    } else {
                        MediaUtils.getAssignFileMedia(context, realPath!!)
                    }
                    onMergeCameraResult(media)
                }
            }
        })
    }

    /**
     * Merge Camera Output Results
     */
    open fun onMergeCameraResult(media: LocalMedia?) {

    }

    /**
     * Activity Result Canceled
     */
    open fun onResultCanceled(requestCode: Int, resultCode: Int) {
        if (requestCode == SelectorConstant.REQUEST_GO_SETTING) {
            handlePermissionSettingResult(TempDataProvider.getInstance().currentRequestPermission)
        }
    }

    /**
     * Merge Cropping single images data
     */
    open fun mergeSingleCrop(data: Intent?, selectResult: MutableList<LocalMedia>) {
        val media = selectResult.first()
        val outputUri = if (data?.hasExtra(CropWrap.CROP_OUTPUT_URI) == true) {
            data.getParcelableExtra<Uri>(CropWrap.CROP_OUTPUT_URI)
        } else {
            data?.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
        }
        media.cropWidth = data?.getIntExtra(CropWrap.CROP_IMAGE_WIDTH, 0) ?: 0
        media.cropHeight = data?.getIntExtra(CropWrap.CROP_IMAGE_HEIGHT, 0) ?: 0
        media.cropOffsetX = data?.getIntExtra(CropWrap.CROP_OFFSET_X, 0) ?: 0
        media.cropOffsetY = data?.getIntExtra(CropWrap.CROP_OFFSET_Y, 0) ?: 0
        media.cropAspectRatio = data?.getFloatExtra(CropWrap.CROP_ASPECT_RATIO, 0F) ?: 0F
        media.cropPath = if (MediaUtils.isContent(outputUri.toString())) {
            outputUri.toString()
        } else {
            outputUri?.path
        }
        onConfirmComplete()
    }

    /**
     * Merge Cropping multiple images data
     */
    open fun mergeMultipleCrop(data: Intent?, selectResult: MutableList<LocalMedia>) {
        val json = data?.getStringExtra(MediaStore.EXTRA_OUTPUT)
        if (json == null || TextUtils.isEmpty(json)) {
            return
        }
        val array = JSONArray(json)
        if (array.length() == selectResult.size) {
            selectResult.forEachIndexed { i, media ->
                val item = array.optJSONObject(i)
                media.cropPath = item.optString(CropWrap.DEFAULT_CROP_OUTPUT_PATH)
                media.cropWidth = item.optInt(CropWrap.DEFAULT_CROP_IMAGE_WIDTH)
                media.cropHeight = item.optInt(CropWrap.DEFAULT_CROP_IMAGE_HEIGHT)
                media.cropOffsetX = item.optInt(CropWrap.DEFAULT_CROP_OFFSET_X)
                media.cropOffsetY = item.optInt(CropWrap.DEFAULT_CROP_OFFSET_Y)
                media.cropAspectRatio = item.optDouble(CropWrap.DEFAULT_CROP_ASPECT_RATIO).toFloat()
            }
            onConfirmComplete()
        } else {
            throw IllegalStateException("Multiple image cropping results do not match selection results:::${array.length()}!=${selectResult.size}")
        }
    }

    /**
     * Permission Listener
     */
    private var mPermissionResultListener: OnPermissionResultListener? = null

    /**
     * Set Permission Listener
     */
    open fun setOnPermissionResultListener(listener: OnPermissionResultListener?) {
        this.mPermissionResultListener = listener
    }
}