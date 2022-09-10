package org.codroid.editor.graphics

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.view.inputmethod.CursorAnchorInfo
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.codroid.editor.*

class Cursor(private val mEditor: CodroidEditor) {

    private val mCursorPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    private val mCursorWidth = 4F
    private var mCurrentRow = 0
    private var mCurrentCol = 1
    private var mStart = 0
    private val mEnd = 0
    private var mPositionLeft = 0F
    private var mPositionTop = 0F

    private var mCurrentAlpha = 1F
    private val mDuration = 500L

    private val mBlinkJob: Job
    private val mBlinkAnimator = ValueAnimator.ofFloat(1F, 0F).apply {
        duration = 200
        addUpdateListener {
            mCurrentAlpha = animatedValue as Float
            mEditor.postInvalidateOnAnimation()
        }
    }
    private var mCursorListeners = mutableListOf<(row: Int, col: Int) -> Unit>()
    private var mVisible = true

    init {
        mEditor.getRowsRender().computeAbsolutePos(mCurrentRow, mCurrentCol).let {
            mPositionTop = it.first
            mPositionLeft = it.second
        }
        mBlinkJob = mEditor.lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                while (isActive) {
                    if (mVisible) {
                        withContext(Dispatchers.Main) {
                            mBlinkAnimator.start()
                        }
                        delay(mDuration)
                        withContext(Dispatchers.Main) {
                            mBlinkAnimator.reverse()
                        }
                        delay(mDuration)
                    }
                }
            }
        }
    }

    fun drawCursor(canvas: Canvas) {
        if (mVisible) {
            mCursorPaint.color = Color.argb(mCurrentAlpha, 1F, 0F, 0F)
            canvas.drawRoundRect(
                RectF(
                    mPositionLeft,
                    mPositionTop + 8,
                    mPositionLeft + mCursorWidth,
                    mPositionTop + mEditor.getLineHeight() - 8
                ), mCursorWidth, mCursorWidth, mCursorPaint
            )
        }
    }

    private fun moveCursor(left: Float, top: Float) {
        mPositionLeft = left
        mPositionTop = top
        mEditor.invalidate()
    }

    fun addCursorChangedListener(callback: (row: Int, col: Int) -> Unit) {
        this.mCursorListeners.add(callback)
    }

    fun moveCursor(row: Int = mCurrentRow, col: Int = mCurrentCol) {
        mCurrentRow = row
        mCurrentCol = col
        mCursorListeners.forEach {
            it.invoke(row, col)
        }
        val temp = mEditor.getRowsRender().computeAbsolutePos(row, col)
        moveCursor(
            temp.second,
            temp.first
        )
    }

    fun move(distance: Int) {
        moveCursor(col = mCurrentCol + distance)
    }

    fun getCurrentRow() = mCurrentRow

    fun getCurrentCol() = mCurrentCol

    fun getStart() = mStart

    fun getEnd() = mEnd

    fun toCursorAnchorInfo(): CursorAnchorInfo =
        CursorAnchorInfo.Builder()
            .setSelectionRange(getCurrentCol(), getCurrentCol())
            .build()


    fun hide() {
        if (mVisible) {
            mVisible = false
        }
    }

    fun show() {
        if (!mVisible) {
            mVisible = true
        }
    }
}