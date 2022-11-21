package org.codroid.editor.graphics

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import org.codroid.editor.UnrestrainedScrollView
import org.codroid.editor.utils.first
import org.codroid.editor.utils.second

class ScrollOverlayCanvas(private val mUnrestrainedScrollView: UnrestrainedScrollView) {

    private var mBaseCanvas: Canvas? = null

    fun drawRect(rect: Rect, paint: Paint) {
        mBaseCanvas?.run {
            rect.top += getTopNow()
            rect.bottom += getTopNow()
            rect.left += getLeftNow()
            rect.right += getLeftNow()
            drawRect(rect, paint)
        }
    }

    fun drawText(content: String, x: Float, y: Float, paint: Paint) {
        mBaseCanvas?.drawText(content, x + getLeftNow(), y + getTopNow(), paint)
    }

    fun bindBaseCanvas(canvas: Canvas) {
        if (mBaseCanvas !== canvas) {
            mBaseCanvas = canvas
        }
    }

    private fun getLeftNow() = mUnrestrainedScrollView.getScrollCurrent().first()

    private fun getTopNow() = mUnrestrainedScrollView.getScrollCurrent().second()

    fun getCanvasWidth() = mUnrestrainedScrollView.width

    fun getCanvasHeight() = mUnrestrainedScrollView.height

}