package org.codroid.body.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.codroid.body.R
import org.codroid.body.dip2px
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.appearance.AppearanceProperty
import org.codroid.interfaces.appearance.Part
import org.codroid.interfaces.appearance.editor.WrappedSpannable
import org.codroid.interfaces.appearance.parts.EditorPart
import org.codroid.interfaces.evnet.EventCenter
import org.codroid.interfaces.evnet.editor.SelectionChangedEvent
import org.codroid.interfaces.evnet.editor.TextChangedEvent
import java.io.FileReader
import java.io.LineNumberReader
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.*

class CodroidEditor : AppCompatEditText {

    private val mConfigure: Configure by lazy {
        Configure(
            lineNumberEnable = true
        )
    }

    private var mWrappedSpannable: WrappedSpannable
    private var mDelegate: EditorDelegate = EditorDelegate(this)

    private val mLineNumberPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            textSize = this@CodroidEditor.textSize
            color = mConfigure.lineNumberColor
        }
    }

    private val mBackPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = mConfigure.lineNumberBackColor
        }
    }

    private val lineNumberBackRect: Rect by lazy {
        Rect()
    }

    // This is suggestion window
    private val mPopupWindow: PopupWindow by lazy {
        PopupWindow(
            inflate(context, R.layout.suggestion_window, null),
            ViewGroup.LayoutParams.MATCH_PARENT,
            300
        )
    }

    // The part of editor that defines the appearances
    private var mPart: Optional<Part> = AddonManager.get().appearancePart(AppearanceProperty.PartEnum.EDITOR)

    private var mPaddingLeft = 0F

    // Left margin of main text area if line number is enable.
    private var mGap = 0F

    // return true if editor is drawn at first time.
    // private val isDrawn = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {}

    init {
        if (mConfigure.lineNumberEnable) {
            mPart.ifPresent {
                it.findColor(EditorPart.Attribute.BACKGROUND) { it2 ->
                    setBackgroundColor(it2.toArgb())
                }
            }
            textSize = context.dip2px(4F)
        }
        mWrappedSpannable = WrappedSpannable(text)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        MeasureSpec.getSize(widthMeasureSpec).let {
//            super.onMeasure(
//                MeasureSpec.makeMeasureSpec(it, MeasureSpec.UNSPECIFIED),
//                heightMeasureSpec
//            )
//        }
//    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        super.onLayout(changed, 20, top, 100, bottom)
        if (mConfigure.lineNumberEnable) {
            mGap = context.dip2px(4F)
            mPaddingLeft = mLineNumberPaint.measureText(lineCount.toString()) + mGap * 2
            setPadding((mGap + mPaddingLeft).toInt(), 0, 0, 0)
        }
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (event?.action == MotionEvent.ACTION_MOVE) return false
//        return super.onTouchEvent(event)
//    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mConfigure.lineNumberEnable) {
            lineNumberBackRect.set(0, 0, mPaddingLeft.toInt(), MeasureSpec.getSize(measuredHeight))
            canvas.drawRect(lineNumberBackRect, mBackPaint)
            for (i in 0 until lineCount) {
                val baseLine = getLineBounds(i, null)
                canvas.drawText(
                    (i + 1).toString(),
                    mGap,
                    baseLine.toFloat(),
                    mLineNumberPaint
                )
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        AddonManager.get().eventCenter()
            .execute<SelectionChangedEvent>(EventCenter.EventsEnum.EDITOR_SELECTION_CHANGED)
            .forEach {
                try {
                    it.onSelectionChanged(mWrappedSpannable.update(text), selStart, selEnd)
                } catch (e: Exception) {
                    AddonManager.get().logger.e("Event editor_selection_changed executes failed with: $e")
                }
            }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        AddonManager.get().eventCenter()
            .execute<TextChangedEvent>(EventCenter.EventsEnum.EDITOR_TEXT_CHANGED)
            .forEach {
                try {
                    it.onTextChanged(
                        mWrappedSpannable.update(getText()),
                        start,
                        lengthBefore,
                        lengthAfter
                    )
                } catch (e: Exception) {
                    AddonManager.get().logger.e("Event editor_text_changed executes failed with: $e")
                }
            }
    }

    fun showSuggestionWindow() {
        mPopupWindow.dismiss()
        mPopupWindow.showAtLocation(
            this, Gravity.TOP or Gravity.START, 0,
            getLineBounds(getCurrentCursorLine() - 1, null) + mPopupWindow.height + lineHeight + 30
        )
    }

    suspend fun fileInput(path: Path, charset: Charset) {
        setText(streamToString(path))
    }

    private suspend fun streamToString(path: Path): String {
        path.toFile().let {
            val buffer = StringBuilder()
            withContext(Dispatchers.IO) {
                LineNumberReader(FileReader(path.toFile())).apply {
                    var i = 0
                    lines().forEach { line ->
                        buffer.append(line).append("\n")
                        if (i > 50) {
                            return@forEach
                        }
                        i++
                        Log.i("Zac", this.lineNumber.toString())
                    }
                }
            }
            return buffer.toString()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mDelegate.recycle()
    }

    private fun getCurrentCursorLine(): Int {
        val selectionStart = selectionStart
        return if (selectionStart != -1) {
            layout.getLineForOffset(selectionStart) + 1
        } else -1
    }

    data class Configure(
        val lineNumberEnable: Boolean,
        @ColorInt val lineNumberColor: Int = Color.BLACK,
        @ColorInt val lineNumberBackColor: Int = Color.GRAY,
    )
}
