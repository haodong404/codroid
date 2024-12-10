package org.codroid.editor.decoration

import android.graphics.Canvas
import android.graphics.Paint

interface ReplacementSpan : SpanDecoration {
    fun onReplacing(canvas: Canvas, paint: Paint, rect: SpanRect, content: String): Float
}