package com.newler.tapmediapicker

import android.content.Context
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.helper.ActivityCompatHelper

/**
 * @author：luck
 * @date：2022/2/14 3:00 下午
 * @describe：CoilEngine
 */
class CoilEngine : ImageEngine {
    override fun loadImage(context: Context, url: String?, imageView: ImageView) {
        val target = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun loadImage(
        context: Context,
        url: String?,
        width: Int,
        height: Int,
        imageView: ImageView
    ) {
        context.let {
            val builder = ImageRequest.Builder(it)
            if (width > 0 && height > 0) {
                builder.size(width, height)
            }
            imageView.let { v -> builder.data(url).target(v) }
            val request = builder.build();
            context.imageLoader.enqueue(request)
        }
    }

    override fun loadRoundImage(
        context: Context,
        url: String?,
        width: Int,
        height: Int,
        imageView: ImageView,
        round: Float
    ) {
        context.let {
            val builder = ImageRequest.Builder(it).transformations(RoundedCornersTransformation(round))
            if (width > 0 && height > 0) {
                builder.size(width, height)
            }
            imageView.let { v -> builder.data(url).target(v) }
            val request = builder.build();
            context.imageLoader.enqueue(request)
        }
    }

    override fun loadAlbumCover(context: Context, url: String?, imageView: ImageView) {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val target = ImageRequest.Builder(context)
            .data(url)
            .transformations(RoundedCornersTransformation(8F))
            .size(180, 180)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun loadListImage(context: Context, url: String?, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val target = ImageRequest.Builder(context)
            .data(url)
            .size(270, 270)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun pauseRequests(context: Context) {
    }

    override fun resumeRequests(context: Context) {
    }



}