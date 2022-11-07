package org.codroid.editor

import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import org.codroid.editor.utils.length

class CodroidInputConnection(
    private val mTargetView: CodroidEditor,
    fullEditor: Boolean,
) : BaseInputConnection(mTargetView, fullEditor) {

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        if (getCursor().isSelecting()) {
            mTargetView.getEditContent()?.replace(text ?: "", getCursor())
        } else {
            mTargetView.getEditContent()?.insert(text ?: "", getCursor())
        }
        getCursor().moveCursorBy(text?.length ?: 0)
        invalidate()
        return true
    }


    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        println("requestCursorUpdates: $cursorUpdateMode")
        return true
    }

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        event?.run {
            println(event)
            if (action == KeyEvent.ACTION_DOWN) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_SPACE -> commitText(" ", getCursorInfo().index)
                    KeyEvent.KEYCODE_DEL -> onKeyDelDown()
                    KeyEvent.KEYCODE_ENTER -> onKeyEnterDown()
                    KeyEvent.KEYCODE_DPAD_LEFT -> getCursor().moveLeft()
                    KeyEvent.KEYCODE_DPAD_UP -> getCursor().moveUp()
                    KeyEvent.KEYCODE_DPAD_RIGHT -> getCursor().moveRight()
                    KeyEvent.KEYCODE_DPAD_DOWN -> getCursor().moveDown()
                }
            }
        }
        return true;
    }

    private fun onKeyDelDown() {
        getEditContent()?.run {
            this.delete(getCursor())
            if (getCursor().isSelecting()) {
                getCursor().moveCursorBy(getCursor().getSelectRange().length())
            } else {
                getCursor().moveCursorBy(-1)
            }
            invalidate()
        }
    }

    private fun onKeyEnterDown() {
        commitText("\n", getCursorInfo().index)
    }

    private fun getCursor() = mTargetView.getCursor()

    private fun getCursorInfo() = mTargetView.getCursor().getCurrentInfo()

    private fun getEditContent() = mTargetView.getEditContent()

    private fun invalidate() = mTargetView.invalidate()
}