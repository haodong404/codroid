/*
 *     Copyright (c) 2022 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.editor

import android.content.Context
import android.graphics.*
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.codroid.editor.analysis.*
import org.codroid.editor.buffer.linearr.LineArray
import org.codroid.editor.decoration.*
import org.codroid.editor.graphics.TextPaint
import org.codroid.textmate.parseJson
import org.codroid.textmate.parsePLIST
import org.codroid.textmate.parseRawGrammar
import org.codroid.textmate.theme.RawTheme
import java.io.InputStream
import java.nio.file.Path

class CodroidEditor : View, View.OnClickListener, LifecycleOwner {

    init {
//        setOnClickListener(this)
        isFocusable = true
        isFocusableInTouchMode = true
//        showInput()
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    private val mLifecycleRegistry = LifecycleRegistry(this)

    private val mInputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private var mEditContent: EditContent? = null

    private var mOffsetX = 0F

    private val mLineAnchor by lazy { LineAnchor(mTextPaint) }

    private var isWrapped = false

    private val mTextPaint = TextPaint.default.apply {
        typeface = this.typeface
    }

    companion object {
        private var DefaultRawTheme: RawTheme? = null
        private var DefaultTypeface: Typeface? = null
    }

    var theme: RawTheme? = null
        get() {
            return DefaultRawTheme
        }

    var typeface: Typeface? = null
        get() {
            return DefaultTypeface
        }

    init {
        lifecycleScope.launch {
            if (DefaultRawTheme == null) {
                val file = "Solarized-light.tmTheme" // Default Theme
                context.assets.open(file).use { input ->
                    if (Regex("\\.json$").containsMatchIn(file)) {
                        DefaultRawTheme = parseJson(input)
                    }
                    DefaultRawTheme = parsePLIST(input)
                }
            }
        }

        lifecycleScope.launch {
            val file = "Kotlin.tmLanguage" // kotlin grammar
            context.assets.open(file).use {
                registerGrammar(
                    GrammarRegistration(
                        language = "kotlin",
                        scopeName = "source.kotlin",
                        grammar = parseRawGrammar(it, file)
                    )
                )
            }

            val c = "c.json"
            context.assets.open(c).use {
                registerGrammar(
                    GrammarRegistration(
                        language = "c",
                        scopeName = "source.c",
                        grammar = parseRawGrammar(it, c)
                    )
                )
            }

            val cpp = "c++.json"
            context.assets.open(cpp).use {
                registerGrammar(
                    GrammarRegistration(
                        language = "c++",
                        scopeName = "source.cpp",
                        grammar = parseRawGrammar(it, cpp)
                    )
                )
            }
        }

        lifecycleScope.launch {
            registerLanguage(
                LanguageRegistration(
                    id = "kotlin",
                    extensions = listOf(".kt")
                )
            )
            registerLanguage(
                LanguageRegistration(
                    id = "c++",
                    extensions = listOf(".cpp", ".cc")
                )
            )
            registerLanguage(
                LanguageRegistration(
                    id = "c",
                    extensions = listOf(".c")
                )
            )
        }

        lifecycleScope.launch {
            if (DefaultTypeface == null) {
                DefaultTypeface =
                    Typeface.createFromAsset(context.assets, "CascadiaCodePL-Regular.ttf")
            }
            typeface = DefaultTypeface
            mTextPaint.typeface = typeface
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mOffsetX = mTextPaint.measureText(mEditContent?.rows()?.toString() ?: "0") + 40
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        mEditContent?.let {
            height = it.rows() * mTextPaint.getLineHeight().toInt()
            width = it.longestLineSize() * mTextPaint.singleWidth().toInt()
        }
        setMeasuredDimension(width * 3, height * 3)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawRows(it)
        }
    }

    private fun drawLineNumber(canvas: Canvas, index: Int) {
        mTextPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            (index + 1).toString(),
            mOffsetX - 20,
            mLineAnchor.baseline,
            mTextPaint.withBlackColor()
        )
        mTextPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawRows(canvas: Canvas) {
        mLineAnchor.reset()
        mEditContent?.forEachIndexed { index, row ->
            if (index == 3) {
                canvas.drawRect(
                    RectF(
                        mOffsetX,
                        index * mTextPaint.getLineHeight(),
                        measuredWidth.toFloat(),
                        (index + 1) * mTextPaint.getLineHeight()
                    ), mTextPaint.withColor(Color.LTGRAY)
                )
            }
            drawLineNumber(canvas, index)
            var offsetXinLine = mOffsetX
            row.blocks.forEach { block ->
                var blockWidth = mTextPaint.measureText(block.substring)
                var offset = blockWidth
                var paint = mTextPaint
                if (block.spans != null) {
                    val spanRect by lazy {
                        SpanRect(
                            offsetXinLine, mLineAnchor.top, offsetXinLine + blockWidth,
                            mLineAnchor.bottom, mLineAnchor.baseline
                        )
                    }
                    if (block.spans.background != null) {
                        block.spans.background!!.onDraw(canvas, spanRect)
                    }
                    if (block.spans.foreground != null) {
                        block.spans.foreground!!.onDraw(canvas, spanRect)
                    }
                    if (block.spans.repaint != null) {
                        paint = block.spans.repaint!!.onRepaint(paint)
                        blockWidth = paint.measureText(block.substring)
                    }
                    if (block.spans.replacement != null) {
                        offset = block.spans.replacement!!.onReplacing(
                            canvas, paint, spanRect, block.substring
                        )
                    } else {
                        canvas.drawText(block.substring, offsetXinLine, mLineAnchor.baseline, paint)
                    }
                    offsetXinLine += offset
                } else {
                    canvas.drawText(
                        block.substring,
                        mOffsetX,
                        mLineAnchor.baseline,
                        mTextPaint.withBlackColor()
                    )
                }
            }
            mLineAnchor.increase()
        }
    }

    override fun onClick(p0: View?) {
        showInput()
    }

    fun load(input: InputStream, path: Path) {
        mEditContent = EditContent(LineArray(input), path, this)
        lifecycleScope.launch(Dispatchers.Main) {
            requestLayout()
            invalidate()
        }

    }


    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs?.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        outAttrs?.inputType = InputType.TYPE_NULL
        return InputConnection(this, true)
    }

    fun showInput() {
        mInputMethodManager.showSoftInput(this@CodroidEditor, InputMethodManager.SHOW_FORCED)
    }

    fun closeInput() {
        mInputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }

    inner class InputConnection(targetView: View, fullEditor: Boolean) :
        BaseInputConnection(targetView, fullEditor) {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            this@CodroidEditor.postInvalidate()
            return true
        }

        override fun closeConnection() {
            super.closeConnection()
            Log.i("Zac", "CloseConnection")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            requestLayout()
            invalidate()
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle = this.mLifecycleRegistry
}
