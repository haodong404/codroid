package org.codroid.body.widgets

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.max

@SuppressLint("ClickableViewAccessibility")
class EditorDelegate(val editor: CodroidEditor) : View.OnTouchListener {

    init {
//        editor.setOnTouchListener(this)
    }

    private val mVelocityTracer: VelocityTracker by lazy {
        VelocityTracker.obtain()
    }

    private val mScroller: Scroller by lazy {
        Scroller(editor.context).apply {
            editor.setScroller(this)
        }
    }

    private var mLastX = 0
    private var mLastY = 0
    private val minScrollGap = 2
    private var isScrolled = false

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        var handled = false
        event?.let { mVelocityTracer.addMovement(it) }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                v?.parent?.requestDisallowInterceptTouchEvent(true)
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                refreshLastMotionPosition(event)
                isScrolled = false
                handled = true
            }

            MotionEvent.ACTION_MOVE -> {
                val diffX = mLastX - event.x.toInt()
                val diffY = mLastY - event.y.toInt()
                if (abs(diffX) + abs(diffY) > minScrollGap * 2) {
                    editor.scrollTo(max(diffX, 0), max(diffY, 0))
                    isScrolled = true
                    handled = true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isScrolled) {
                    mVelocityTracer.computeCurrentVelocity(1000, 3500F)
                    fling(-mVelocityTracer.xVelocity, -mVelocityTracer.yVelocity)
                }
                handled = isScrolled
            }
        }
        return handled
    }

    private fun refreshLastMotionPosition(event: MotionEvent) {
        mLastX = event.x.toInt() + mScroller.currX
        mLastY = event.y.toInt() + mScroller.currY
    }

    private fun fling(velocityX: Float, velocityY: Float) {
        mScroller.fling(
            editor.scrollX,
            editor.scrollY,
            velocityX.toInt(),
            velocityY.toInt(),
            0,
            3000,
            0,
            3000
        )
    }

    fun recycle() {
        mVelocityTracer.recycle()
    }
}