package org.codroid.editor.graphics

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.codroid.editor.*
import org.codroid.editor.decoration.SpanRect
import kotlin.math.ceil

class RowsRender(private val mEditor: CodroidEditor, private var mContent: EditContent? = null) {
    private val mTextPaint = TextPaint().apply {
        typeface = CodroidEditor.DefaultTypeface
    }

    private var mOffsetX = 0F
    private val mLineAnchor = LineAnchor(mTextPaint)
    private var isWrapped = false
    private val mLineHeight = mTextPaint.getLineHeight()
    private var mHighlightLine = 0

    /**
     * It might be changed by [org.codroid.editor.decoration.ReplacementSpan]
     */
    private var mLongestLineLength = 0F

    private var mCurrentHeightLineTop = 0

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
        canvas.drawText(
            index.toString(),
            mOffsetX - 20,
            mLineAnchor.baseline,
            mTextPaint.withBlackColor()
        )
        mTextPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawing(canvas: Canvas) {
        mLineAnchor.resetByRow(mContent?.getVisibleRowsRange()?.getBegin() ?: 0)
        mContent?.forEach { row ->
            if (mLineAnchor.lineNumber == mHighlightLine) {
                drawLineHighlight(canvas)
            }
            drawLineNumber(canvas, mLineAnchor.lineNumber)
            var offsetXinLine = mOffsetX
            row.blocks.forEach { block ->
                var blockWidth = mTextPaint.measureText(block.substring)
                var offset = blockWidth
                var paint = mTextPaint
                if (block.spans != null) {
                    val spanRect by lazy {
                        SpanRect(
                            offsetXinLine, mLineAnchor.top, offsetXinLine + blockWidth,
                            mLineAnchor.bottom, mLineAnchor.baseline
                        )
                    }
                    if (block.spans.background != null) {
                        block.spans.background!!.onDraw(canvas, spanRect)
                    }
                    if (block.spans.foreground != null) {
                        block.spans.foreground!!.onDraw(canvas, spanRect)
                    }
                    if (block.spans.repaint != null) {
                        paint = block.spans.repaint!!.onRepaint(paint)
                        blockWidth = paint.measureText(block.substring)
                    }
                    if (block.spans.replacement != null) {
                        offset = block.spans.replacement!!.onReplacing(
                            canvas, paint, spanRect, block.substring
                        )
                    } else {
                        canvas.drawText(block.substring, offsetXinLine, mLineAnchor.baseline, paint)
                    }
                    offsetXinLine += offset
                } else {
                    canvas.drawText(
                        block.substring,
                        mOffsetX,
                        mLineAnchor.baseline,
                        mTextPaint.withBlackColor()
                    )
                }
            }
            mLineAnchor.increase()
        }
    }

    private fun drawLineHighlight(canvas: Canvas) {
        canvas.drawRect(
            mOffsetX,
            mCurrentHeightLineTop.toFloat(),
            mLongestLineLength,
            mCurrentHeightLineTop + mLineAnchor.height(),
            mTextPaint.withColor(getHighlightColor())
        )
    }

    private fun getHighlightColor(): Int = Color.LTGRAY

    fun focusRow(line: Int, originalPosition: IntPair) {
        if (line != this.mHighlightLine) {
            ValueAnimator.ofInt(
                computeAbsolutePos(mHighlightLine, 0).first.toInt(),
                computeAbsolutePos(line, 0).first.toInt()
            ).run {
                duration = 300
                addUpdateListener {
                    mCurrentHeightLineTop = animatedValue as Int
                    mEditor.postInvalidateOnAnimation()
                }
                start()
            }
            this.mHighlightLine = line
        }
    }

    fun loadContent(content: EditContent) {
        this.mContent = content
    }

    fun computeRowCol(position: IntPair): IntPair {
        val row = ceil(position.first() / mLineAnchor.height()).toInt() - 1
        val col =
            ceil((position.second() - lineNumberOffset()) / mTextPaint.singleWidth()).toInt() - 1
        return makePair(row, col)
    }

    fun computeAbsolutePos(row: Int, col: Int): Pair<Float, Float> {
        return row * mEditor.getLineHeight() to
                col * mEditor.getSingleCharWidth() + mEditor.getRowsRender().lineNumberOffset()
    }

    fun lineNumberOffset() = mOffsetX

    fun getLineHeight() = mTextPaint.getLineHeight()

    fun getSingleCharWidth() = mTextPaint.singleWidth()
}