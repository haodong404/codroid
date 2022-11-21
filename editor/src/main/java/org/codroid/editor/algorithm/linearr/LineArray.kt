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
import org.codroid.editor.utils.*
import java.io.InputStream
import java.util.TreeMap
import kotlin.math.max

/**
 * Represents a [TextSequence] with line array.
 * Lines are stored in an [ArrayList] of [String], from 0 to lines.size - 1, and each are not contain the new line character(LF or CR).
 */
class LineArray : TextSequence {

    private val mBuffer: ArrayList<String> = ArrayList(20)
    private val mRow2Index: TreeMap<Int, Int> = TreeMap()
    private var length = 0
    private var longest = 0

    constructor(inputStream: InputStream) : super(inputStream) {
        val bytes = String(inputStream.readBytes(), TextBufferConfig.charset())
        init(bytes)
    }

    constructor(str: String) : super(str) {
        init(str)
    }

    private fun init(str: String) {
        var isFirstLine = true
        for ((index, line) in str.lineSequence().withIndex()) {
            if (isFirstLine) {
                isFirstLine = false
            } else {
                length++
            }
            mBuffer.add(line)
            // Each line has a line breaker(except the last line).
            length += line.length
            mRow2Index[index] = length
            if (line.length > longest) {
                this.longest = line.length
            }
        }
    }

    override fun rowAt(index: Int): String {
        return mBuffer[index]
    }

    override fun rowAtOrNull(index: Int): String? {
        if (mBuffer.size <= index) {
            return null
        }
        return rowAt(index)
    }

    override fun insert(content: CharSequence, index: Int) {
        replace(content, index, index)
    }

    override fun insert(content: CharSequence, row: Int, col: Int) {
        if (content.isEmpty()) {
            return
        }
        val index = charIndex(row, col) + 1
        replace(content, index, index)
    }

    override fun delete(range: IntRange) {
        if (range.isEmpty() || range.first == -1) {
            return
        }
        replace("", range)
    }

    private fun replace(content: CharSequence, start: Int, end: Int) {
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
        updateRow2Index(from)
    }

    override fun replace(content: CharSequence, range: IntRange) {
        replace(content, range.first, range.endExclusive())
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
        // The last Row
        if (from == rows() - 1) {
            return from
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

    private fun updateRow2Index(start: Int) {
        for (i in start until length) {
            if (i >= rows()) {
                mRow2Index.remove(i)
            } else {
                var result = rowAt(i).length + mRow2Index.getOrDefault(i - 1, 0)
                if (i != 0) {
                    result++
                }
                mRow2Index[i] = result
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
        if (row < 0 || col < 0) return 0
        val actualRow = row - 1
        return (mRow2Index[actualRow] ?: -1) + col
    }

    override fun getRowAndCol(position: Int): IntPair {
        if (position <= -1) {
            return makePair(0, 0)
        } else if (position >= length) {
            return makePair(rows() - 1, rowAt(rows() - 1).length - 1)
        }

        var row = (position / length) * rows()
        var flag = -1
        while (row <= rows()) {
            val temp = mRow2Index.getOrDefault(row, 0)
            flag = if (position >= temp) {
                if (flag == 0) {
                    break
                }
                row++
                1
            } else {
                row--
                if (flag == 1) {
                    break
                }
                0
            }
        }
        return makePair(row + 1, position - mRow2Index.getOrDefault(row, -1))
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