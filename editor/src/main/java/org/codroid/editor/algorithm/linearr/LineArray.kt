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


package org.codroid.editor.algorithm.linearr

import org.codroid.editor.algorithm.TextSequence
import org.codroid.editor.config.TextBufferConfig
import org.codroid.editor.utils.IntPair
import org.codroid.editor.utils.hasIntersection
import org.codroid.editor.utils.makePair
import java.io.InputStream
import kotlin.math.max

/**
 * Represents a [TextSequence] with line array.
 * Lines are stored in an [ArrayList] of [String], from 0 to lines.size - 1, and each are not contain the new line character(LF or CR).
 */
class LineArray : TextSequence {

    private val mBuffer: ArrayList<String> = ArrayList(20)
    private var length = 0
    private var longest = 0

    constructor(inputStream: InputStream) : super(
        inputStream
    ) {

        val bytes = String(inputStream.readBytes(), TextBufferConfig.charset())
        for (i in bytes.lineSequence()) {
            mBuffer.add(i)
            // Each line has a line breaker(except the last line).
            length += i.length + 1
            if (i.length > longest) {
                this.longest = i.length
            }
        }
        // There isn't a line breaker in the last line, so I subtracted 1.
        length--
    }

    constructor(str: String) : super(str) {
        for (i in str.lineSequence()) {
            mBuffer.add(i)
            length += i.length + 1
            if (i.length > longest) {
                this.longest = i.length
            }
        }
        length--
    }

    override fun rowAt(index: Int): String {
        return mBuffer[index]
    }

    override fun rowAtOrNull(index: Int): String? {
        if (mBuffer.size < index) {
            return null
        }
        return rowAt(index)
    }

    override fun insert(content: String, position: Int) {
        var ptr = 0
        var row = 0
        var col = 0
        for ((idx, now) in mBuffer.withIndex()) {
            ptr += now.length
            if (ptr >= position) {
                row = idx
                col = position - ptr + now.length
                break
            }
            ptr++
        }
        insert(content, row, col)
    }

    override fun insert(content: CharSequence, row: Int, col: Int) {
        if (content.isEmpty()) {
            return
        }
        val old = mBuffer[row]
        var offset = row
        for ((index, now) in content.lineSequence().withIndex()) {
            if (index == 0) {
                mBuffer[row] = old.substring(0, col) + now
                length += now.length
            } else {
                mBuffer.add(row + index, now)
                length += now.length + 1
                offset++
            }
        }
        mBuffer[offset] =
            StringBuilder(mBuffer[offset])
                .append(old.substring(col, max(0, old.length)))
                .toString()
    }

    override fun delete(start: Int, end: Int) {
        if (start >= end) {
            return
        }
        replace("", start, end)
    }

    override fun replace(content: String, start: Int, end: Int) {
        var rightEdge: Int
        var leftEdge = 0
        var from = -1
        var to = -1
        var offset = 0
        for ((idx, now) in mBuffer.withIndex()) {
            rightEdge = leftEdge + now.length
            if ((leftEdge..rightEdge).hasIntersection(start..end)) {
                if (from == -1) {
                    from = idx
                    offset = leftEdge
                } else if (idx > to) {
                    to = idx
                }
            } else if (from != -1) {
                if (to == -1) {
                    to = from + 1
                }
                break
            }
            leftEdge = rightEdge + 1
        }
        val pos = concatRows(from, to)
        mBuffer[pos] = StringBuilder(mBuffer[pos])
            .replaceRange(start - offset, end - offset, content)
            .toString()
        length = length - (end - start) + content.length
        expandRow(pos)

    }

    /**
     * Concatenate rows into a single row.(which contains line breaker.)
     *
     * @param from beginning point.
     * @param to ending point.
     * @return the row's index.
     */
    private fun concatRows(from: Int, to: Int): Int {
        if (from == to) {
            return to
        }
        var isStart = true
        val builder = StringBuilder()
        var next = 0
        for (i in from..to) {
            if (isStart) {
                builder.append(rowAt(i))
                next = i + 1
                isStart = false
            } else {
                builder.append(TextBufferConfig.lineSeparator())
                builder.append(rowAt(next))
                mBuffer.removeAt(next)
            }
        }
        mBuffer[next - 1] = builder.toString()
        return max(0, next - 1)
    }


    /**
     * Expand a row to multiple rows in mBuffer,
     * if the row contains line breakers.
     *
     * @param index which row to expand
     */
    private fun expandRow(index: Int) {

        if (!mBuffer[index].contains(TextBufferConfig.lineSeparator())) {
            return
        }

        for ((idx, now) in mBuffer[index].lineSequence().withIndex()) {
            if (idx == 0) {
                mBuffer[index] = now
            } else {
                mBuffer.add(index + idx, now)
            }
        }
    }

    override fun length(): Int {
        return this.length
    }

    override fun rows(): Int {
        return mBuffer.size
    }

    override fun longestLineLength(): Int = this.longest

    override fun charIndex(row: Int, col: Int): Int {
        var acc = -1
        var isFirstLine = true
        repeat(row + 1) {
            if (isFirstLine) {
                isFirstLine = false
            } else {
                acc++ // new line
            }
            if (it != row) {
                acc += rowAt(it).length
            }
        }
        return acc + col
    }

    override fun getRowAndCol(position: Int): IntPair {
        var total = position + 1
        var row = 0
        var col = 0
        var isFirstLine = true
        for (line in this) {
            if (isFirstLine) {
                isFirstLine = false
            } else {
                total -= 1 // consume a new-line character
            }
            val temp = total - line.length
            if (temp > 0) {
                row++
                total = temp
            } else {
                col = total
                break
            }
        }
        return makePair(row, col)
    }

    override fun toString(): String {
        val result = StringBuffer()
        for ((idx, i) in mBuffer.withIndex()) {
            result.append(i)
            if (idx != mBuffer.size - 1) {
                result.append(TextBufferConfig.lineSeparator())
            }
        }
        return result.toString()
    }

    override fun iterator(): Iterator<String> = LineIterator(this)

    inner class LineIterator(private val lineArray: LineArray) : Iterator<String> {
        private var current = 0

        override fun hasNext(): Boolean {
            return (lineArray.rows() - 1 >= current)
        }

        override fun next(): String {
            return lineArray.rowAt(current).also {
                current++
            }
        }
    }

}