package org.codroid.editor.graphics

import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs


class TextPaint : Paint() {
    companion object {
        fun default(): TextPaint = TextPaint().apply {
            textSize = 40F
            style = Style.FILL
            color = Color.BLACK
            isAntiAlias = true
            return this
        }
    }


    fun getStandardHeight(): Float {
        return abs(fontMetrics.leading + fontMetrics.ascent) + fontMetrics.descent
    }

    fun singleWidth(): Float {
        return measureText(" ")
    }

    fun withBlackColor(): TextPaint = withColor(Color.BLACK)

    fun withColor(color: Int): TextPaint = this.apply {
        this.color = color
    }
}