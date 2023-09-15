package com.taptap.pick.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.taptap.pick.data.bean.LocalMedia

class GlobalViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    private companion object {
        const val KEY_EDITOR_LIVE_DATA = "key_editor_live_data"
        const val KEY_RESULT_LIVE_DATA = "key_result_live_data"
        const val KEY_ORIGINAL_LIVE_DATA = "key_original_live_data"
    }

    /**
     *  The original drawing options have changed
     */
    fun getOriginalLiveData(): MutableLiveData<Boolean> {
        return state.getLiveData(KEY_ORIGINAL_LIVE_DATA)
    }

    fun setOriginalLiveData(isOriginal: Boolean) {
        getOriginalLiveData().value = isOriginal
    }

    /**
     *  selected result change
     */
    fun getSelectResultLiveData(): MutableLiveData<LocalMedia> {
        return state.getLiveData(KEY_RESULT_LIVE_DATA)
    }

    fun setSelectResultLiveData(media: LocalMedia) {
        getSelectResultLiveData().value = media
    }

    /**
     *  editor result change
     */
    fun getEditorLiveData(): MutableLiveData<LocalMedia> {
        return state.getLiveData(KEY_EDITOR_LIVE_DATA)
    }

    fun setEditorLiveData(media: LocalMedia) {
        getEditorLiveData().value = media
    }
}