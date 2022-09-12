/*
 *      Copyright (c) 2022 Zachary. All rights reserved.
 *
 *      This file is part of Codroid.
 *
 *      Codroid is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      Codroid is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 *
 */


package org.codroid.editor

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.FrameLayout
import android.widget.OverScroller
import kotlinx.coroutines.*
import org.codroid.editor.utils.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class UnrestrainedScrollView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setWillNotDraw(false)
    }

    private val mMaximumVelocity: Float = 8000F
    private var isScrolling = false
    private var mScrollThreshold = 5
    private var mLastMotion = Vector()
    private val mScroller = OverScroller(context)
    private var mVelocityTracker: VelocityTracker? = null

    // The current distance from the top and left. Updated by onScrollChanged();
    private var mScrollCurrent: IntPair = 0u

    private val mColorNormal = Color.argb(0xEA, 0xDC, 0xDC, 0xDC)
    private val mColorPressed = Color.argb(0xEA, 0x20, 0x20, 0x20)
    private val mBarPaint = Paint().apply {
        color = Color.argb(0xEA, 0xDC, 0xDC, 0xDC)
        style = Paint.Style.FILL
    }

    // How thick is the scroll bar.
    private val mBarSize = 30

    // The height of the vertical bar.
    private var mBarHeight = 0

    // The width of the horizontal bar.
    private var mBarWidth = 0

    // Left margin for horizontal bar. It's changed as scrolling the canvas.
    private var mBarLeft = 0

    // Top margin for vertical bar. It's changed as scrolling the canvas.
    private var mBarTop = 0

    // true if touched vertical scroll bar.
    private var isHitVerticalBar = false

    // true if touched horizontal scroll bar.
    private var isHitHorizontalBar = false

    // When the scrollbar is pressed, its color will be changed.
    private var mCurrentScrollBarColor = mColorNormal
    private val mScrollBarColorAnimator = ValueAnimator.ofArgb(mColorNormal, mColorPressed).apply {
        duration = 150
        addUpdateListener {
            mCurrentScrollBarColor = animatedValue as Int
            postInvalidateOnAnimation()
        }
    }

    // true if scroll bars are hidden.
    private var isScrollBarHidden = true

    // The color from transparent to normal.
    private var mCurrentScrollBarColorNormal = Color.TRANSPARENT

    // This job will be executed when you end a scroll event,
    // and will be cancelled when you scrolled again within a specific duration.
    private var mHiddenJob: Job? = null
    private val mHiddenDuration = 3000L
    private var mHiddenTimer = Timer.create(mHiddenDuration, {
        withContext(Dispatchers.Main) {
            isHitHorizontalBar = false
            isHitVerticalBar = false
            mScrollBarVisibleAnimator.reverse()
            isScrollBarHidden = true
        }
    })

    private val mScrollBarVisibleAnimator =
        ValueAnimator.ofArgb(Color.argb(0, 0xDC, 0xDC, 0xDC), mColorNormal).apply {
            duration = 300
            addUpdateListener {
                mCurrentScrollBarColorNormal = animatedValue as Int
                postInvalidateOnAnimation()
            }
        }

    private var mOnScrollWithRow: ((start: Int, old: Int) -> Unit)? = null
    private var mLastRow = 0

    private var mInterceptedByChildEditor = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        getChild()?.let {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getChildAsEditor()?.let {
            mBarHeight = height * height / it.measuredHeight
            mBarWidth = if (it.measuredWidth == 0) {
                0
            } else {
                width * width / it.measuredWidth
            }
        }
    }

    override fun measureChild(
        child: View?,
        parentWidthMeasureSpec: Int,
        parentHeightMeasureSpec: Int
    ) {
        child?.let {
            val width = MeasureSpec.getSize(parentWidthMeasureSpec)
            val height = MeasureSpec.getSize(parentHeightMeasureSpec)
            child.measure(
                MeasureSpec.makeMeasureSpec(
                    max(0, width - paddingLeft - paddingRight),
                    MeasureSpec.UNSPECIFIED
                ),
                MeasureSpec.makeMeasureSpec(
                    max(0, height - paddingTop - paddingBottom),
                    MeasureSpec.UNSPECIFIED
                )
            )
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (ev.action == MotionEvent.ACTION_MOVE && isScrolling) {
                return true
            }
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastMotion.reset(it.x.roundToInt(), it.y.roundToInt())
                    mScroller.computeScrollOffset()
                    isScrolling = !mScroller.isFinished
                    if (getChildAsEditor()?.interceptParentScroll(
                            mScrollCurrent.first() + ev.x,
                            mScrollCurrent.second() + ev.y
                        ) == true
                    ) {
                        mInterceptedByChildEditor = true
                    } else {
                        mInterceptedByChildEditor = false
                        if (checkIsHitScrollBar(it)) {
                            // A scroll bar is hit.
                            mScrollBarColorAnimator.start()
                            isScrollBarHidden = false
                            mHiddenTimer.cancel()
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mInterceptedByChildEditor) {
                        return false
                    }
                    val delta = mLastMotion.deltaFrom(it.x.roundToInt(), it.y.roundToInt())
                    if (abs(delta.x) >= mScrollThreshold || abs(delta.y) >= mScrollThreshold) {
                        parent?.run {
                            requestDisallowInterceptTouchEvent(true)
                        }
                        isScrolling = true
                        mLastMotion.reset(it.x.roundToInt(), it.y.roundToInt())
                    }
                }

                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    isScrolling = false;
                }
            }
        }
        return isScrolling || isHitVerticalBar || isHitHorizontalBar
    }

    private fun checkIsHitScrollBar(event: MotionEvent): Boolean {
        isHitVerticalBar = event.x.toInt() in (width - mBarSize)..width
                && event.y.toInt() in mBarTop..(mBarTop + mBarHeight)
        isHitHorizontalBar = event.x.toInt() in mBarLeft..(mBarLeft + mBarWidth)
                && event.y.toInt() in (height - mBarSize)..height
        return isHitHorizontalBar || isHitVerticalBar
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            initVelocityTrackerIfNotExists()
            mVelocityTracker!!.addMovement(event)
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    isScrolling = !mScroller.isFinished
                    if (!mScroller.isFinished) {
                        parent?.run {
                            requestDisallowInterceptTouchEvent(true)
                        }
                        mScroller.abortAnimation()
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val delta =
                        -mLastMotion.deltaFrom(event.x.roundToInt(), event.y.roundToInt())
                    val range = getScrollRange()
                    mLastMotion.reset(event.x.roundToInt(), event.y.roundToInt())
                    parent?.run {
                        requestDisallowInterceptTouchEvent(true)
                    }
                    if (isScrollBarHidden) {
                        mScrollBarVisibleAnimator.start()
                        isScrollBarHidden = false
                    }
                    mHiddenTimer.cancel()
                    if (isHitVerticalBar) {
                        overScrollBy(
                            0, -(delta.y * height / mBarHeight),
                            scrollX, scrollY,
                            range.x, range.y,
                            0, 0,
                            false
                        )
                    } else if (isHitHorizontalBar) {
                        overScrollBy(
                            -(delta.x * width / mBarWidth), 0,
                            scrollX, scrollY,
                            range.x, range.y,
                            0, 0,
                            false
                        )
                    } else if (isScrolling) {
                        overScrollBy(
                            delta.x, delta.y,
                            scrollX, scrollY,
                            range.x, range.y,
                            0, 0,
                            true
                        )
                    }
                    return@let
                }

                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    if (isHitHorizontalBar || isHitVerticalBar) {
                        mScrollBarColorAnimator.reverse()
                    } else if (isScrolling) {
                        isScrolling = false
                        mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity)
                        fling(
                            Vector(
                                mVelocityTracker!!.xVelocity.roundToInt(),
                                mVelocityTracker!!.yVelocity.roundToInt()
                            )
                        )
                        recycleVelocityTracker()
                    }
                    mHiddenTimer.start()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onDrawForeground(canvas: Canvas?) {
        canvas?.run {
            // Draw the scroll bars.
            mBarPaint.color = if (isHitVerticalBar) {
                mCurrentScrollBarColor
            } else {
                mCurrentScrollBarColorNormal
            }
            drawRect(
                getVerticalScrollBarRect(),
                mBarPaint
            )

            mBarPaint.color = if (isHitHorizontalBar) {
                mCurrentScrollBarColor
            } else {
                mCurrentScrollBarColorNormal
            }
            drawRect(
                getHorizontalScrollBarRect(),
                mBarPaint
            )
        }
    }

    /**
     * Returns a [Rect], also the vertical scroll bar.
     * It contains the actual position, so the position is changed as you scroll the canvas.
     *
     * @return The [Rect] of vertical scroll bar.
     */
    private fun getVerticalScrollBarRect(): Rect {
        mBarTop = height * mScrollCurrent.second() / getScrollableSize().second()
        return Rect(
            mScrollCurrent.first() + width - mBarSize,
            mScrollCurrent.second() + mBarTop,
            mScrollCurrent.first() + width,
            mScrollCurrent.second() + mBarTop + mBarHeight
        )
    }

    /**
     * Returns a [Rect] also the horizontal scroll bar.
     * It contains the actual position, so the position is changed as you scroll the canvas.
     *
     * @return The [Rect] of horizontal scroll bar.
     */
    private fun getHorizontalScrollBarRect(): Rect {
        mBarLeft = if (getScrollableSize().first() == 0) {
            0
        } else {
            width * mScrollCurrent.first() / getScrollableSize().first()
        }
        return Rect(
            mScrollCurrent.first() + mBarLeft,
            mScrollCurrent.second() + height - mBarSize,
            mScrollCurrent.first() + mBarLeft + mBarWidth,
            mScrollCurrent.second() + height
        )
    }

    /**
     * Returns the border of the content.
     */
    private fun getScrollRange(): Vector {
        val result = Vector()
        if (childCount > 0) {
            getChildAt(0)?.let {
                result.reset(
                    it.width - (width - paddingLeft - paddingRight),
                    it.height - (height - paddingTop - paddingBottom)
                )
            }
        }
        return result
    }

    /**
     * Returns the full size of the child.
     */
    private fun getScrollableSize(): IntPair {
        getChildAt(0)?.let {
            return makePair(it.width, it.height)
        }
        return 0U
    }

    private fun fling(velocity: Vector) {
        val range = getScrollRange()
        mScroller.fling(
            scrollX,
            scrollY,
            -velocity.x,
            -velocity.y,
            0,
            range.x,
            0,
            range.y
        )
        postInvalidateOnAnimation()
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        if (!mScroller.isFinished) {
            val oldX = this.scrollX
            val oldY = this.scrollY
            this.scrollX = scrollX
            this.scrollY = scrollY
            onScrollChanged(this.scrollX, this.scrollY, oldX, oldY)
        }
        super.scrollTo(scrollX, scrollY)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val old = Vector(scrollX, scrollY)
            val now = Vector(mScroller.currX, mScroller.currY)
            if (now != old) {
                val range = getScrollRange()
                val delta = old.deltaFrom(now)
                overScrollBy(delta.x, delta.y, scrollX, scrollY, range.x, range.y, 0, 0, false)
                onScrollChanged(scrollX, scrollY, old.x, old.y)
            }
        }
    }

    fun setOnScrollWithRowListener(callback: (start: Int, old: Int) -> Unit) {
        mOnScrollWithRow = callback
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollCurrent = makePair(l, t)
        getChildAsEditor()?.run {
            if (oldt != t) {
                val current = (t / getLineHeight()).roundToInt()
                if (current != mLastRow) {
                    mOnScrollWithRow?.invoke(current, mLastRow)
                    mLastRow = current
                }
            }
        }
    }

    private fun getChildAsEditor(): CodroidEditor? {
        getChild()?.let {
            if (it is CodroidEditor) {
                return it
            }
        }
        return null
    }

    private fun getChild(): View? {
        if (childCount > 0) {
            return getChildAt(0)
        }
        return null
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }
}