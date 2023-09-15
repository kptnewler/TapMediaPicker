package com.taptap.pick.ui.widget

import android.content.Context
import android.content.res.Resources
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

class SlideSelectTouchListener : OnItemTouchListener {
    private var isActive = false
    private var mStart = 0
    private var mEnd = 0
    private var mInTopSpot = false
    private var mInBottomSpot = false
    private var mScrollDistance = 0
    private var mLastX = 0f
    private var mLastY = 0f
    private var mLastStart = 0
    private var mLastEnd = 0
    private var mSelectListener: OnSlideSelectListener? = null
    private var mRecyclerView: RecyclerView? = null
    private var mScroller: OverScroller? = null
    private val mScrollRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mScroller != null && mScroller!!.computeScrollOffset()) {
                scrollBy(mScrollDistance)
                ViewCompat.postOnAnimation(mRecyclerView!!, this)
            }
        }
    }

    // Definitions for touch auto scroll regions
    private var mTopBoundFrom = 0
    private var mTopBoundTo = 0
    private var mBottomBoundFrom = 0
    private var mBottomBoundTo = 0

    // User settings - default values
    private var mMaxScrollDistance = 16
    private var mAutoScrollDistance = (Resources.getSystem().displayMetrics.density * 56).toInt()
    private var mTouchRegionTopOffset = 0
    private var mTouchRegionBottomOffset = 0
    private var mScrollAboveTopRegion = true
    private var mScrollBelowTopRegion = true
    private var mHeaderViewCount = 0

    init {
        reset()
    }

    /**
     * Recyclerview header item count
     *
     * @param count
     */
    fun setRecyclerViewHeaderCount(count: Int): SlideSelectTouchListener {
        mHeaderViewCount = count
        return this
    }

    /**
     * sets the listener
     *
     *
     *
     * @param selectListener the listener that will be notified when items are (un)selected
     */
    fun withSelectListener(selectListener: OnSlideSelectListener?): SlideSelectTouchListener {
        mSelectListener = selectListener
        return this
    }

    /**
     * sets the distance that the RecyclerView is maximally scrolled (per scroll event)
     * higher values result in higher scrolling speed
     *
     *
     *
     * @param distance the distance in pixels
     */
    fun withMaxScrollDistance(distance: Int): SlideSelectTouchListener {
        mMaxScrollDistance = distance
        return this
    }

    /**
     * defines the height of the region at the top/bottom of the RecyclerView
     * which will make the RecyclerView scroll
     *
     *
     *
     * @param size height of region
     */
    fun withTouchRegion(size: Int): SlideSelectTouchListener {
        mAutoScrollDistance = size
        return this
    }

    /**
     * defines an offset for the TouchRegion from the top
     * useful, if RecyclerView is displayed underneath a semi transparent Toolbar at top or similar
     *
     *
     *
     * @param distance offset
     */
    fun withTopOffset(distance: Int): SlideSelectTouchListener {
        mTouchRegionTopOffset = distance
        return this
    }

    /**
     * defines an offset for the TouchRegion from the bottom
     * useful, if RecyclerView is displayed underneath a semi transparent navigation view at the bottom or similar
     * ATTENTION: to move the region upwards, set a negative value!
     *
     *
     *
     * @param distance offset
     */
    fun withBottomOffset(distance: Int): SlideSelectTouchListener {
        mTouchRegionBottomOffset = distance
        return this
    }

    /**
     * enables scrolling, if the user touches the region above the RecyclerView
     * respectively above the TouchRegion at the top
     *
     *
     *
     * @param enabled if true, scrolling will continue even if the touch moves above the top touch region
     */
    fun withScrollAboveTopRegion(enabled: Boolean): SlideSelectTouchListener {
        mScrollAboveTopRegion = enabled
        return this
    }

    /**
     * enables scrolling, if the user touches the region below the RecyclerView
     * respectively below the TouchRegion at the bottom
     *
     *
     *
     * @param enabled if true, scrolling will continue even if the touch moves below the bottom touch region
     */
    fun withScrollBelowTopRegion(enabled: Boolean): SlideSelectTouchListener {
        mScrollBelowTopRegion = enabled
        return this
    }

    /**
     * start the drag selection
     *
     *
     *
     * @param position the index of the first selected item
     */
    fun startSlideSelection(position: Int) {
        setActive(true)
        mStart = position
        mEnd = position
        mLastStart = position
        mLastEnd = position
        if (mSelectListener != null && mSelectListener is OnAdvancedSlideSelectListener) {
            (mSelectListener as OnAdvancedSlideSelectListener).onSelectionStarted(position)
        }
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (!isActive || rv.adapter == null || rv.adapter!!.itemCount == 0) {
            return false
        }
        val action = e.action
        when (action) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> reset()
        }
        mRecyclerView = rv
        val height = rv.height
        mTopBoundFrom = mTouchRegionTopOffset
        mTopBoundTo = mTouchRegionTopOffset + mAutoScrollDistance
        mBottomBoundFrom = height + mTouchRegionBottomOffset - mAutoScrollDistance
        mBottomBoundTo = height + mTouchRegionBottomOffset
        return true
    }

    fun startAutoScroll() {
        if (mRecyclerView == null) return
        initScroller(mRecyclerView!!.context)
        if (mScroller!!.isFinished) {
            mRecyclerView!!.removeCallbacks(mScrollRunnable)
            mScroller!!.startScroll(0, mScroller!!.currY, 0, 5000, 100000)
            ViewCompat.postOnAnimation(mRecyclerView!!, mScrollRunnable)
        }
    }

    private fun initScroller(context: Context) {
        if (mScroller == null) {
            mScroller = OverScroller(context, LinearInterpolator())
        }
    }

    fun stopAutoScroll() {
        try {
            if (mScroller != null && !mScroller!!.isFinished) {
                mRecyclerView!!.removeCallbacks(mScrollRunnable)
                mScroller!!.abortAnimation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        if (!isActive) {
            reset()
            return
        }
        val action = e.action
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (!mInTopSpot && !mInBottomSpot) changeSelectedRange(rv, e)
                processAutoScroll(e)
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> reset()
        }
    }

    private fun changeSelectedRange(rv: RecyclerView, e: MotionEvent) {
        changeSelectedRange(rv, e.x, e.y)
    }

    private fun changeSelectedRange(rv: RecyclerView?, x: Float, y: Float) {
        val child = rv!!.findChildViewUnder(x, y)
        if (child != null) {
            val position = rv.getChildAdapterPosition(child) - mHeaderViewCount
            if (position != RecyclerView.NO_POSITION && mEnd != position) {
                mEnd = position
                notifySelectRangeChange()
            }
        }
    }

    private fun processAutoScroll(event: MotionEvent) {
        val y = event.y.toInt()
        val mScrollSpeedFactor: Float
        if (y >= mTopBoundFrom && y <= mTopBoundTo) {
            mLastX = event.x
            mLastY = event.y
            mScrollSpeedFactor =
                (mTopBoundTo.toFloat() - mTopBoundFrom.toFloat() - (y.toFloat() - mTopBoundFrom.toFloat())) / (mTopBoundTo.toFloat() - mTopBoundFrom.toFloat())
            mScrollDistance = (mMaxScrollDistance.toFloat() * mScrollSpeedFactor * -1f).toInt()
            if (!mInTopSpot) {
                mInTopSpot = true
                startAutoScroll()
            }
        } else if (mScrollAboveTopRegion && y < mTopBoundFrom) {
            mLastX = event.x
            mLastY = event.y
            mScrollDistance = mMaxScrollDistance * -1
            if (!mInTopSpot) {
                mInTopSpot = true
                startAutoScroll()
            }
        } else if (y >= mBottomBoundFrom && y <= mBottomBoundTo) {
            mLastX = event.x
            mLastY = event.y
            mScrollSpeedFactor =
                (y.toFloat() - mBottomBoundFrom.toFloat()) / (mBottomBoundTo.toFloat() - mBottomBoundFrom.toFloat())
            mScrollDistance = (mMaxScrollDistance.toFloat() * mScrollSpeedFactor).toInt()
            if (!mInBottomSpot) {
                mInBottomSpot = true
                startAutoScroll()
            }
        } else if (mScrollBelowTopRegion && y > mBottomBoundTo) {
            mLastX = event.x
            mLastY = event.y
            mScrollDistance = mMaxScrollDistance
            if (!mInTopSpot) {
                mInTopSpot = true
                startAutoScroll()
            }
        } else {
            mInBottomSpot = false
            mInTopSpot = false
            mLastX = Float.MIN_VALUE
            mLastY = Float.MIN_VALUE
            stopAutoScroll()
        }
    }

    private fun notifySelectRangeChange() {
        if (mSelectListener == null) {
            return
        }
        if (mStart == RecyclerView.NO_POSITION || mEnd == RecyclerView.NO_POSITION) {
            return
        }
        val newStart: Int
        val newEnd: Int
        newStart = Math.min(mStart, mEnd)
        newEnd = Math.max(mStart, mEnd)
        if (newStart < 0) {
            return
        }
        if (mLastStart == RecyclerView.NO_POSITION || mLastEnd == RecyclerView.NO_POSITION) {
            if (newEnd - newStart == 1) {
                mSelectListener!!.onSelectChange(newStart, newStart, true)
            } else {
                mSelectListener!!.onSelectChange(newStart, newEnd, true)
            }
        } else {
            if (newStart > mLastStart) {
                mSelectListener!!.onSelectChange(mLastStart, newStart - 1, false)
            } else if (newStart < mLastStart) {
                mSelectListener!!.onSelectChange(newStart, mLastStart - 1, true)
            }
            if (newEnd > mLastEnd) {
                mSelectListener!!.onSelectChange(mLastEnd + 1, newEnd, true)
            } else if (newEnd < mLastEnd) {
                mSelectListener!!.onSelectChange(newEnd + 1, mLastEnd, false)
            }
        }
        mLastStart = newStart
        mLastEnd = newEnd
    }

    private fun reset() {
        setActive(false)
        if (mSelectListener != null && mSelectListener is OnAdvancedSlideSelectListener) (mSelectListener as OnAdvancedSlideSelectListener).onSelectionFinished(
            mEnd
        )
        mStart = RecyclerView.NO_POSITION
        mEnd = RecyclerView.NO_POSITION
        mLastStart = RecyclerView.NO_POSITION
        mLastEnd = RecyclerView.NO_POSITION
        mInTopSpot = false
        mInBottomSpot = false
        mLastX = Float.MIN_VALUE
        mLastY = Float.MIN_VALUE
        stopAutoScroll()
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    private fun scrollBy(distance: Int) {
        val scrollDistance: Int
        scrollDistance =
            if (distance > 0) Math.min(distance, mMaxScrollDistance) else Math.max(
                distance,
                -mMaxScrollDistance
            )
        mRecyclerView!!.scrollBy(0, scrollDistance)
        if (mLastX != Float.MIN_VALUE && mLastY != Float.MIN_VALUE) changeSelectedRange(
            mRecyclerView,
            mLastX,
            mLastY
        )
    }

    fun setActive(isActive: Boolean) {
        this.isActive = isActive
    }

    interface OnAdvancedSlideSelectListener : OnSlideSelectListener {
        /**
         * @param start the item on which the drag selection was started at
         */
        fun onSelectionStarted(start: Int)

        /**
         * @param end the item on which the drag selection was finished at
         */
        fun onSelectionFinished(end: Int)
    }

    interface OnSlideSelectListener {
        /**
         * @param start      the newly (un)selected range start
         * @param end        the newly (un)selected range end
         * @param isSelected true, it range got selected, false if not
         */
        fun onSelectChange(start: Int, end: Int, isSelected: Boolean)
    }
}
