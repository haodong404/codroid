package org.codroid.editor.decoration

import org.codroid.editor.graphics.TextPaint

interface RepaintSpan : SpanDecoration {
    fun onRepaint(origin: TextPaint): TextPaint
}