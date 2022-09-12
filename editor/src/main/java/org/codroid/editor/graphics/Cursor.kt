package org.codroid.editor.graphics

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.codroid.editor.CodroidEditor
import org.codroid.editor.utils.IntPair
import org.codroid.editor.utils.Timer
import org.codroid.editor.utils.first
import org.codroid.editor.utils.second

class Cursor(private val mEditor: CodroidEditor) {

    private val mCursorPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        isAntiAlias = true
    }

    private val mCursorWidth = 4F
    private var mCurrentRow = 0
    private var mCurrentCol = 1

    private var mPositionLeft = 0F
    private var mPositionTop = 0F

    private var isSelecting = false
    private var mStartIndex = 0
    private var mEndIndex = 0
    private var mStartPosition: IntPair = 0u
    private var mEndPosition: IntPair = 0u
    private var mStartPositionLeft = 0F
    private var mStartPositionTop = 0F

    private var mEndPositionLeft = 0F
    private var mEndPositionTop = 0F

    private var mCurrentAlpha = 1F
    private val mDuration = 500L

    private var isBlinking = false
    private val mBlinkingTimer = Timer.create(1000, {
        while (isActive) {
            if (mVisible && isBlinking) {
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
    }, mEditor.lifecycleScope)

    private val mBlinkAnimator = ValueAnimator.ofFloat(1F, 0F).apply {
        duration = 200
        addUpdateListener {
            mCurrentAlpha = animatedValue as Float
            mEditor.postInvalidateOnAnimation()
        }
    }
    private var mCursorListeners = mutableListOf<(row: Int, col: Int) -> Unit>()
    private var mVisible = true

    private val mCursorHandleRadius = 25F

    init {
        mEditor.getRowsRender().computeAbsolutePos(mCurrentRow, mCurrentCol).let {
            mPositionLeft = it.first
            mPositionTop = it.second
        }
        mEditor.lifecycleScope.launchWhenCreated {
            mBlinkingTimer.start()
        }
    }

    fun drawCursor(canvas: Canvas) {
        if (mVisible) {
            mCursorPaint.color = Color.argb(mCurrentAlpha, 1F, 0F, 0F)

            if (isSelecting) {
                // Draw start handle.
                canvas.drawRect(
                    RectF(
                        mStartPositionLeft,
                        mStartPositionTop,
                        mStartPositionLeft + mCursorWidth,
                        mStartPositionTop + mEditor.getLineHeight()
                    ), mCursorPaint
                )

                canvas.drawCircle(
                    mStartPositionLeft,
                    mStartPositionTop - mCursorHandleRadius,
                    mCursorHandleRadius,
                    mCursorPaint
                )

                // Draw end handle
                canvas.drawRect(
                    RectF(
                        mEndPositionLeft,
                        mEndPositionTop,
                        mEndPositionLeft + mCursorWidth,
                        mEndPositionTop + mEditor.getLineHeight()
                    ), mCursorPaint
                )

                canvas.drawCircle(
                    mStartPositionLeft,
                    mStartPositionTop + mEditor.getLineHeight() + mCursorHandleRadius,
                    mCursorHandleRadius,
                    mCursorPaint
                )

            } else {
                canvas.drawRoundRect(
                    RectF(
                        mPositionLeft,
                        mPositionTop + 8,
                        mPositionLeft + mCursorWidth,
                        mPositionTop + mEditor.getLineHeight() - 8
                    ), mCursorWidth, mCursorWidth, mCursorPaint
                )

                canvas.drawCircle(
                    mPositionLeft + mCursorWidth / 2,
                    mPositionTop + mEditor.getLineHeight() + mCursorHandleRadius,
                    mCursorHandleRadius,
                    mCursorPaint
                )
            }
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
        mEditor.getRowsRender().focusRow(row)
        mCurrentRow = row
        mCurrentCol = col
        mCursorListeners.forEach {
            it.invoke(row, col)
        }
        val temp = mEditor.getRowsRender().computeAbsolutePos(row, col)
        moveCursor(
            temp.first,
            temp.second
        )
    }

    fun moveCursorBy(distance: Int) {
        moveCursor(col = mCurrentCol + distance)
    }

    fun moveCursorTo(position: Int) {
        getTextSequence()?.getRowAndCol(position)?.run {
            moveCursor(first(), second())
        }
    }

    fun getCurrentRow() = mCurrentRow

    fun getCurrentLine() = mCurrentRow + 1

    fun getCurrentCol() = mCurrentCol

    fun getStart() = mStartIndex

    fun getEnd() = mEndIndex

    fun isSelecting() = isSelecting

    /**
     * Intercepts the scroll event when handle is hit.
     *
     * @param x the absolute position of x.
     * @param y the absolute position of y.
     */
    fun hitCursorHandle(x: Float, y: Float): Boolean {
        getCursorHandleRectF().run {
            return x in left..right && y in top..bottom
        }
    }

    fun handleCursorHandleMovement(x: Float, y: Float) {
        mEditor.getRowsRender().computeRowCol(x, y).run {
            val actualRow = first() - 1
            val actualCol = second()
            if (actualRow != getCurrentRow() || actualCol != getCurrentCol()) {
                moveCursor(actualRow, actualCol)
            }
        }
    }

    private fun getCursorHandleRectF(): RectF = RectF(
        mPositionLeft - mCursorHandleRadius,
        mPositionTop + mEditor.getLineHeight(),
        mPositionLeft + mCursorHandleRadius,
        mPositionTop + mEditor.getLineHeight() + 2 * mCursorHandleRadius
    )

    fun select(start: Int, end: Int) {
        mStartIndex = start
        mEndIndex = end
        isSelecting = true
        getTextSequence()?.getRowAndCol(start)?.let {
            moveCursor(it.first(), it.second())
            mStartPosition = it
            mEditor.getRowsRender().computeAbsolutePos(it.first(), it.second()).let { pos ->
                mStartPositionLeft = pos.first
                mStartPositionTop = pos.second
            }
        }
        getTextSequence()?.getRowAndCol(end)?.let {
            mEndPosition = it
            mEditor.getRowsRender().computeAbsolutePos(it.first(), it.second()).let { pos ->
                mEndPositionLeft = pos.first
                mEndPositionTop = pos.second
            }
        }
    }

    private fun getTextSequence() = mEditor.getEditContent()?.getTextSequence()

    fun startBlinking() {
        isBlinking = true
    }

    fun stopBlinking() {
        isBlinking = false
    }

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