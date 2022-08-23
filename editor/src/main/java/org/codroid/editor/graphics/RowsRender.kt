package org.codroid.editor.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import org.codroid.editor.*
import org.codroid.editor.decoration.SpanRect

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
        return makePair(width, height)
    }

    fun drawRows(canvas: Canvas) {
        drawing(canvas)
    }

    private fun drawLineNumber(canvas: Canvas, index: Int) {
        mTextPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            (index + 1).toString(),
            mOffsetX - 20,
            mLineAnchor.baseline,
            mTextPaint.withBlackColor()
        )
        mTextPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawing(canvas: Canvas) {
        mLineAnchor.reset()
        mContent?.forEachIndexed { index, row ->
            if (index == 3) {
                canvas.drawRect(
                    RectF(
                        mOffsetX,
                        index * mTextPaint.getLineHeight(),
                        400F,
                        (index + 1) * mTextPaint.getLineHeight()
                    ), mTextPaint.withColor(Color.LTGRAY)
                )
            }
            drawLineNumber(canvas, index)
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

            // Remeasure when the maximum line width changes.
            if (offsetXinLine > mLongestLineLength) {
                mLongestLineLength = offsetXinLine
                mEditor.requestLayout()
            }
            mLineAnchor.increase()
        }
    }

    fun loadContent(content: EditContent) {
        this.mContent = content
    }
}