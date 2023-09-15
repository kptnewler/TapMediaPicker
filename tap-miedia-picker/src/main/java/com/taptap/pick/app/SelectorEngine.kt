package com.taptap.pick.app

import com.taptap.pick.engine.ImageEngine

interface SelectorEngine {
    /**
     * Create ImageLoad Engine
     */
    fun createImageLoaderEngine(): ImageEngine
}