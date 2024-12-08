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

import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onCompletion
import org.codroid.editor.analysis.SyntaxAnalyser
import org.codroid.editor.algorithm.TextSequence
import org.codroid.editor.decoration.*
import org.codroid.editor.graphics.Cursor
import org.codroid.editor.utils.*
import org.codroid.editor.utils.Timer
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

@OptIn(ExperimentalUnsignedTypes::class)
class EditContent(
    private val mTextSequence: TextSequence,
    contentDescription: ContentDescription,
    private val mEditor: CodroidEditor,
    visibleLines: Int = 10,
) : Iterable<Row> {

    private val mDecorator: Decorator = Decorator()
    private val mRange: RowsRange by lazy {
        RowsRange(visibleLines)
    }

    private val mSyntaxAnalyser = SyntaxAnalyser(mEditor.theme!!, mTextSequence, contentDescription)
    private val mStartRowChannel = Channel<Pair<Int, RowNode?>>(CONFLATED)
    private val isSyntaxAnalyserEnabled = true
    private var mCursorRowNode: RowNode? = null
    private val mAnalyserTask = Timer.create(Duration.ZERO, {
        startAnalysing()
    }, mEditor.lifecycleScope)

    private val mSelectionColor = Color.LTGRAY

    init {
        pushAnalyseTask()
    }

    private fun pushAnalyseTask(startRow: Int = 0, node: RowNode? = mCursorRowNode) {
        mEditor.lifecycleScope.launch {
            if (mAnalyserTask.isRunning()) {
                mAnalyserTask.cancel()
            }
            mAnalyserTask.start()
            mStartRowChannel.send(startRow to node)
        }
    }

    private suspend fun startAnalysing() {
        if (isSyntaxAnalyserEnabled) {
            val current = mStartRowChannel.receive()
            var currentRowIndex = current.first
            val currentRowNode = if (current.second != null) {
                mDecorator.spanDecorations().iterator(current.second!!)
            } else {
                null
            }
            mSyntaxAnalyser.analyze(currentRowIndex)
                .buffer()
                .onCompletion {
                    withContext(Dispatchers.Main) {
                        mEditor.requestLayout()
                        mEditor.invalidate()
                    }
                }
                .collect { pair ->
                    currentRowNode?.moveForward(1)
                    val tokenLength = pair.second.tokens.size / 2
                    val spans = mutableMapOf<IntRange, UInt>()
                    for (j in 0 until tokenLength) {
                        val startIndex = pair.second.tokens[2 * j]
                        val nextStartIndex = if (j + 1 < tokenLength) {
                            pair.second.tokens[2 * j + 2].toInt()
                        } else {
                            pair.first.second()
                        }
                        val metadata = pair.second.tokens[2 * j + 1]
                        spans[startIndex.toInt() until nextStartIndex] = metadata
                    }
                    if (currentRowIndex == getVisibleRowsRange().getEnd()) {
                        mEditor.postInvalidate()
                    }
                    mDecorator.setSyntaxSpans(
                        currentRowNode?.getCurrentNodeOrNull(),
                        spans,
                        tokenLength
                    )
                    currentRowIndex++
                }
        }
    }

    // Start: inclusive; End: exclusive
    fun delete() {
        mDecorator.removeSpan(
            getCursor().getCurrentInfo().rowNode,
            getCursor().getCurrentInfo().column - 1,
            getCursor().getSelectedRange().length()
        )
        mTextSequence.delete(getCursor().getSelectedRange())
        updateCursor(-1)
        refreshSyntax()
    }

    // Start: inclusive; End: exclusive
    fun replace(content: CharSequence) {
        if (content.isEmpty()) {
            delete()
            return
        }
        mTextSequence.getRowAndCol(getCursor().getSelectedRange().first).run {
            mDecorator.removeSpan(
                first(),
                second(),
                getCursor().getSelectedRange().length()
            )
            mTextSequence.replace(
                content,
                getCursor().getSelectedRange()
            )
        }
        updateCursor(content.length - 1, content == "\n")
        refreshSyntax()
    }

    fun insert(content: CharSequence) {
        if (content.isEmpty()) return
        mTextSequence.insert(content, getCursor().getCurrentInfo().index + 1)
        val multiRow = content.lines()
        mDecorator.insertSpan(
            mEditor.getCursor().getCurrentInfo().rowNode,
            getCursor().getCurrentInfo().column until getCursor().getCurrentInfo().column + (multiRow.getOrNull(
                0
            )?.length ?: 0),
            multiRow.size
        )
        updateCursor(content.length, content == "\n")
        refreshSyntax()
    }

    private fun updateCursor(offset: Int, newLine: Boolean = false) {
        if (getCursor().isSelecting()) {
            getCursor().moveCursor(
                getCursor().getCurrentInfo().selectedStart.first(),
                getCursor().getCurrentInfo().selectedStart.second() + offset,
                getCursor().getCurrentInfo().selectedRange.first + offset
            )
        } else {
            getCursor().moveCursorBy(offset, newLine)
        }
    }

    private fun refreshSyntax() {
        pushAnalyseTask(
            mEditor.getCursor().getCurrentInfo().row,
            mEditor.getCursor().getCurrentInfo().rowNode
        )
    }

    fun length(): Int {
        return mTextSequence.length()
    }

    fun rows(): Int {
        return mTextSequence.rows()
    }

    fun rowNodeAt(index: Int) = mDecorator.spanDecorations().nodeAt(index)

    fun addDecoration(cursor: Cursor, span: SpanDecoration) {
//        mDecorator.addSpan(
//            cursor.getCurrentInfo().rowNode,
//            cursor.getCurrentInfo().column,
//            cursor.getEnd() - cursor.getStart(),
//            span
//        )
    }

    fun removeSpan(range: IntRange, span: SpanDecoration) {
//        mTextSequence.getRowAndCol(range.first).run {
//            mDecorator.removeSpan(
//                mEditor.getCursor().getCurrentInfo().rowNode,
//                second(),
//                range.length(),
//                span
//            )
//        }
    }

    fun getVisibleRowsRange(): RowsRange {
        return mRange
    }

    fun getTextSequence() = mTextSequence


    fun longestLineLength(): Int {
        return mTextSequence.longestLineLength()
    }

    private fun getCursor() = mEditor.getCursor()

    override fun iterator(): Iterator<Row> = RowIterator()

    inner class RowIterator() : Iterator<Row> {

        private var mCurrentRow = getVisibleRowsRange().getBegin()
        private var mRowNodeIt: RowNodeIterator? = null
        private var mLastStartIndex = mTextSequence.charIndex(getVisibleRowsRange().getBegin(), 1)
        private val mSelectedRowsRange =
            getCursor().getCurrentInfo().selectedStart.first()..getCursor().getCurrentInfo().selectedEnd.first()

        override fun hasNext() = mCurrentRow <= getVisibleRowsRange().getEnd()

        override fun next(): Row {
            if (mRowNodeIt == null) {
                mDecorator.spanDecorations().nodeAt(mCurrentRow)?.let {
                    mRowNodeIt = mDecorator.spanDecorations().iterator(it)
                }
            }
            mRowNodeIt?.moveForward(1)
            val temp = makeRow()
            mCurrentRow++
            return temp
        }

        private fun makeRow() = makeRow(mCurrentRow, mRowNodeIt?.getCurrentOrNull())

        private fun makeRow(row: Int, spans: ArrayList<UInt>?) = Row().apply {
            mTextSequence.rowAtOrNull(row)?.let { line ->
                var newBlock = Block()
                val range = mLastStartIndex..(mLastStartIndex + line.length)
                var left = 0
                var right = 0
                if (row in mSelectedRowsRange) {
                    left = if (row == getCursor().getCurrentInfo().selectedStart.first()) {
                        getCursor().getCurrentInfo().selectedStart.second()
                    } else {
                        -1
                    }

                    right = if (row == getCursor().getCurrentInfo().selectedEnd.first()) {
                        getCursor().getCurrentInfo().selectedEnd.second()
                    } else {
                        -1
                    }
                }
                selection = makePair(left, right)
                mLastStartIndex = range.endExclusive()
                for ((index, item) in line.withIndex()) {
                    val temp = mDecorator.findCharacterSpan(spans?.getOrNull(index) ?: 0u)
                    if (newBlock.getCharacterSpan() != temp) {
                        appendBlock(newBlock)
                        newBlock = Block().apply { temp?.let(::setCharacterSpan) }
                    }
                    newBlock.appendChar(item)
                }
                appendBlock(newBlock)
            }
        }
    }


    /**
     * The range of buffer window and visible window.
     *
     * @property bufferSize
     * @constructor
     * TODO
     *
     * @param mVisibleRows
     */
    inner class RowsRange(private val mVisibleRows: Int) {
        // inclusive
        private var mVisibleBegin = 0

        // inclusive
        private var mVisibleEnd = min(mVisibleRows - 1, endEdge())

        fun bindScroll(start: Int, old: Int) {
            mVisibleBegin = max(0, start - 2)
            mVisibleEnd = min(endEdge(), mVisibleRows + start + 1)
            mEditor.invalidate()
        }

        fun getBegin(): Int {
            return mVisibleBegin
        }

        fun getEnd(): Int {
            return min(mTextSequence.rows(), mVisibleEnd)
        }


        private fun endEdge(): Int {
            return mTextSequence.rows() - 1
        }

        override fun toString(): String =
            "VisibleBegin: $mVisibleBegin, VisibleEnd: $mVisibleEnd"
    }
}