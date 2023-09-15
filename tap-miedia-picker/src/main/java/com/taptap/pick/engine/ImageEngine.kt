package com.taptap.pick.engine

import android.content.Context
import android.widget.ImageView

interface ImageEngine {
    fun loadImage(context: Context, url: String?, imageView: ImageView)

    fun loadImage(context: Context, url: String?, width: Int, height: Int, imageView: ImageView)

    fun loadAlbumCover(context: Context, url: String?, imageView: ImageView)

    fun loadListImage(context: Context, url: String?, imageView: ImageView)

    fun pauseRequests(context: Context)

    fun resumeRequests(context: Context)
}