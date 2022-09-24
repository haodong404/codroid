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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.codroid.editor.analysis.GrammarRegistration
import org.codroid.editor.analysis.LanguageRegistration
import org.codroid.editor.analysis.registerGrammar
import org.codroid.editor.analysis.registerLanguage
import org.codroid.editor.algorithm.linearr.LineArray
import org.codroid.editor.graphics.Cursor
import org.codroid.editor.graphics.RowsRender
import org.codroid.editor.utils.first
import org.codroid.editor.utils.second
import org.codroid.textmate.parseJson
import org.codroid.textmate.parsePLIST
import org.codroid.textmate.parseRawGrammar
import org.codroid.textmate.theme.RawTheme
import java.io.InputStream
import java.nio.file.Path
import kotlin.math.ceil

class CodroidEditor : View, LifecycleOwner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    private val mLifecycleRegistry = LifecycleRegistry(this)

    private val mInputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private var mEditContent: EditContent? = null
    private val mRowsRender by lazy {
        RowsRender(this)
    }

    private val mCursor by lazy {
        Cursor(this)
    }

    private var mVisibleRows = 0
    private val mInputConnection by lazy {
        CodroidInputConnection(this, true)
    }

    companion object {
        var DefaultRawTheme: RawTheme? = null
        var DefaultTypeface: Typeface? = null
    }

    val theme: RawTheme?
        get() {
            return DefaultRawTheme
        }

    var typeface: Typeface?
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

        if (DefaultTypeface == null) {
            DefaultTypeface = Typeface.createFromAsset(context.assets, "CascadiaCodePL-Regular.ttf")
        }
        typeface = DefaultTypeface
        lifecycleScope.launchWhenCreated {
            getParentAsUnrestrainedScroll()?.run {
                setOnScrollWithRowListener { start, old ->
                    mEditContent?.getVisibleRowsRange()?.let {
                        it.bindScroll(start, old)
                        if (getCursor().getCurrentRow() in it.getBegin()..it.getEnd()) {
                            getCursor().show()
                        } else {
                            getCursor().hide()
                        }
                    }
                }
            }
            getCursor().addCursorChangedListener { row, col ->

            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mRowsRender.measure().run {
            setMeasuredDimension(first(), second())
        }
        mVisibleRows =
            ceil(MeasureSpec.getSize(heightMeasureSpec) / mRowsRender.getLineHeight()).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            mRowsRender.drawRows(canvas)
            mCursor.drawCursor(canvas)
        }
    }

    private var mActionDownStartTime = 0L
    private var mLongPressJob: Job? = null
    private var mClickCounter = 0
    private var mFirstClickTime = 0L

    // true if cursor has intercepted the scroll event.
    private var isCursorIntercepted = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            if (isCursorIntercepted) {
                getCursor().handleCursorHandleTouchEvent(event)
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mActionDownStartTime = System.currentTimeMillis()
                    if (mLongPressJob == null) {
                        mLongPressJob = lifecycleScope.launch {
                            delay(500)
                            onLongPress(event.x, event.y)
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isCursorIntercepted) {
                        mLongPressJob?.cancel()
                        mLongPressJob = null
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    mLongPressJob?.cancel()
                    mLongPressJob = null
                }
                MotionEvent.ACTION_UP -> {
                    mLongPressJob?.cancel()
                    mLongPressJob = null
                    mClickCounter++
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - mActionDownStartTime < 300) {
                        if (mClickCounter >= 2 && currentTime - mFirstClickTime < 200) {
                            onDoubleClick(x, y)
                            mClickCounter = 0
                        } else {
                            mFirstClickTime = currentTime
                            onClick(event.x, event.y)
                        }
                    }
                }
            }
        }
        return true
    }

    private fun onClick(x: Float, y: Float) {
        if (x >= mRowsRender.lineNumberOffset()) {
            mRowsRender.computeRowCol(x, y).run {
                mCursor.moveCursor(this.first(), this.second())
                mCursor.show()
            }
            showInput()
        }
    }

    private fun onLongPress(x: Float, y: Float) {
        Log.i("Zac", "OnLongClicked")
        Toast.makeText(this.context, "onLongClick", Toast.LENGTH_SHORT).show()
        select(x, y)
    }

    /**
     * Called when double clicked. Because I don't want to have a delay in clicking on events,
     * So it doesn't discard the first click event. That means it will trigger a click event when you double clicked.
     *
     * @param x the position of x
     * @param x the position of y
     */
    private fun onDoubleClick(x: Float, y: Float) {
        Log.i("Zac", "onDoubleClick")
        Toast.makeText(this.context, "onDoubleClick", Toast.LENGTH_SHORT).show()
        select(x, y)
    }

    private fun select(x: Float, y: Float) {
        val regex = Regex("\\W")
        getRowsRender().computeRowCol(x, y).run {
            getEditContent()?.let {
                val line = it.getTextSequence().rowAt(first())
                val startDeffer = lifecycleScope.async {
                    for (i in second() downTo 0) {
                        if (regex.containsMatchIn(line[i].toString())) {
                            return@async i + 1
                        }
                    }
                    return@async -1
                }

                val endDeffer = lifecycleScope.async {
                    for (i in second() until line.length) {
                        if (regex.containsMatchIn(line[i].toString())) {
                            return@async i
                        }
                    }
                    return@async -1
                }

                lifecycleScope.launch {
                    val start = startDeffer.await()
                    val end = endDeffer.await()
                    if (end != -1 && start != -1) {
                        getCursor().select(first(), start, first(), end)
                    }

                }
            }
        }
    }

    fun load(input: InputStream, path: Path) {
        mEditContent =
            EditContent(LineArray(input), path, this, mVisibleRows)
        mRowsRender.loadContent(mEditContent!!)
        lifecycleScope.launch(Dispatchers.Main) {
            requestLayout()
            invalidate()
        }
    }

    fun getEditContent() = mEditContent

    fun getCursor() = mCursor

    fun interceptParentScroll(absoluteX: Float, absoluteY: Float): Boolean {
        isCursorIntercepted = if (getCursor().isSelecting()) {
            getCursor().isHitSelectingHandleStart(
                absoluteX,
                absoluteY
            ) || getCursor().isHitSelectingHandleEnd(absoluteX, absoluteY)
        } else {
            getCursor().isHitCursorHandle(absoluteX, absoluteY)
        }
        return isCursorIntercepted
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs?.run {
            initialSelStart = mCursor.getCurrentCol()
            initialSelEnd = mCursor.getCurrentCol() + 1
            imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        return mInputConnection
    }

    fun showInput() {
        if (requestFocus()) {
            mInputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun closeInput() {
        mInputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
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

    fun getRowsRender(): RowsRender = this.mRowsRender

    fun getLineHeight(): Float = mRowsRender.getLineHeight()

    fun getSingleCharWidth(): Float = mRowsRender.getSingleCharWidth()

    private fun getParentAsUnrestrainedScroll(): UnrestrainedScrollView? {
        if (parent is UnrestrainedScrollView) {
            return parent as UnrestrainedScrollView
        }
        return null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle = this.mLifecycleRegistry
}
