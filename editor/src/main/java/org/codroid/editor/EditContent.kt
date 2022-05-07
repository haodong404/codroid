/*
 *      Copyright (c) 2022 Zachary. All rights reserved.
 *
 *      This file is part of Codroid.
 *
 *      Codroid is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      Codroid is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.codroid.editor

import org.codroid.editor.buffer.TextSequence
import org.codroid.editor.decoration.Decorator
import kotlin.math.abs
import kotlin.math.min

class EditContent(
    private val mTextSequence: TextSequence,
    visibleLines: Int = 10,
    bufferSize: Int = 10
) {

    private val mDecorator: Decorator = Decorator()
    private val mRange = Range(visibleLines, bufferSize)


    fun up() {
        mRange.moveUp(1)
    }

    fun down() {
        mRange.moveDown(1)
    }

    fun length(): Int {
        return mTextSequence.length()
    }

    fun getRange(): Range {
        return mRange
    }

    inner class RowIterator : Iterator<Row> {
        override fun hasNext(): Boolean {
            TODO("Not yet implemented")
        }

        override fun next(): Row {
            TODO("Not yet implemented")
        }

    }

    /**
     * The range of buffer window and visible window.
     *
     * @property bufferSize
     * @constructor
     * TODO
     *
     * @param visibleLines
     */
    inner class Range(visibleLines: Int, val bufferSize: Int) {
        // inclusive
        private var mBufferBegin = 0

        // inclusive
        private var mBufferEnd = min(visibleLines + bufferSize * 2 - 1, endEdge())

        // inclusive
        private var mVisibleBegin = 0

        // inclusive
        private var mVisibleEnd = min(visibleLines - 1, endEdge())

        fun moveUp(distance: Int) {
            slide(-distance)
        }

        fun moveDown(distance: Int) {
            slide(distance)
        }

        private fun slide(distance: Int) {
            val visibleOffset = computeOffset(mVisibleBegin, mVisibleEnd, distance)
            slideVisibleWindow(visibleOffset)
            val topGap = mVisibleBegin - mBufferBegin
            val bottomGap = mBufferEnd - mVisibleEnd
            (topGap - bottomGap).let {
                val offset = computeOffset(mBufferBegin, mBufferEnd, it)
                if (abs(offset) < abs(distance)) {
                    slideBufferWindow(offset)
                } else {
                    slideBufferWindow(distance)
                }
            }
        }

        private fun slideVisibleWindow(distance: Int) {
            mVisibleBegin += distance
            mVisibleEnd += distance
        }

        private fun slideBufferWindow(distance: Int) {
            mBufferBegin += distance
            mBufferEnd += distance
        }

        private fun computeOffset(top: Int, bottom: Int, distance: Int): Int {
            var offset = distance
            if (top + distance < 0) {
                offset = -top
            } else if (bottom + distance > endEdge()) {
                offset = endEdge() - bottom
            }
            return offset
        }

        fun getBufferBegin(): Int {
            return mBufferBegin
        }

        fun getBufferEnd(): Int {
            return mBufferEnd
        }

        fun getBegin(): Int {
            return mVisibleBegin
        }

        fun getEnd(): Int {
            return mVisibleEnd
        }

        private fun endEdge(): Int {
            return mTextSequence.rows() - 1
        }
    }
}