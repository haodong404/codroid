package org.codroid.editor.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.codroid.editor.*
import org.codroid.editor.decoration.SpanRect
import org.codroid.editor.utils.*
import kotlin.math.ceil
import kotlin.math.max

class RowsRender(private val mEditor: CodroidEditor, private var mContent: EditContent? = null) {
    private val mTextPaint = TextPaint().apply {
        typeface = CodroidEditor.DefaultTypeface
    }

    private var mOffsetX = 0F
    private val mLineAnchor = LineAnchor(mTextPaint)
    private var isWrapped = false
    private val mLineHeight = mTextPaint.getLineHeight()

    /**
     * It might be changed by [org.codroid.editor.decoration.ReplacementSpan]
     */
    private var mLongestLineLength = 0F

    fun measure(): IntPair {
        mOffsetX = mTextPaint.measureText(mContent?.rows()?.toString() ?: "0") + 40
        mLongestLineLength = (mContent?.longestLineLength() ?: 0) * mTextPaint.singleWidth()
        var width = 0
        var height = 0
        mContent?.let {
            width = mLongestLineLength.toInt()
            height = it.rows() * mLineHeight.toInt()
        }
        return makePair(width, height + 40)
    }

    fun drawRows(canvas: Canvas) {
        drawing(canvas)
    }

    private fun drawLineNumber(canvas: Canvas, index: Int) {
        mTextPaint.textAlign = Paint.Align.RIGHT
        var tempPaint = mTextPaint.withBlackColor()
        if (index == mEditor.getCursor().getCurrentLine()) {
            tempPaint = mTextPaint.withColor(Color.RED)
        }
        canvas.drawText(
            index.toString(),
            mOffsetX - 20,
            mLineAnchor.baseline,
            tempPaint
        )
        mTextPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawing(canvas: Canvas) {
        mLineAnchor.resetByRow(mContent?.getVisibleRowsRange()?.getBegin() ?: 0)
        mContent?.forEach { row ->
            if (mLineAnchor.lineNumber == mEditor.getCursor().getCurrentLine()) {
                drawLineHighlight(canvas)
            }
            drawLineNumber(canvas, mLineAnchor.lineNumber)
            drawSelection(canvas, row)
            var offsetXinLine = mOffsetX
            row.blocks.forEach { block ->
                var blockWidth = mTextPaint.measureText(block.getSubstring())
                var offset = blockWidth
                var paint = mTextPaint
                block.getAssembledSpans()?.run {
                    val spanRect by lazy {
                        SpanRect(
                            offsetXinLine, mLineAnchor.top, offsetXinLine + blockWidth,
                            mLineAnchor.bottom, mLineAnchor.baseline
                        )
                    }
                    background.forEach { it.onDraw(canvas, spanRect) }
                    foreground.forEach { it.onDraw(canvas, spanRect) }
                    repaint?.run {
                        paint = onRepaint(paint)
                        blockWidth = paint.measureText(block.getSubstring())
                    }
                    if (replacement.isNotEmpty()) {
                        offset = replacement.last.onReplacing(
                            canvas,
                            paint,
                            spanRect,
                            block.getSubstring()
                        )
                    } else {
                        canvas.drawText(
                            block.getSubstring(),
                            offsetXinLine,
                            mLineAnchor.baseline,
                            paint
                        )
                    }
                } ?: canvas.drawText(
                    block.getSubstring(),
                    offsetXinLine,
                    mLineAnchor.baseline,
                    mTextPaint.withBlackColor()
                )
                offsetXinLine += offset
            }
            mLineAnchor.increase()
        }
    }

    private fun drawSelection(canvas: Canvas, row: Row) {
        if (row.selection != 0UL && mEditor.getCursor().isSelecting()) {
            val left = mTextPaint.singleWidth() * row.selection.first()
            val right = if (row.selection.second() != -1) {
                mTextPaint.singleWidth() * row.selection.second()
            } else {
                mEditor.width.toFloat()
            }
            canvas.drawRect(
                left + mOffsetX,
                mLineAnchor.top,
                right + mOffsetX,
                mLineAnchor.bottom,
                mTextPaint.withColor(Color.LTGRAY)
            )
        }
    }

    private fun drawLineHighlight(canvas: Canvas) {
        canvas.drawRect(
            mOffsetX,
            mEditor.getCursor().getCurrentInfo().row * getLineHeight(),
            mLongestLineLength,
            (mEditor.getCursor().getCurrentInfo().row + 1) * mLineAnchor.height(),
            mTextPaint.withColor(getHighlightColor())
        )
    }

    private fun getHighlightColor(): Int = Color.argb(0xA0, 0xDC, 0xDC, 0xDC)

    fun focusRow(row: Int) {
        // Do something like animation here if needed.
    }

    fun loadContent(content: EditContent) {
        this.mContent = content
    }

    fun computeRowCol(x: Float, y: Float): IntPair {
        val row = ceil(y / mLineAnchor.height()).toInt() - 1
        val col =
            ceil((x - lineNumberOffset()) / mTextPaint.singleWidth()).toInt()
        return makePair(row, max(0, col))
    }

    /**
     * Returns the absolute position of specific row and col.
     *
     * @param row row
     * @param col col
     * @return a pair of x and y
     */
    fun computeAbsolutePos(row: Int, col: Int): Pair<Float, Float> {
        return col * mEditor.getSingleCharWidth() + mEditor.getRowsRender().lineNumberOffset() to
                row * mEditor.getLineHeight()
    }

    fun lineNumberOffset() = mOffsetX

    fun getLineHeight() = mLineAnchor.height()

    fun getSingleCharWidth() = mTextPaint.singleWidth()
}