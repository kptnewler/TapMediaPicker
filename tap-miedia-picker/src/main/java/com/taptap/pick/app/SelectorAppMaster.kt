package com.taptap.pick.app

import android.content.Context

class SelectorAppMaster : IApp {
    override fun getAppContext(): Context? {
        return app?.getAppContext()
    }

    override fun getSelectorEngine(): SelectorEngine? {
        return app?.getSelectorEngine()
    }

    companion object {
        fun getInstance() = InstanceHelper.instance
    }

    object InstanceHelper {
        val instance = SelectorAppMaster()
    }

    private var app: IApp? = null

    fun setApp(app: IApp?) {
        this.app = app
    }

    fun getApp(): IApp? {
        return app
    }
}