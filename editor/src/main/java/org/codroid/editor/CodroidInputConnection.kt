package org.codroid.editor

import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import kotlin.math.max
import kotlin.math.min

class CodroidInputConnection(
    private val mTargetView: CodroidEditor,
    fullEditor: Boolean,
) : BaseInputConnection(mTargetView, fullEditor) {

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        mTargetView.getEditContent()?.getTextSequence()
            ?.insert(text ?: "", getCursor().getCurrentRow(), getCursor().getCurrentCol())
        getCursor().move(text?.length ?: 0)
        mTargetView.getEditContent()?.pushAnalyseTask(getCursor().getCurrentRow())
        invalidate()
        return true
    }

    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        return true
    }

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        println(event)
        event?.let { ev ->
            if (ev.keyCode == KeyEvent.KEYCODE_DEL) {
                if (ev.action == KeyEvent.ACTION_UP) {
                    val pos = mTargetView.getEditContent()?.getTextSequence()
                        ?.charIndex(getCursor().getCurrentRow(), getCursor().getCurrentCol())
                    if (pos != null) {
                        mTargetView.getEditContent()?.getTextSequence()?.delete(pos - 1, pos)
                        getCursor().move(-1)
                        mTargetView.getEditContent()?.pushAnalyseTask(getCursor().getCurrentRow())
                    }
                }
            }
        }
        return true
    }

    override fun getTextBeforeCursor(length: Int, flags: Int): CharSequence? {
        mTargetView.getEditContent()?.run {
            getTextSequence().rowAtOrNull(mTargetView.getCursor().getCurrentRow())?.let {
                return it.substring(
                    max(
                        0,
                        mTargetView.getCursor().getCurrentCol() - length
                    ) until min(it.length, mTargetView.getCursor().getCurrentCol())
                )
            }
        }
        return null
    }

    override fun getTextAfterCursor(length: Int, flags: Int): CharSequence? {
        mTargetView.getEditContent()?.run {
            getTextSequence().rowAtOrNull(mTargetView.getCursor().getCurrentRow())?.let {
                return it.substring(
                    mTargetView.getCursor().getCurrentCol() until
                            min(
                                it.length,
                                mTargetView.getCursor().getCurrentCol() + length
                            )
                )
            }
        }
        return null
    }

    private fun getCursor() = mTargetView.getCursor()

    private fun invalidate() = mTargetView.invalidate()
}