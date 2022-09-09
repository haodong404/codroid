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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.codroid.editor.analysis.SyntaxAnalyser
import org.codroid.editor.buffer.TextSequence
import org.codroid.editor.decoration.CharacterSpan
import org.codroid.editor.decoration.Decorator
import org.codroid.editor.decoration.SpanDecoration
import org.codroid.textmate.EncodedTokenAttributes
import java.nio.file.Path
import java.util.*
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalUnsignedTypes::class)
class EditContent(
    private val mTextSequence: TextSequence,
    private val mPath: Path,
    private val editor: CodroidEditor,
    visibleLines: Int = 10,
) : Iterable<Row> {

    private val mDecorator: Decorator = Decorator()
    private val mRange = RowsRange(visibleLines)

    private val mSyntaxAnalyser = SyntaxAnalyser(editor.theme!!, mTextSequence, mPath)
    private var mNewLineStartPos = 0
    private val mStartRowChannel = Channel<Int>(CONFLATED)

    init {
        startAnalysing()
        pushAnalyseTask(0)
    }

    fun pushAnalyseTask(startRow: Int = 0) {
        editor.lifecycleScope.launch {
            mStartRowChannel.send(startRow)
        }
    }

    private fun startAnalysing() {
        editor.lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                var current = mStartRowChannel.receive()
                mSyntaxAnalyser.analyze(current)
                    .buffer()
                    .onCompletion {
                        withContext(Dispatchers.Main) {
                            editor.requestLayout()
                            editor.invalidate()
                        }
                    }
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
                                IntRange(startIndex.toInt(), nextStartIndex - 1),
                                metadata, spans
                            )
                        }
                        mDecorator.appendSpan(current, spans)
                        if (current == getVisibleRowsRange().getEnd()) {
                            editor.postInvalidate()
                        }
                        current++
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

    fun length(): Int {
        return mTextSequence.length()
    }

    fun rows(): Int {
        return mTextSequence.rows()
    }

    fun getVisibleRowsRange(): RowsRange {
        return mRange
    }

    fun getTextSequence() = mTextSequence

    private fun makeRow(row: Int): Row =
        Row().apply {
            mTextSequence.rowAtOrNull(row)?.let { line ->
                val syntaxSpans = mDecorator.syntaxSpans()[row]
                if (syntaxSpans != null) {
                    syntaxSpans.forEach {
                        appendBlock(
                            Block(
                                line.substring(
                                    it.key.first,
                                    min(line.length, it.key.last + 1)
                                ), it.value
                            )
                        )
                    }
                    return@apply
                }
                appendBlock(Block(line))
                mNewLineStartPos += line.length
            }
        }


    fun longestLineLength(): Int {
        return mTextSequence.longestLineLength()
    }

    override fun iterator(): Iterator<Row> = RowIterator()

    inner class RowIterator() : Iterator<Row> {

        private var mCurrentRow = getVisibleRowsRange().getBegin()

        override fun hasNext() = mCurrentRow <= getVisibleRowsRange().getEnd()

        override fun next(): Row {
            val temp = makeRow(mCurrentRow)
            mCurrentRow++
            return temp
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
            editor.invalidate()
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