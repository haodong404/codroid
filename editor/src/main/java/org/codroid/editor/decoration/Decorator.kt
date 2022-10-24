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

import android.graphics.Color
import org.codroid.editor.algorithm.ScrollableLinkedList
import org.codroid.editor.analysis.SyntaxAnalyser
import org.codroid.textmate.EncodedTokenAttributes
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

typealias RowNode = ScrollableLinkedList.Node<ArrayList<UInt>>
typealias RowNodeIterator = ScrollableLinkedList<ArrayList<UInt>>.Iterator

typealias SpanId = UShort

class Decorator {

    private val mSpanDecorations = ScrollableLinkedList<ArrayList<UInt>>()

    private val mDynamicDecorationSet: HashSet<DynamicDecoration> = HashSet()

    private val mStaticDecorationSet: HashSet<StaticDecoration> = HashSet()

    private val mCharacterSpanCache = mutableMapOf<UInt, CharacterSpan>()

    fun insertSpan(
        rowNode: RowNode?,
        range: IntRange,
        rowsCount: Int,
        metadata: UInt = 0u,
    ) {
        if (!mCharacterSpanCache.containsKey(metadata) && metadata != 0u) {
            mCharacterSpanCache[metadata] = makeCharacterSpans(metadata)
        }
        if (rowsCount > 1) {
            // Clear spans that after insertion position.
            rowNode?.value?.run {
                clear()
            }

        } else {
            rowNode?.value?.run {
                val temp = if (metadata == 0u) {
                    get(max(0, range.first - 1))
                } else {
                    metadata
                }
                for (i in range) {
                    add(i, temp);
                }
            }
        }
    }


    fun setSyntaxSpans(row: Int, spans: Map<IntRange, UInt>, sizeOfLine: Int = 0) {
        setSyntaxSpans(mSpanDecorations.nodeAt(row), spans, sizeOfLine)
    }

    fun setSyntaxSpans(
        rowNode: RowNode?,
        spans: Map<IntRange, UInt>,
        sizeOfLine: Int = 0
    ) {
        val result = spans.flatMap {
            val result = ArrayList<UInt>(sizeOfLine)
            if (!mCharacterSpanCache.containsKey(it.value) && it.value != 0u) {
                mCharacterSpanCache[it.value] = makeCharacterSpans(it.value)
            }
            for (i in it.key) {
                result.add(it.value)
            }
            result
        } as ArrayList
        if (rowNode == null) {
            mSpanDecorations.appendLast(result)
        } else {
            rowNode.value = result
        }
    }

    private fun makeCharacterSpans(metadata: UInt): CharacterSpan = CharacterSpan().apply {
        val foreground = EncodedTokenAttributes.getForeground(metadata)
        val background = EncodedTokenAttributes.getBackground(metadata)
        val fontStyle = EncodedTokenAttributes.getFontStyle(metadata)
        setFontStyle(fontStyle)
        SyntaxAnalyser.registry?.getColorMap()?.run {
            setTextColor(Color.parseColor(getOrDefault(foreground, "#FF0000")))
//                setBackground(Color.parseColor(getOrDefault(background, "#FF00FF")))
        }
    }

    fun removeSpan(rowNode: RowNode?, col: Int, length: Int) {
        if (rowNode == null) return
        var count = 0
        mSpanDecorations.iterator(rowNode).run {
            while (hasNext() && count < length) {
                moveForward(1)
                while (count < length) {
                    getCurrentNodeOrNull()?.value?.removeAt(col)
                    count++
                }
            }
        }
    }

    fun removeSpan(row: Int, col: Int, length: Int) {
        mSpanDecorations.nodeAt(row)?.let { start ->
            removeSpan(start, col, length)
        }
    }

    fun spanDecorations() = mSpanDecorations

    fun findCharacterSpan(metadata: UInt): CharacterSpan? = mCharacterSpanCache[metadata]

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

        override fun toString(): String {
            return "Repaint: ${repaint == null}; Background: ${background.size}; Foreground: ${background.size}; Replacement: ${replacement.size}"
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