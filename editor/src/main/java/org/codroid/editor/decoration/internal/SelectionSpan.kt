package org.codroid.editor.decoration.internal

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.codroid.editor.decoration.BackgroundSpan
import org.codroid.editor.decoration.SpanRect

class SelectionSpan : BackgroundSpan {

    private val mPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

    override fun onDraw(canvas: Canvas, rect: SpanRect) {
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, mPaint)
    }
}