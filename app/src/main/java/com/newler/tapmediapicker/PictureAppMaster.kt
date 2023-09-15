package com.newler.tapmediapicker

import android.content.Context
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.engine.PictureSelectorEngine

object PictureAppMaster: IApp {
    var app: IApp? = null
    override fun getAppContext(): Context? {
        return app?.appContext
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine? {
        return app?.pictureSelectorEngine
    }
}