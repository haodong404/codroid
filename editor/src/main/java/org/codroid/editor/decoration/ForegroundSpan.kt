package org.codroid.editor.decoration

import android.graphics.Canvas

interface ForegroundSpan : SpanDecoration, Drawable {
    override fun onDraw(canvas: Canvas, rect: SpanRect)
}