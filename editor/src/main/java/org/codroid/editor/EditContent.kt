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
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onCompletion
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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalUnsignedTypes::class)
class EditContent(
    private val mTextSequence: TextSequence,
    private val mPath: Path,
    private val editor: CodroidEditor,
    visibleLines: Int = 10,
    bufferSize: Int = 10
) : Iterable<Row> {

    private val mDecorator: Decorator = Decorator()
    private val mRange = Range(visibleLines, bufferSize)

    private val mSyntaxAnalyser = SyntaxAnalyser(editor.theme!!)

    init {
        editor.lifecycleScope.launch(Dispatchers.IO) {
            mSyntaxAnalyser.analyze(mTextSequence, mPath)
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
                    mDecorator.appendSpan(spans)
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
                setBackground(Color.parseColor(getOrDefault(background, "#FF00FF")))
            }
        }
        out[range] = span
    }

    fun up() {
        mRange.moveUp(1)
    }

    fun down() {
        mRange.moveDown(1)
    }

    fun length(): Int {
        return mTextSequence.length()
    }

    fun rows(): Int {
        return mTextSequence.rows()
    }

    fun getRange(): Range {
        return mRange
    }

    fun longestLineSize(): Int {
        return mTextSequence.longestLineSize()
    }

    override fun iterator(): Iterator<Row> = RowIterator(mTextSequence.iterator())

    inner class RowIterator(private val iterator: Iterator<String>) : Iterator<Row> {

        private var mNewLineStartPos = 0
        private val mSyntaxSpansIt = mDecorator.syntaxSpans().iterator()

        override fun hasNext() = iterator.hasNext()

        override fun next(): Row {
            return Row().apply {
                val textContent = iterator.next()
                if (mSyntaxSpansIt.hasNext()) {
                    mSyntaxSpansIt.next().forEach {
                        appendBlock(Block(textContent.substring(it.key), it.value))
                    }
                    return@apply
                }
                appendBlock(Block(textContent))
                mNewLineStartPos += textContent.length
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