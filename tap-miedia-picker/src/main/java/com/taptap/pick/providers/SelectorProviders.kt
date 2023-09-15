package com.taptap.pick.providers

import com.taptap.pick.data.config.PickerConfig
import java.util.LinkedList

object SelectorProviders {
    private val configQueue = LinkedList<PickerConfig>()

    fun addConfigQueue(config: PickerConfig) {
        configQueue.add(config)
    }

    fun getConfig(): PickerConfig {
        return configQueue.lastOrNull()?: PickerConfig()
    }

    fun destroy() {
        val config = getConfig()
        config.destroy()
        configQueue.remove(config)
        TempDataProvider.getInstance().reset()
    }

}