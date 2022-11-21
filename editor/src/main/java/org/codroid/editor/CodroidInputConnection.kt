package org.codroid.editor

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import org.codroid.editor.utils.length

class CodroidInputConnection(
    private val mTargetView: CodroidEditor,
    fullEditor: Boolean,
) : BaseInputConnection(mTargetView, fullEditor) {

    companion object {
        const val TAG = "CodroidInputConnection"
    }

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        Log.d(TAG, "commitText: $newCursorPosition")
        if (getCursor().isSelecting()) {
            mTargetView.getEditContent()?.replace(text ?: "")
        } else {
            mTargetView.getEditContent()?.insert(text ?: "")
        }
        return true
    }


    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        Log.d(TAG, "requestCursorUpdates: $cursorUpdateMode")
        return true
    }


    override fun setImeConsumesInput(imeConsumesInput: Boolean): Boolean {
        Log.d(TAG, "imeConsumesInput")
        return super.setImeConsumesInput(imeConsumesInput)
    }

    override fun getSelectedText(flags: Int): CharSequence {
        Log.d(TAG, "getSelectedText: $flags")
        return ""
    }

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        event?.run {
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
        getEditContent()?.delete()
    }

    private fun onKeyEnterDown() {
        commitText("\n", getCursorInfo().index)
    }

    override fun performContextMenuAction(id: Int): Boolean {
        println(id)
        return super.performContextMenuAction(id)
    }

    override fun performEditorAction(actionCode: Int): Boolean {
        return super.performEditorAction(actionCode)
    }


    override fun beginBatchEdit(): Boolean {
        Log.d(TAG, "beginBatchEdit")
        return super.beginBatchEdit()
    }

    override fun endBatchEdit(): Boolean {
        Log.d(TAG, "endBatchEdit")
        return super.endBatchEdit()
    }

    override fun setComposingText(text: CharSequence?, newCursorPosition: Int): Boolean {
        Log.d(TAG, "setComposingText: $text, $newCursorPosition")
        return super.setComposingText(text, newCursorPosition)
    }

    override fun finishComposingText(): Boolean {
        Log.d(TAG, "finishComposingText")
        return super.finishComposingText()
    }

    override fun setSelection(start: Int, end: Int): Boolean {
        Log.d(TAG, "setSelection: $start, $end")
        return super.setSelection(start, end)
    }
    private fun getCursor() = mTargetView.getCursor()

    private fun getCursorInfo() = mTargetView.getCursor().getCurrentInfo()

    private fun getEditContent() = mTargetView.getEditContent()

    private fun invalidate() = mTargetView.invalidate()
}