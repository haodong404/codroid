package org.codroid.editor

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import org.codroid.editor.graphics.Overlay
import org.codroid.editor.graphics.ScrollOverlayCanvas
import org.codroid.editor.graphics.TextPaint
import kotlin.math.min

class EditorInfoOverlay(private var mInfo: String) : Overlay {

    private val mBackgroundPaint = Paint().apply {
        color = Color.LTGRAY
        textSize = TextPaint.DefaultTextSize
        alpha = 150
        style = Paint.Style.FILL
    }

    private val mTextPaint = TextPaint().apply {
        color = Color.RED
        textSize = 35F
        alpha = 170
        typeface = CodroidEditor.DefaultTypeface
    }

    fun refreshContent(content: String) {
        this.mInfo = content
    }

    override fun onDraw(canvas: ScrollOverlayCanvas) {
        val width = mTextPaint.singleWidth() * 30
        val height = mTextPaint.getLineHeight() * mInfo.lines().size
        val padding = 10F
        canvas.drawRect(
            Rect(
                (canvas.getCanvasWidth() - width - padding - 10F).toInt(),
                0,
                canvas.getCanvasWidth(),
                height.toInt() + 20,
            ), mBackgroundPaint
        )
        mInfo.lines().forEachIndexed { index, it ->
            canvas.drawText(
                it,
                canvas.getCanvasWidth() - width + padding,
                (index + 1) * mTextPaint.getLineHeight(),
                mTextPaint
            )
        }
    }
}