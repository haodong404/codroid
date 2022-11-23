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
import org.codroid.editor.utils.*
import kotlin.math.max
import kotlin.math.min

class Cursor(private val mEditor: CodroidEditor) {

    companion object {
        const val TAG = "Cursor"
    }

    data class CurrentInfo(
        var index: Int = -1,
        var row: Int = 0,
        var column: Int = 0,
        var lineLength: Int = 0,
        var rowNode: RowNode? = null,
        var isSelecting: Boolean = false,
        var selectedRange: IntRange = IntRange.EMPTY,
        var selectedStart: IntPair = 0U, // the start row and column, inclusive
        var selectedEnd: IntPair = 0U // the end row and column, inclusive
    ) {
        fun resetSelection() {
            if (isSelecting) {
                selectedRange = IntRange.EMPTY
                selectedStart = 0u
                selectedEnd = 0u
                isSelecting = false
            }
        }

        override fun toString(): String {
            return "CurrentInfo(index=$index, row=$row, column=$column, lineLength=$lineLength, rowNode=$rowNode, isSelecting=$isSelecting, selectedRange=$selectedRange, selectedStart=$selectedStart, selectedEnd=$selectedEnd)"
        }

        fun toPrettyString(): String {
            return "CURSOR INFO: ${if (isSelecting) "SELECTING" else ""}\n" +
                    "--------------------\n" +
                    "Index: $index\n" +
                    "Row: $row, Column: $column\n" +
                    "Length of line: $lineLength\n" +
                    "Selected start: ${selectedRange.first}(${selectedStart.first()}, ${selectedStart.second()})\n" +
                    "Selected end: ${selectedRange.last}(${selectedEnd.first()}, ${selectedEnd.second()})\n" +
                    "Selected length: ${selectedRange.length()}\n" +
                    "---------------------"
        }
    }

    private val mCursorPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        isAntiAlias = true
    }

    private val mCurrentInfo: CurrentInfo = CurrentInfo()
    private val mCursorWidth = 4F

    private var mCoordinateLeft = 0F
    private var mCoordinateTop = 0F

    // Determine which handle is held. -1: unknown, 1: start, 0: end
    private var isMovingStartHandle = -1;

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
    private var mCursorListeners = mutableListOf<(info: CurrentInfo) -> Unit>()
    private var mVisible = true

    private val mCursorHandleRadius = 25F

    init {

        mEditor.lifecycleScope.launchWhenCreated {
            mBlinkingTimer.start()
        }
    }

    fun measure() {
        mEditor.getRowsRender().computeAbsolutePos(mCurrentInfo.row, mCurrentInfo.column).let {
            mCoordinateLeft = it.first
            mCoordinateTop = it.second
        }
    }

    fun drawCursor(canvas: Canvas) {
        if (mVisible) {
            mCursorPaint.color = Color.argb(mCurrentAlpha, 1F, 0F, 0F)
            if (getCurrentInfo().isSelecting) {
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
        if (row == mCurrentInfo.row && col == mCurrentInfo.column && mCurrentInfo.index == index) {
            return
        }
        getTextSequence()?.run {
            val validatedRow = max(0, min(rows() - 1, row))
            val lengthOfCurrentLine = rowAt(validatedRow).length
            val validateCol = max(0, min(lengthOfCurrentLine, col))

            mEditor.getRowsRender().focusRow(row)
            mCurrentInfo.row = validatedRow
            mCurrentInfo.column = validateCol
            mCurrentInfo.lineLength = lengthOfCurrentLine
            if (index == null) {
                mCurrentInfo.index = min(length(), charIndex(validatedRow, validateCol))
            } else {
                mCurrentInfo.index = index
            }
            mCurrentInfo.rowNode = getEditContent()?.rowNodeAt(mCurrentInfo.row)
            val temp =
                mEditor.getRowsRender().computeAbsolutePos(mCurrentInfo.row, mCurrentInfo.column)
            mCurrentInfo.resetSelection()
            moveCursorByCoordinate(
                temp.first,
                temp.second
            )
            mCursorListeners.forEach {
                it.invoke(mCurrentInfo)
            }
        }
    }

    fun moveCursorBy(distance: Int, newLine: Boolean = false) {
        val offsetInCol = mCurrentInfo.column + distance
        if (offsetInCol in 0..mCurrentInfo.lineLength && !
            newLine
        ) {
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
        if (isSelecting()) {
            stopSelecting()
        } else {
            moveCursorBy(-1)
        }
    }

    fun moveUp() {
        if (isSelecting()) {
            stopSelecting()
        } else if (mCurrentInfo.row != 0) {
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
        if (isSelecting()) {
            stopSelecting()
        } else {
            moveCursorBy(1)
        }
    }

    fun moveDown() {
        if (isSelecting()) {
            stopSelecting()
        } else if (mCurrentInfo.row != ((getTextSequence()?.rows() ?: 0) - 1)) {
            moveCursor(
                mCurrentInfo.row + 1,
                min(
                    getTextSequence()?.rowAt(mCurrentInfo.row + 1)?.length ?: 0,
                    mCurrentInfo.column
                )
            )
        }
    }

    fun stopSelecting() {
        if (isSelecting()) {
            moveCursor(
                mCurrentInfo.selectedStart.first(),
                mCurrentInfo.selectedStart.second() - 1,
                mCurrentInfo.selectedRange.first - 1
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

    fun getSelectedRange(): IntRange {
        if (!mCurrentInfo.isSelecting) {
            return mCurrentInfo.index..mCurrentInfo.index
        }
        return mCurrentInfo.selectedRange
    }

    fun isSelecting() = mCurrentInfo.isSelecting

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
                if (mCurrentInfo.isSelecting) {
                    mEditor.getRowsRender().computeRowCol(event.x, event.y).run {
                        val actualRow = first()
                        val actualCol = second()
                        if (actualRow != mCurrentInfo.row || actualCol != mCurrentInfo.column) {
                            var isFlipped = false
                            if (isMovingStartHandle == 1) {
                                if (actualRow > mCurrentInfo.selectedEnd.first() || (actualRow == mCurrentInfo.selectedEnd.first() && actualCol > mCurrentInfo.selectedEnd.second())) {
                                    isFlipped = true
                                    isMovingStartHandle = 0
                                }
                            } else {
                                if (actualRow < mCurrentInfo.selectedStart.first() || (actualRow == mCurrentInfo.selectedStart.first() && actualCol < mCurrentInfo.selectedStart.second())) {
                                    isFlipped = true
                                    isMovingStartHandle = 1
                                }
                            }

                            if (isMovingStartHandle == 1) {
                                val endPosition = if (isFlipped) {
                                    makePair(
                                        mCurrentInfo.selectedStart.first(),
                                        mCurrentInfo.selectedStart.second() - 1
                                    )
                                } else {
                                    mCurrentInfo.selectedEnd
                                }
                                select(
                                    actualRow, actualCol,
                                    endPosition.first(), endPosition.second()
                                )
                            } else {
                                val startPosition = if (isFlipped) {
                                    makePair(
                                        mCurrentInfo.selectedEnd.first(),
                                        mCurrentInfo.selectedEnd.second() + 1
                                    )
                                } else {
                                    mCurrentInfo.selectedStart
                                }
                                select(
                                    startPosition.first(), startPosition.second(),
                                    actualRow, actualCol
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

    fun select(
        startRow: Int,
        startCol: Int,
        endRow: Int,
        endCol: Int,
        indexRange: IntRange? = null
    ) {
        if (startRow < 0 || startCol < 0 || endRow < 0 || endCol < 0) {
            // Illegal arguments.
            return
        }
        val actualStartRow = min(getTextSequence()?.rows() ?: 0, startRow)
        val actualStartCol =
            max(1, min(getTextSequence()?.rowAt(actualStartRow)?.length ?: 0, startCol))
        val actualEndRow = min(getTextSequence()?.rows() ?: 0, endRow)
        val actualEndCol = min(getTextSequence()?.rowAt(actualEndRow)?.length ?: 0, endCol)

        mCurrentInfo.row = -1
        mCurrentInfo.column = -1
        mCurrentInfo.isSelecting = true
        if (indexRange != null) {
            mCurrentInfo.selectedRange = indexRange
        } else {
            getTextSequence()?.let {
                mCurrentInfo.selectedRange =
                    it.charIndex(actualStartRow, actualStartCol)..it.charIndex(
                        actualEndRow,
                        actualEndCol
                    )
            }
        }

        mCurrentInfo.index = mCurrentInfo.selectedRange.first
        mCurrentInfo.selectedStart = makePair(actualStartRow, actualStartCol)
        // The reason for using actualStartCol minus 1 to calculate the absolute position of the first cursor is
        // that the left cursor is to the left of a character when selected.
        mEditor.getRowsRender().computeAbsolutePos(actualStartRow, actualStartCol - 1).let { pos ->
            mStartPositionLeft = pos.first
            mStartPositionTop = pos.second
        }
        mCurrentInfo.selectedEnd = makePair(actualEndRow, actualEndCol)
        mEditor.getRowsRender().computeAbsolutePos(actualEndRow, actualEndCol).let { pos ->
            mEndPositionLeft = pos.first
            mEndPositionTop = pos.second
        }
        mCurrentInfo.selectedRange
        mCursorListeners.forEach {
            it.invoke(mCurrentInfo)
        }
        mEditor.invalidate()
    }

    fun select(start: Int, end: Int) {
        if (start > end) {
            return
        }
        val length = getTextSequence()?.length() ?: 0
        val actualStart = max(0, min(length - 1, start))
        val actualEnd = max(0, min(length - 1, end))

        var startRow = 0
        var startCol = 0
        var endRow = 0
        var endCol = 0
        getTextSequence()?.getRowAndCol(actualStart)?.let {
            startRow = it.first()
            startCol = it.second()
        }
        getTextSequence()?.getRowAndCol(actualEnd)?.let {
            endRow = it.first()
            endCol = it.second()
        }
        select(startRow, startCol, endRow, endCol, actualStart..actualEnd)
    }

    private fun resetSelection() {
        mCurrentInfo.resetSelection()
        mEditor.invalidate()
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