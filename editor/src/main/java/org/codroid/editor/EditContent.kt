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
import org.codroid.editor.analysis.SyntaxAnalyser
import org.codroid.editor.algorithm.TextSequence
import org.codroid.editor.decoration.*
import org.codroid.editor.graphics.Cursor
import org.codroid.editor.utils.*
import org.codroid.textmate.EncodedTokenAttributes
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalUnsignedTypes::class)
class EditContent(
    private val mTextSequence: TextSequence,
    mPath: Path,
    private val mEditor: CodroidEditor,
    visibleLines: Int = 10,
) : Iterable<Row> {

    private val mDecorator: Decorator = Decorator()
    private val mRange: RowsRange by lazy {
        RowsRange(visibleLines)
    }

    private val mSyntaxAnalyser = SyntaxAnalyser(mEditor.theme!!, mTextSequence, mPath)
    private val mStartRowChannel = Channel<Pair<Int, RowNode?>>(CONFLATED)
    private val isSyntaxAnalyserEnabled = true
    private var mCursorRowNode: RowNode? = null

    init {
        startAnalysing()
        pushAnalyseTask(0)
    }

    fun pushAnalyseTask(startRow: Int = 0, node: RowNode? = mCursorRowNode) {
        mEditor.lifecycleScope.launch {
            mStartRowChannel.send(startRow to node)
        }
    }

    private fun startAnalysing() {
        if (isSyntaxAnalyserEnabled) {
            mEditor.lifecycleScope.launch(Dispatchers.IO) {
                while (isActive) {
                    val current = mStartRowChannel.receive()
                    var currentRowIndex = current.first
                    val currentRowNode = if (current.second != null) {
                        mDecorator.spanDecorations().iterator(current.second!!)
                    } else {
                        null
                    }
                    var lastLineTokens = UIntArray(0)
                    mSyntaxAnalyser.analyze(current.first)
                        .buffer()
                        .collect { pair ->
                            val tokenLength = pair.second.tokens.size / 2
                            val spans = mutableMapOf<IntRange, SpanDecoration>()
                            for (j in 0 until tokenLength) {
                                val startIndex = pair.second.tokens[2 * j]
                                val nextStartIndex = if (j + 1 < tokenLength) {
                                    pair.second.tokens[2 * j + 2].toInt()
                                } else {
                                    pair.first.second()
                                }
                                val metadata = pair.second.tokens[2 * j + 1]
                                makeSyntaxSpan(
                                    startIndex.toInt() until nextStartIndex, metadata, spans
                                )
                            }
                            if (currentRowIndex == getVisibleRowsRange().getEnd()) {
                                mEditor.postInvalidate()
                            }

                            lastLineTokens = pair.second.tokens
                            mDecorator.setSpan(
                                currentRowNode?.getCurrentNodeOrNull(),
                                spans,
                                tokenLength
                            )
                            currentRowIndex++
                            currentRowNode?.moveForward(1)
                        }
                    withContext(Dispatchers.Main) {
                        mEditor.requestLayout()
                        mEditor.invalidate()
                    }
                }
            }
        }
    }

    private fun makeSyntaxSpan(
        range: IntRange,
        metadata: UInt,
        out: MutableMap<IntRange, SpanDecoration>
    ) {
        val span = CharacterSpan().apply {
            val foreground = EncodedTokenAttributes.getForeground(metadata)
            val background = EncodedTokenAttributes.getBackground(metadata)
            val fontStyle = EncodedTokenAttributes.getFontStyle(metadata)
            setFontStyle(fontStyle)
            SyntaxAnalyser.registry?.getColorMap()?.run {
                setTextColor(Color.parseColor(getOrDefault(foreground, "#FF0000")))
//                setBackground(Color.parseColor(getOrDefault(background, "#FF00FF")))
            }
        }
        out[range] = span
    }

    // Start: inclusive; End: exclusive
    fun delete(cursor: Cursor) {
        mDecorator.removeSpan(
            cursor.getCurrentInfo().rowNode,
            cursor.getCurrentInfo().column - 1,
            cursor.getEnd() - cursor.getStart()
        )
        mTextSequence.delete(cursor.getStart(), cursor.getEnd())
        refreshSyntax()
    }

    // Start: inclusive; End: exclusive
    fun replace(content: CharSequence, cursor: Cursor) {
        mTextSequence.getRowAndCol(cursor.getStart()).run {
            mDecorator.removeSpan(
                first(),
                second(),
                cursor.getStart() - cursor.getEnd(),
                CharacterSpan()
            )
            mTextSequence.replace(content, cursor.getStart(), cursor.getEnd())
            refreshSyntax()
        }
    }

    fun insert(content: CharSequence, cursor: Cursor) {
        mTextSequence.insert(content, cursor.getCurrentInfo().index + 1)
        val multiRow = content.lines()
        mDecorator.insertSpan(
            mEditor.getCursor().getCurrentInfo().rowNode,
            cursor.getCurrentInfo().column until cursor.getCurrentInfo().column + (multiRow.getOrNull(
                0
            )?.length ?: 0),
            multiRow.size
        )
//        refreshSyntax()
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
        mDecorator.addSpan(
            cursor.getCurrentInfo().rowNode,
            cursor.getCurrentInfo().column,
            cursor.getEnd() - cursor.getStart(),
            span
        )
    }

    fun removeSpan(range: IntRange, span: SpanDecoration) {
        mTextSequence.getRowAndCol(range.first).run {
            mDecorator.removeSpan(
                mEditor.getCursor().getCurrentInfo().rowNode,
                second(),
                range.length(),
                span
            )
        }
    }

    fun getVisibleRowsRange(): RowsRange {
        return mRange
    }

    fun getTextSequence() = mTextSequence


    fun longestLineLength(): Int {
        return mTextSequence.longestLineLength()
    }

    override fun iterator(): Iterator<Row> = RowIterator()

    inner class RowIterator() : Iterator<Row> {

        private var mCurrentRow = getVisibleRowsRange().getBegin()
        private var mRowNodeIt: RowNodeIterator? = null

        override fun hasNext() =
            mCurrentRow <= getVisibleRowsRange().getEnd()

        override fun next(): Row {
            if (mRowNodeIt == null) {
                mDecorator.spanDecorations().nodeAt(mCurrentRow)?.let {
                    mRowNodeIt = mDecorator.spanDecorations().iterator(it)
                }
            }
            val temp = makeRow()
            mCurrentRow++
            return temp
        }

        private fun makeRow() =
            makeRow(mCurrentRow, mRowNodeIt?.next())

        private fun makeRow(row: Int, spans: ArrayList<Decorator.Spans>?) = Row().apply {
            println("SPANS: ${spans == null}")
            mTextSequence.rowAtOrNull(row)?.let { line ->
                var newBlock = Block()
                for ((index, item) in line.withIndex()) {
                    if (newBlock.getSpans() != spans?.getOrNull(index)) {
                        appendBlock(newBlock)
                        newBlock = Block().apply {
                            spans?.getOrNull(index)?.let { setSpans(it) }
                        }
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
        private val mVisibleRowNodeHeader by lazy {
            mDecorator.spanDecorations().iterator()
        }

        // inclusive
        private var mVisibleEnd = min(mVisibleRows - 1, endEdge())

        fun bindScroll(start: Int, old: Int) {
            mVisibleBegin = max(0, start - 2)
            mVisibleEnd = min(endEdge(), mVisibleRows + start + 1)
            mVisibleRowNodeHeader.moveBy(start - old)
            mEditor.invalidate()
        }

        fun getBegin(): Int {
            return mVisibleBegin
        }

        fun getEnd(): Int {
            return min(mTextSequence.rows(), mVisibleEnd)
        }

        fun getHeadNode() = mVisibleRowNodeHeader.getCurrentNodeOrNull()

        private fun endEdge(): Int {
            return mTextSequence.rows() - 1
        }

        override fun toString(): String =
            "VisibleBegin: $mVisibleBegin, VisibleEnd: $mVisibleEnd"
    }
}