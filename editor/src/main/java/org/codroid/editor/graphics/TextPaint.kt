package org.codroid.editor.graphics

import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs


class TextPaint : Paint() {

    companion object {
        const val DefaultTextSize = 50F
        val default: TextPaint by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TextPaint()
        }
    }

    init {
        color = Color.BLACK
        textSize = DefaultTextSize
        isAntiAlias = true
        style = Style.FILL
        fontMetrics.leading = 10F
    }

    fun getBaselineHeight(): Float {
        return abs(fontMetrics.leading + fontMetrics.ascent) + 3F
    }

    fun getLineHeight(): Float {
        fontMetrics.run {
            return abs(leading + ascent) + descent + 10F
        }
    }

    fun singleWidth(): Float {
        return measureText(" ")
    }

    fun withBlackColor(): TextPaint = withColor(Color.BLACK)

    fun withColor(color: Int): TextPaint = this.apply {
        this.color = color
    }
}