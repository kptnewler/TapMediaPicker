package com.newler.tapmediapicker

import android.app.Application
import android.content.Context
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.app.SelectorEngine
import com.luck.picture.lib.engine.ImageEngine

class App: Application(), IApp {

    override fun onCreate() {
        super.onCreate()
    }


    override fun getAppContext(): Context {
        return this
    }

    override fun getSelectorEngine(): SelectorEngine? {
        return SelectorEngineImp()

    }


    class SelectorEngineImp : SelectorEngine {
        override fun createImageLoaderEngine(): ImageEngine {
            return CoilEngine()
        }
    }

}