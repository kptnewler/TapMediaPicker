package com.taptap.pick.ui.widget.photoview

import android.annotation.TargetApi
import android.view.View

object Compat {
    fun postOnAnimation(view: View, runnable: Runnable) {
        postOnAnimationJellyBean(view, runnable)
    }

    @TargetApi(16)
    private fun postOnAnimationJellyBean(view: View, runnable: Runnable) {
        view.postOnAnimation(runnable)
    }
}