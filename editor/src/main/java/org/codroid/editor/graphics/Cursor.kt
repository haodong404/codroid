package org.codroid.editor.graphics

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.codroid.editor.CodroidEditor
import org.codroid.editor.decoration.internal.SelectionSpan
import org.codroid.editor.utils.*

class Cursor(private val mEditor: CodroidEditor) {

    companion object {
        const val TAG = "Cursor"
    }

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

    private var mSelectedRange = IntRange.EMPTY
    private var mSelectionSpan = SelectionSpan()
    private var isSelecting = false

    // Determine which handle is held. -1: unknown, 1: start, 0: end
    private var isMovingStartHandle = -1;

    // the position of start and end row.
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
        this.mCursorListeners.add(this::onCursorChanged)
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

                mCursorPaint.color = Color.GREEN
                canvas.drawCircle(
                    mStartPositionLeft,
                    mStartPositionTop + mEditor.getLineHeight() + mCursorHandleRadius,
                    mCursorHandleRadius,
                    mCursorPaint
                )

                // Draw end handle
                mCursorPaint.color = Color.RED
                canvas.drawRect(
                    RectF(
                        mEndPositionLeft,
                        mEndPositionTop,
                        mEndPositionLeft + mCursorWidth,
                        mEndPositionTop + mEditor.getLineHeight()
                    ), mCursorPaint
                )

                canvas.drawCircle(
                    mEndPositionLeft,
                    mEndPositionTop + mEditor.getLineHeight() + mCursorHandleRadius,
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

    fun getStart() = mSelectedRange.first

    fun getEnd() = mSelectedRange.last

    fun isSelecting() = isSelecting

    /**
     * Intercepts the scroll event when handle is hit.
     *
     * @param x the absolute position of x.
     * @param y the absolute position of y.
     */
    fun isHitCursorHandle(x: Float, y: Float): Boolean {
        getCursorHandleRectF().run {
            return x in left..right && y in top..bottom
        }
    }

    fun isHitSelectingHandleStart(x: Float, y: Float): Boolean {
        getSelectingHandleStartRectF().run {
            return x in left..right && y in top..bottom
        }
    }

    fun isHitSelectingHandleEnd(x: Float, y: Float): Boolean {
        getSelectingHandleEndRectF().run {
            return x in left..right && y in top..bottom
        }
    }

    fun handleCursorHandleTouchEvent(event: MotionEvent) {
        if (isMovingStartHandle == -1) {
            isMovingStartHandle = if (isHitSelectingHandleStart(event.x, event.y)) {
                1
            } else {
                0
            }
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (isSelecting) {
                    mEditor.getRowsRender().computeRowCol(event.x, event.y).run {
                        val actualRow = first() - 1
                        val actualCol = second()
                        if (actualRow != getCurrentRow() || actualCol != getCurrentCol()) {

                            var isFlipped = false
                            if (isMovingStartHandle == 1) {
                                if (actualRow > mEndPosition.first() || (actualRow == mEndPosition.first() && actualCol > mEndPosition.second())) {
                                    isFlipped = true
                                    isMovingStartHandle = 0
                                }
                            } else {
                                if (actualRow < mStartPosition.first() || (actualRow == mStartPosition.first() && actualCol < mStartPosition.second())) {
                                    isFlipped = true
                                    isMovingStartHandle = 1
                                }
                            }

                            if (isMovingStartHandle == 1) {
                                val endPosition = if (isFlipped) {
                                    mStartPosition
                                } else {
                                    mEndPosition
                                }
                                select(
                                    actualRow,
                                    actualCol,
                                    endPosition.first(),
                                    endPosition.second()
                                )
                            } else {
                                val startPosition = if (isFlipped) {
                                    mEndPosition
                                } else {
                                    mStartPosition
                                }
                                select(
                                    startPosition.first(),
                                    startPosition.second(),
                                    actualRow,
                                    actualCol
                                )
                            }
                        }
                    }
                } else {
                    mEditor.getRowsRender().computeRowCol(event.x, event.y).run {
                        val actualRow = first() - 1
                        val actualCol = second()
                        if (actualRow != getCurrentRow() || actualCol != getCurrentCol()) {
                            moveCursor(actualRow, actualCol)
                        }
                    }
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                isMovingStartHandle = -1
            }
        }
    }

    private fun getCursorHandleRectF() = RectF(
        mPositionLeft - mCursorHandleRadius,
        mPositionTop + mEditor.getLineHeight(),
        mPositionLeft + mCursorHandleRadius,
        mPositionTop + mEditor.getLineHeight() + 2 * mCursorHandleRadius
    )

    private fun getSelectingHandleStartRectF() = RectF(
        mStartPositionLeft - mCursorHandleRadius,
        mStartPositionTop + mEditor.getLineHeight(),
        mStartPositionLeft + mCursorHandleRadius,
        mStartPositionTop + mEditor.getLineHeight() + 2 * mCursorHandleRadius
    )

    private fun getSelectingHandleEndRectF() = RectF(
        mEndPositionLeft - mCursorHandleRadius,
        mEndPositionTop + mEditor.getLineHeight(),
        mEndPositionLeft + mCursorHandleRadius,
        mEndPositionTop + mEditor.getLineHeight() + 2 * mCursorHandleRadius
    )

    fun select(startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
        moveCursor(startRow, startCol)
        if (mSelectedRange.isEmpty()) {
            mSelectedRange = (getTextSequence()?.charIndex(startRow, startCol + 1)
                ?: 0)..(getTextSequence()?.charIndex(endRow, endCol) ?: 0)
        }
        isSelecting = true
        getEditContent()?.addDecoration(mSelectedRange, mSelectionSpan)
        mStartPosition = makePair(startRow, startCol)
        mEditor.getRowsRender().computeAbsolutePos(startRow, startCol).let { pos ->
            mStartPositionLeft = pos.first
            mStartPositionTop = pos.second
        }
        mEndPosition = makePair(endRow, endCol)
        mEditor.getRowsRender().computeAbsolutePos(endRow, endCol).let { pos ->
            mEndPositionLeft = pos.first
            mEndPositionTop = pos.second
        }
        mEditor.postInvalidate()
    }

    fun select(start: Int, end: Int) {
        mSelectedRange = start..end
        var startRow = 0
        var startCol = 0
        var endRow = 0
        var endCol = 0
        getTextSequence()?.getRowAndCol(start)?.let {
            startRow = it.first()
            startCol = it.second()
        }
        getTextSequence()?.getRowAndCol(end)?.let {
            endRow = it.first()
            endCol = it.second()
        }
        select(startRow, startCol, endRow, endCol)
    }

    private fun onCursorChanged(row: Int, col: Int) {
        resetSelection()
    }

    private fun resetSelection() {
        if (!mSelectedRange.isEmpty()) {
            getEditContent()?.removeSpan(mSelectedRange, mSelectionSpan)
            mSelectedRange = IntRange.EMPTY
            isSelecting = false
            mEditor.postInvalidate()
        }
    }

    private fun getTextSequence() = mEditor.getEditContent()?.getTextSequence()

    private fun getEditContent() = mEditor.getEditContent()

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