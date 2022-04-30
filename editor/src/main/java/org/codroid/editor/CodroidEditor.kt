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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager

class CodroidEditor : View, View.OnClickListener {

    init {
        setOnClickListener(this)
        isFocusable = true
        isFocusableInTouchMode = true
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }


    private val mInputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private var textToDraw = "HELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLO"
    private var appendPosition = 0F
    private val mTextPaint: Paint by lazy {
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 40F
            strokeWidth = 3F
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawText(textToDraw, appendPosition, 100F, mTextPaint)
        appendPosition += 20F
    }

    override fun onClick(p0: View?) {
        showInput()
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

    inner class InputConnection(targetView: View, fullEditor: Boolean): BaseInputConnection(targetView, fullEditor) {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            Log.i("Zac", text.toString() + ":" + newCursorPosition)
            textToDraw = text.toString()
            this@CodroidEditor.postInvalidate()
            return true
        }
    }
}
