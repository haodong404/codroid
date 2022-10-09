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
import org.codroid.editor.algorithm.TextSequence
import org.codroid.editor.utils.*
import java.util.*
import kotlin.collections.ArrayList

typealias RowNode = ScrollableLinkedList.Node<ArrayList<Decorator.Spans>>

class Decorator {

    private val mSpanDecorations = ScrollableLinkedList<ArrayList<Spans>>()

    private val mDynamicDecorationSet: HashSet<DynamicDecoration> = HashSet()

    private val mStaticDecorationSet: HashSet<StaticDecoration> = HashSet()

    fun setSpan(row: Int, col: Int, length: Int, decoration: SpanDecoration? = null) {
        val disassembledSpans = Spans()
        if (decoration != null) {
            disassembleSpan(decoration, disassembledSpans)
        }
        var count = 0
        mSpanDecorations.nodeAt(row)?.let { start ->
            mSpanDecorations.iterator(start).run {
                var current = getCurrentOrNull()
                var newSpan: Spans? = null
                var isFirst = 0
                while (hasNext() && count < length) {
                    if (current != null) {
                        for ((index, item) in current.withIndex()) {
                            count++
                            if (isFirst != -1) {
                                isFirst++
                                if (isFirst == col) {
                                    isFirst = -1
                                }
                                continue
                            }
                            if (newSpan == null) {
                                newSpan = item.overrideSpans(disassembledSpans);
                            }
                            current[index] = newSpan
                        }
                    }
                    current = next();
                }
            }
        }
    }

    fun insertSpan(
        rowNode: RowNode?,
        range: IntRange,
        rowsCount: Int,
        decoration: SpanDecoration? = null
    ) {
        val disassembledSpans = Spans()
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
                for (i in range) {
                    add(i, disassembledSpans);
                }
            }
        }
    }


    fun addSpan(row: Int, spans: Map<IntRange, SpanDecoration>, sizeOfLine: Int) {
        mSpanDecorations.nodeAt(row)?.let {
            addSpan(it, spans, sizeOfLine)
        }
    }

    fun addSpan(rowNode: RowNode?, spans: Map<IntRange, SpanDecoration>, sizeOfLine: Int = 0) {
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

    fun setSpan(decoration: DynamicDecoration) {
        mDynamicDecorationSet.add(decoration)
    }

    fun setSpan(decoration: StaticDecoration) {
        mStaticDecorationSet.add(decoration)
    }

    fun removeSpan(rowNode: RowNode?, col: Int, length: Int, spans: SpanDecoration? = null) {
        if (rowNode == null) return
        var count = 0
        mSpanDecorations.iterator(rowNode).run {
            var current = getCurrentOrNull()
            var isFirst = 0
            while (hasNext() && count < length) {
                if (current != null) {
                    for ((index, item) in current.withIndex()) {
                        count++
                        if (isFirst != -1) {
                            isFirst++
                            if (isFirst == col) {
                                isFirst = -1
                            }
                            continue
                        }
                        if (spans == null) {
                            current.removeAt(index)
                        }
                        if (item.repaint == spans) {
                            item.repaint = null
                        }
                        if (item.background.contains(spans)) {
                            item.background.remove(spans)
                        }
                        if (item.foreground.contains(spans)) {
                            item.foreground.remove(spans)
                        }
                        if (item.replacement.contains(spans)) {
                            item.replacement.remove(spans)
                        }
                    }
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