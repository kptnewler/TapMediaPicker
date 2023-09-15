package com.newler.tapmediapicker

import android.app.Application
import android.content.Context
import android.os.Build
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.engine.PictureSelectorEngine
import java.io.File

class App: Application(), IApp, ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        PictureAppMaster.app = this
    }

    override fun newImageLoader(): ImageLoader {
        val imageLoader = ImageLoader.Builder(appContext)
        val newBuilder: ComponentRegistry.Builder = ComponentRegistry().newBuilder()
        newBuilder.add(VideoFrameDecoder.Factory())
        if (Build.VERSION.SDK_INT >= 28) {
            newBuilder.add(ImageDecoderDecoder.Factory())
        } else {
            newBuilder.add(GifDecoder.Factory())
        }
        val componentRegistry: ComponentRegistry = newBuilder.build()
        imageLoader.components(componentRegistry)
        imageLoader.memoryCache(
            MemoryCache.Builder(appContext)
                .maxSizePercent(0.25).build()
        )
        imageLoader.diskCache(
            DiskCache.Builder()
                .directory(File(cacheDir, "image_cache"))
                .maxSizePercent(0.02)
                .build()
        )
        return imageLoader.build()
    }

    override fun getAppContext(): Context {
        return this
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine {
        return PictureSelectorEngineImpl()
    }
}