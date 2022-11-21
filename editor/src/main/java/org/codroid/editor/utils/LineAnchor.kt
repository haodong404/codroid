package org.codroid.editor.utils

import org.codroid.editor.graphics.TextPaint


class LineAnchor(paint: TextPaint) {
    private var mLineHeight = paint.getLineHeight()
    private val mBaselineHeight = paint.getBaselineHeight()
    var top = 0F
    var bottom = mLineHeight
    var baseline = mBaselineHeight
    var lineNumber = 1

    fun height(): Float = bottom - top

    fun increase() {
        this.top = bottom
        this.bottom += mLineHeight
        this.baseline = top + mBaselineHeight
        this.lineNumber++
    }

    fun reset() {
        resetByRow(0)
    }

    fun resetByRow(row: Int) {
        this.lineNumber = row + 1
        this.top = row * mLineHeight
        this.bottom = (row + 1) * mLineHeight
        this.baseline = (row + 1) * mBaselineHeight
    }
}