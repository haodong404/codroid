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

package org.codroid.editor.utils

import org.codroid.editor.decoration.*
import org.codroid.editor.graphics.TextPaint
import java.util.*
import kotlin.math.abs

class Block(substring: String = "") {

    private var mSpans: Decorator.Spans? = null
    private var mCharacterSpan: CharacterSpan? = null

    private val mSubstring = StringBuilder(substring)

    fun getSpans() = mSpans

    fun getCharacterSpan() = mCharacterSpan

    fun getAssembledSpans() = overrideSpans(mCharacterSpan)

    private fun overrideSpans(span: SpanDecoration?): Decorator.Spans? {

        if (span == null) return mSpans

        mSpans = Decorator.Spans()

        if (span is RepaintSpan) {
            mSpans!!.repaint = span
        }

        if (span is BackgroundSpan) {
            mSpans!!.background.add(span)
        }

        if (span is ForegroundSpan) {
            mSpans!!.foreground.add(span)
        }

        if (span is ReplacementSpan) {
            mSpans!!.replacement.add(span)
        }
        return mSpans
    }

    fun getSubstring() = mSubstring.toString()

    fun isEmpty() = mSubstring.isEmpty()

    fun appendChar(char: Char) {
        mSubstring.append(char)
    }

    fun setSpans(spans: Decorator.Spans) {
        this.mSpans = spans
    }

    fun setCharacterSpan(span: CharacterSpan) {
        this.mCharacterSpan = span
    }

    override fun toString(): String {
        return "Row($mSubstring, ${mSpans.toString()})"
    }
}

fun disassembleSpan(span: SpanDecoration, out: Decorator.Spans) {
    if (span is RepaintSpan) {
        val temp = out.repaint
        if (temp != null) {
            out.repaint = object : RepaintSpan {
                override fun onRepaint(origin: TextPaint): TextPaint {
                    return temp.onRepaint(origin)
                }
            }
        } else {
            out.repaint = span
        }
    }
    if (span is ForegroundSpan) {
        out.foreground.addLast(span)
    }
    if (span is BackgroundSpan) {
        out.background.addLast(span)
    }
    if (span is ReplacementSpan) {
        out.replacement.addLast(span)
    }
}

data class Row(val blocks: LinkedList<Block> = LinkedList(), var selection: IntPair = 0u) {

    fun appendBlock(block: Block) {
        if (!block.isEmpty()) {
            blocks.add(block)
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (item in blocks) {
            builder.append(item.getSubstring())
        }
        return builder.toString()
    }

}