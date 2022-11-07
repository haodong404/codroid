package org.codroid.editor.graphics

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.codroid.editor.CodroidEditor
import org.codroid.editor.decoration.RowNode
import org.codroid.editor.decoration.internal.SelectionSpan
import org.codroid.editor.utils.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Cursor(private val mEditor: CodroidEditor) {

    companion object {
        const val TAG = "Cursor"
    }

    data class CurrentInfo(
        var index: Int = 0,
        var row: Int = 1,
        var column: Int = 0,
        var lineLength: Int = 0,
        var rowNode: RowNode? = null
    )

    private val mCursorPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        isAntiAlias = true
    }

    private val mCurrentInfo: CurrentInfo = CurrentInfo()
    private val mCursorWidth = 4F

    private var mCoordinateLeft = 0F
    private var mCoordinateTop = 0F

    private var mSelectedRange = IntRange.EMPTY
    private var mSelectionSpan = SelectionSpan()
    private var isSelecting = false

    // Determine which handle is held. -1: unknown, 1: start, 0: end
    private var isMovingStartHandle = -1;

    // the position of start and end row.
    private var mStartPosition: IntPair = 0u
    private var mEndPosition: IntPair = 0u
//    private var mCursorIndex = 0

    private var mStartPositionLeft = 0F
    private var mStartPositionTop = 0F

    private var mEndPositionLeft = 0F
    private var mEndPositionTop = 0F

    private var mCurrentAlpha = 1F
    private val mDuration = 500L

    private var isFromMoveTo = false

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
    private var mCursorListeners = mutableListOf<(info: CurrentInfo) -> Unit>()
    private var mVisible = true

    private val mCursorHandleRadius = 25F

    init {
        mEditor.getRowsRender().computeAbsolutePos(mCurrentInfo.row, mCurrentInfo.column).let {
            mCoordinateLeft = it.first
            mCoordinateTop = it.second
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
                        mCoordinateLeft,
                        mCoordinateTop + 8,
                        mCoordinateLeft + mCursorWidth,
                        mCoordinateTop + mEditor.getLineHeight() - 8
                    ), mCursorWidth, mCursorWidth, mCursorPaint
                )

                canvas.drawCircle(
                    mCoordinateLeft + mCursorWidth / 2,
                    mCoordinateTop + mEditor.getLineHeight() + mCursorHandleRadius,
                    mCursorHandleRadius,
                    mCursorPaint
                )
            }
        }
    }

    private fun moveCursorByCoordinate(left: Float, top: Float) {
        mCoordinateLeft = left
        mCoordinateTop = top
        mEditor.invalidate()
    }

    fun addCursorChangedListener(callback: (info: CurrentInfo) -> Unit) {
        this.mCursorListeners.add(callback)
    }

    fun moveCursor(
        row: Int = mCurrentInfo.row,
        col: Int = mCurrentInfo.column,
        index: Int? = null
    ) {
        if (row == mCurrentInfo.row && col == mCurrentInfo.column) {
            return
        }
        getTextSequence()?.run {
            val validatedRow = max(0, min(rows() - 1, row))
            val lengthOfCurrentLine = rowAt(validatedRow).length
            val validateCol = max(0, min(lengthOfCurrentLine, col))

            mEditor.getRowsRender().focusRow(row)
            mCurrentInfo.row = validatedRow
            mCurrentInfo.lineLength = lengthOfCurrentLine
            mCurrentInfo.column = validateCol
            if (index == null) {
                mCurrentInfo.index = min(length(), charIndex(row, col))
            } else {
                mCurrentInfo.index = index
            }
            mCurrentInfo.rowNode = getEditContent()?.rowNodeAt(mCurrentInfo.row)
            mCursorListeners.forEach {
                it.invoke(mCurrentInfo)
            }
            val temp =
                mEditor.getRowsRender().computeAbsolutePos(mCurrentInfo.row, mCurrentInfo.column)
            moveCursorByCoordinate(
                temp.first,
                temp.second
            )
        }
    }

    fun moveCursorBy(distance: Int) {
        if (isSelecting) {
            resetSelection()
            return
        }
        val offsetInCol = mCurrentInfo.column + distance
        if (offsetInCol in 0..mCurrentInfo.lineLength) {
            moveCursor(col = offsetInCol, index = mCurrentInfo.index + distance)
        } else {
            moveCursorTo(mCurrentInfo.index + distance)
        }
    }

    fun moveCursorTo(index: Int) {
        if (index < -1 || index >= (getTextSequence()?.length() ?: 0)) {
            return
        } else if (index == -1) {
            moveCursor(0, 0, -1)
        } else {
            getTextSequence()?.getRowAndCol(index)?.run {
                moveCursor(first(), second(), index)
            }
        }
    }

    fun moveLeft() {
        moveCursorBy(-1)
    }

    fun moveUp() {
        if (mCurrentInfo.row != 0) {
            moveCursor(
                mCurrentInfo.row - 1,
                min(
                    getTextSequence()?.rowAt(mCurrentInfo.row - 1)?.length ?: 0,
                    mCurrentInfo.column
                )
            )
        }
    }

    fun moveRight() {
        moveCursorBy(1)
    }

    fun moveDown() {
        if (mCurrentInfo.row != ((getTextSequence()?.rows() ?: 0) - 1)) {
            moveCursor(
                mCurrentInfo.row + 1,
                min(
                    getTextSequence()?.rowAt(mCurrentInfo.row + 1)?.length ?: 0,
                    mCurrentInfo.column
                )
            )
        }
    }

    fun moveToLineEnd() {
        moveCursor(col = Int.MAX_VALUE)
    }

    fun moveToLineStart() {
        moveCursor(col = Int.MIN_VALUE)
    }

    fun moveToStart() {
        moveCursor(row = 0, col = 0, index = -1)
    }

    fun moveToEnd() {
        getTextSequence()?.run {
            moveCursor(row = rows() - 1, col = rowAt(rows() - 1).length - 1, index = length() - 1)
        }
    }

    fun getCurrentLine() = mCurrentInfo.row + 1

    fun getCurrentInfo() = mCurrentInfo

    fun getSelectRange(): IntRange {
        if (mSelectedRange.isEmpty()) {
            return mCurrentInfo.index..mCurrentInfo.index
        }
        return mSelectedRange
    }

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
                        val actualRow = first()
                        val actualCol = second()
                        if (actualRow != mCurrentInfo.row || actualCol != mCurrentInfo.column) {

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
                        if (actualRow != mCurrentInfo.row || actualCol != mCurrentInfo.column) {
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
        mCoordinateLeft - mCursorHandleRadius,
        mCoordinateTop + mEditor.getLineHeight(),
        mCoordinateLeft + mCursorHandleRadius,
        mCoordinateTop + mEditor.getLineHeight() + 2 * mCursorHandleRadius
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
        if (startRow < 0 || startCol < 0 || endRow < 0 || endCol < 0) {
            // Illegal arguments.
            return
        }
        val actualStartRow = min(getTextSequence()?.rows() ?: 0, startRow)
        val actualStartCol = min(getTextSequence()?.rowAt(actualStartRow)?.length ?: 0, startCol)
        val actualEndRow = min(getTextSequence()?.rows() ?: 0, endRow)
        val actualEndCol = min(getTextSequence()?.rowAt(actualEndRow)?.length ?: 0, endCol)

        moveCursor(actualStartRow, actualStartCol)
        if (mSelectedRange.isEmpty()) {
            mSelectedRange = (getTextSequence()?.charIndex(actualStartRow, actualStartCol + 1)
                ?: 0)..(getTextSequence()?.charIndex(actualEndRow, actualEndCol) ?: 0)
        }
        isSelecting = true
        getEditContent()?.addDecoration(this, mSelectionSpan)
        mStartPosition = makePair(actualStartRow, actualStartCol)
        mEditor.getRowsRender().computeAbsolutePos(actualStartRow, actualStartCol).let { pos ->
            mStartPositionLeft = pos.first
            mStartPositionTop = pos.second
        }
        mEndPosition = makePair(actualEndRow, actualEndCol)
        mEditor.getRowsRender().computeAbsolutePos(actualEndRow, actualEndCol).let { pos ->
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

    private fun onCursorChanged(info: CurrentInfo) {
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

    private fun getTextSequence() = getEditContent()?.getTextSequence()

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