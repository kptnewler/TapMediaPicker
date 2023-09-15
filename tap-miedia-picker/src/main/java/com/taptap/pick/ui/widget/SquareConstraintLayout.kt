package com.taptap.pick.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout

class SquareConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Set a square layout.
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}