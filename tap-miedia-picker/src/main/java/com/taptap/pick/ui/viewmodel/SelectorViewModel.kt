package com.taptap.pick.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.taptap.pick.data.bean.LocalMedia
import com.taptap.pick.data.bean.LocalMediaAlbum
import com.taptap.pick.loader.MediaPagingLoaderImpl
import com.taptap.pick.loader.service.ScanListener
import com.taptap.pick.loader.service.SelectorMediaScannerConnection
import com.taptap.pick.providers.SelectorProviders
import kotlinx.coroutines.launch

class SelectorViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    private companion object {
        const val KEY_PAGE = "key_page"
        const val KEY_OUTPUT_DATA = "key_output_uri"
    }
    /**
     * Page of request
     */
    var page: Int = 1

    /**
     *  camera output uri
     */
    var outputUri: Uri? = null

    /**
     * PictureSelector Config
     */
    private val config = SelectorProviders.getConfig()

    /**
     * Media loader
     */
    private val mediaLoader = config.dataLoader ?: MediaPagingLoaderImpl(application)

    /**
     * Media list data LiveData
     */
    private val _medias = MutableLiveData<MutableList<LocalMedia>>()
    val mediaLiveData: LiveData<MutableList<LocalMedia>> get() = _medias

    /**
     *  Album list data LiveData
     */
    private val _albums = MutableLiveData<MutableList<LocalMediaAlbum>>()
    val albumLiveData: LiveData<MutableList<LocalMediaAlbum>> get() = _albums

    fun onSaveInstanceState() {
        state[KEY_PAGE] = page
        state[KEY_OUTPUT_DATA] = outputUri
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            page = state[KEY_PAGE] ?: page
            outputUri = state[KEY_OUTPUT_DATA] ?: outputUri
        }
    }


    /**
     * Refresh System Album
     */
    fun scanFile(path: String?, l: ScanListener?) {
        SelectorMediaScannerConnection(getApplication(), path, object : ScanListener {
            override fun onScanFinish() {
                l?.onScanFinish()
            }
        })
    }

    /**
     * Query local album list [LocalMediaAlbum]
     */
    fun loadMediaAlbum() {
        viewModelScope.launch {
            _albums.postValue(mediaLoader.loadMediaAlbum())
        }
    }


    /**
     * Query the data on the first page of [LocalMedia]
     */
    fun loadMedia(bucketId: Long) {
        viewModelScope.launch {
            page = 1
            _medias.postValue(
                mediaLoader.loadMedia(bucketId, config.pageSize)
            )
        }
    }

    /**
     * Query more data of [LocalMedia]
     */
    fun loadMediaMore(bucketId: Long) {
        viewModelScope.launch {
            page++
            _medias.postValue(mediaLoader.loadMediaMore(bucketId, page, config.pageSize))
        }
    }

    /**
     * Query media resources in the specified directory
     */
    fun loadAppInternalDir(sandboxDir: String) {
        viewModelScope.launch {
            page = 1
            _medias.postValue(mediaLoader.loadAppInternalDir(sandboxDir))
        }
    }
}