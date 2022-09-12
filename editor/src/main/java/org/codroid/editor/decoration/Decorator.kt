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

package org.codroid.editor.decoration

import org.codroid.editor.graphics.TextPaint
import org.codroid.editor.utils.isIn
import java.util.*

class Decorator {
    private val mSpanDecorationTree: TreeMap<IntRange, Spans> =
        TreeMap { t1, t2 ->
            if (t1.first > t2.first) {
                return@TreeMap 1
            } else if (t1.first < t2.first) {
                return@TreeMap -1
            }
            return@TreeMap 0
        }

    private val mSyntaxSpans = TreeMap<Int, Map<IntRange, Spans>>()

    private val mDynamicDecorationSet: HashSet<DynamicDecoration> = HashSet()

    private val mStaticDecorationSet: HashSet<StaticDecoration> = HashSet()

    fun addSpan(range: IntRange, decoration: SpanDecoration) {
        if (!mSpanDecorationTree.containsKey(range)) {
            mSpanDecorationTree[range] = Spans()
        }
        disassembleSpan(decoration, mSpanDecorationTree[range]!!)
    }

    fun addSpan(decoration: DynamicDecoration) {
        mDynamicDecorationSet.add(decoration)
    }

    fun addSpan(decoration: StaticDecoration) {
        mStaticDecorationSet.add(decoration)
    }

    fun appendSpan(rowIndex: Int, map: Map<IntRange, SpanDecoration>) {
        val temp = mutableMapOf<IntRange, Spans>()
        map.forEach {
            val spans = Spans()
            disassembleSpan(it.value, spans)
            temp[it.key] = spans
        }
        mSyntaxSpans[rowIndex] = temp
    }

    private fun disassembleSpan(span: SpanDecoration, out: Spans) {
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
            out.foreground = span
        }
        if (span is BackgroundSpan) {
            out.background = span
        }
        if (span is ReplacementSpan) {
            out.replacement = span
        }
    }

    fun spanDecorations(): TreeMap<IntRange, Spans> {
        return mSpanDecorationTree
    }

    fun dynamicDecorationSequence(): Sequence<DynamicDecoration> {
        return mDynamicDecorationSet.asSequence()
    }

    fun staticDecorationSequence(): Sequence<StaticDecoration> {
        return mStaticDecorationSet.asSequence()
    }

    fun syntaxSpans(): TreeMap<Int, Map<IntRange, Spans>> {
        return mSyntaxSpans
    }

    fun searchSpan(range: IntRange): Map<IntRange, Spans> {
        val result = mutableMapOf<IntRange, Spans>()
        mSpanDecorationTree.forEach {
            if (it.key.isIn(range)) {
                result[it.key] = it.value
            } else if (it.key.first > range.last) {
                return result
            }
        }
        return result
    }

    fun spanSize(): Int {
        return mSpanDecorationTree.size
    }

    fun dynamicSize(): Int {
        return mDynamicDecorationSet.size
    }

    fun staticSize(): Int {
        return mStaticDecorationSet.size
    }

    data class Spans(
        var repaint: RepaintSpan? = null,
        var background: BackgroundSpan? = null,
        var foreground: ForegroundSpan? = null,
        var replacement: ReplacementSpan? = null
    )
}