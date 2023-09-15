package com.taptap.pick.ui.widget.photoview

import android.graphics.RectF
import android.view.MotionEvent
import android.widget.ImageView

interface OnGestureListener {

    fun onDrag(dx: Float, dy: Float)

    fun onFling(
        startX: Float, startY: Float, velocityX: Float,
        velocityY: Float
    )

    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)

    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float, dx: Float, dy: Float)
}


interface OnOutsidePhotoTapListener {
    fun onOutsidePhotoTap(imageView: ImageView?)
}

interface OnMatrixChangedListener {
    fun onMatrixChanged(rect: RectF?)
}

interface OnPhotoTapListener {
    fun onPhotoTap(view: ImageView?, x: Float, y: Float)
}

interface OnSingleFlingListener {
    fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean
}

interface OnViewDragListener {
    fun onDrag(dx: Float, dy: Float)
}

interface OnViewTapListener {
    fun onViewTap(view: ImageView?, x: Float, y: Float)
}

interface OnScaleChangedListener {
    fun onScaleChanged(scaleFactor: Float, focusX: Float, focusY: Float)
}

