package org.codroid.editor

import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection

class CodroidInputConnection(
    private val mTargetView: CodroidEditor,
    fullEditor: Boolean,
) : BaseInputConnection(mTargetView, fullEditor) {

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {

        if (getCursor().isSelecting()) {
            mTargetView.getEditContent()
                ?.replace(text ?: "", getCursor())
        } else {
            mTargetView.getEditContent()
                ?.insert(text ?: "", getCursor())
        }
        getCursor().moveCursorBy(text?.length ?: 0)
        invalidate()
        return true
    }

    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        return true
    }

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        event?.let { ev ->
            if (ev.keyCode == KeyEvent.KEYCODE_DEL) {
                if (ev.action == KeyEvent.ACTION_UP) {
                    mTargetView.getEditContent()
                        ?.delete(getCursor())
                    getCursor().moveCursorBy(getCursor().getStart() - getCursor().getEnd())
                }
            }
        }
        return true
    }

    override fun getTextBeforeCursor(length: Int, flags: Int): CharSequence? {
//        mTargetView.getEditContent()?.run {
//            getTextSequence().rowAtOrNull(mTargetView.getCursor().getCurrentRow())?.let {
//                return it.substring(
//                    max(
//                        0,
//                        mTargetView.getCursor().getCurrentCol() - length
//                    ) until min(it.length, mTargetView.getCursor().getCurrentCol())
//                )
//            }
//        }
        return null
    }

    override fun getTextAfterCursor(length: Int, flags: Int): CharSequence? {
//        mTargetView.getEditContent()?.run {
//            getTextSequence().rowAtOrNull(mTargetView.getCursor().getCurrentRow())?.let {
//                return it.substring(
//                    mTargetView.getCursor().getCurrentCol() until
//                            min(
//                                it.length,
//                                mTargetView.getCursor().getCurrentCol() + length
//                            )
//                )
//            }
//        }
        return null
    }

    private fun getCursor() = mTargetView.getCursor()

    private fun invalidate() = mTargetView.invalidate()
}