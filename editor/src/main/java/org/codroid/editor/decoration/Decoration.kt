package org.codroid.editor.decoration

import android.graphics.Canvas

interface Decoration {

}

interface Drawable {
    fun onDraw(canvas: Canvas, rect: SpanRect)
}

/**
 * If there are decorations inside the characters, implement this interface.
 * It positioned by the interval in [org.codroid.editor.buffer.TextSequence].
 *
 */
interface SpanDecoration : Decoration {

}

/**
 * If there are decorations that span multiple lines, implement this interface.
 * It positioned by row and column.
 *
 */
interface DynamicDecoration : Decoration, Drawable {

}

/**
 * If there are decorations fixed to the window, implement this interface.
 * It's provided a fixed position.
 *
 */
interface StaticDecoration : Decoration, Drawable {

}

data class SpanRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val baseline: Float
) {
    fun width(): Float = right - left

    fun height(): Float = bottom - top

    fun centerX(): Float = width() / 2

    fun centerY(): Float = height() / 2

}