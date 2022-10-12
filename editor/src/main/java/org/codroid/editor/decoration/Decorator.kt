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

import org.codroid.editor.algorithm.ScrollableLinkedList
import org.codroid.editor.utils.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

typealias RowNode = ScrollableLinkedList.Node<ArrayList<Decorator.Spans>>
typealias RowNodeIterator = ScrollableLinkedList<ArrayList<Decorator.Spans>>.Iterator

class Decorator {

    private val mSpanDecorations = ScrollableLinkedList<ArrayList<Spans>>()

    private val mDynamicDecorationSet: HashSet<DynamicDecoration> = HashSet()

    private val mStaticDecorationSet: HashSet<StaticDecoration> = HashSet()

    fun addSpan(
        rowNode: RowNode?,
        col: Int,
        length: Int,
        decoration: SpanDecoration? = null
    ) {
        val disassembledSpans = Spans()
        if (decoration != null) {
            disassembleSpan(decoration, disassembledSpans)
        }
        var count = 0
        rowNode?.let {
            mSpanDecorations.iterator(rowNode).run {
                var newSpan: Spans? = null
                var startIndex = col
                while (hasNext() && count < length) {
                    val current = next();
                    for (i in startIndex until current.size) {
                        if (count >= length) {
                            break
                        }
                        if (newSpan == null) {
                            newSpan = current[i].overrideSpans(disassembledSpans);
                        }
                        current[i] = newSpan
                        count++
                    }
                    startIndex = 0
                }
            }
        }
    }

    fun addSpan(row: Int, col: Int, length: Int, decoration: SpanDecoration? = null) {
        mSpanDecorations.nodeAt(row)?.let { start ->
            addSpan(start, col, length, decoration)
        }
    }

    fun insertSpan(
        rowNode: RowNode?,
        range: IntRange,
        rowsCount: Int,
        decoration: SpanDecoration? = null
    ) {
        var disassembledSpans = Spans()
        if (decoration != null) {
            disassembleSpan(decoration, disassembledSpans)
        }
        if (rowsCount > 1) {
            // Clear spans that after insertion position.
            rowNode?.value?.run {
                for (i in range.first until size) {
                    get(i).clearAll()
                }
            }

            // Insert empty spans.
            repeat(rowsCount) {
                mSpanDecorations.insert(rowNode, arrayListOf())
            }
        } else {
            rowNode?.value?.run {
                disassembledSpans = get(max(0, range.first - 1))
                for (i in range) {
                    add(i, disassembledSpans);
                }
            }
        }
    }


    fun setSpan(row: Int, spans: Map<IntRange, SpanDecoration>, sizeOfLine: Int = 0) {
        setSpan(mSpanDecorations.nodeAt(row), spans, sizeOfLine)
    }

    fun setSpan(
        rowNode: RowNode?,
        spans: Map<IntRange, SpanDecoration>,
        sizeOfLine: Int = 0
    ) {
        val result = spans.flatMap {
            val result = ArrayList<Spans>(sizeOfLine)
            val temp = Spans()
            disassembleSpan(it.value, temp)
            for (i in it.key) {
                result.add(temp)
            }
            result
        } as ArrayList
        if (rowNode == null) {
            mSpanDecorations.appendLast(result)
        } else {
            rowNode.value = result
        }
    }

    private fun Spans.overrideSpans(spans: Spans): Spans {
        val newSpans = this.clone()
        if (spans.repaint != null) {
            newSpans.repaint = spans.repaint
        }
        newSpans.background.addAll(spans.background)
        newSpans.foreground.addAll(spans.foreground)
        newSpans.replacement.addAll(spans.replacement)
        return newSpans
    }

    fun removeSpan(rowNode: RowNode?, col: Int, length: Int, spans: SpanDecoration? = null) {
        if (rowNode == null) return
        var count = 0
        mSpanDecorations.iterator(rowNode).run {
            var current = getCurrentOrNull()
            while (hasNext() && count < length) {
                while (count < length) {
                    if (spans == null) {
                        current?.removeAt(col)
                    } else {
                        current?.getOrNull(col)?.let {
//                            if (it.repaint == spans) {
//                                it.repaint = null
//                            }
//                            it.background.remove(spans)
//                            it.foreground.remove(spans)
//                            it.replacement.remove(spans)
                        }
                    }
                    count++
                }
                current = next();
            }
        }
    }

    fun removeSpan(row: Int, col: Int, length: Int, spans: SpanDecoration? = null) {
        mSpanDecorations.nodeAt(row)?.let { start ->
            removeSpan(start, col, length, spans)
        }
    }

    fun spanDecorations() = mSpanDecorations

    fun dynamicDecorations(): Set<DynamicDecoration> {
        return mDynamicDecorationSet
    }

    fun staticDecorations(): Set<StaticDecoration> {
        return mStaticDecorationSet
    }

    fun spanSize(): Int {
        return 0
    }

    fun dynamicSize(): Int {
        return mDynamicDecorationSet.size
    }

    fun staticSize(): Int {
        return mStaticDecorationSet.size
    }

    data class Spans(
        var repaint: RepaintSpan? = null,
        var background: LinkedList<BackgroundSpan> = LinkedList(),
        var foreground: LinkedList<ForegroundSpan> = LinkedList(),
        var replacement: LinkedList<ReplacementSpan> = LinkedList(),
    ) : Cloneable {

        override fun equals(other: Any?): Boolean {
            if (other is Spans) {
                return this.repaint == other.repaint && this.background == other.background
                        && this.foreground == other.foreground && this.replacement == other.replacement
            }
            return false
        }

        override fun hashCode(): Int {
            var result = repaint?.hashCode() ?: 0
            result = 31 * result + background.hashCode()
            result = 31 * result + foreground.hashCode()
            result = 31 * result + replacement.hashCode()
            return result
        }

        fun clearAll() {
            this.repaint = null
            this.background = LinkedList()
            this.foreground = LinkedList()
            this.replacement = LinkedList()
        }

        public override fun clone(): Spans {
            return Spans(
                repaint,
                LinkedList(background),
                LinkedList(foreground),
                LinkedList(replacement),
            )
        }
    }
}