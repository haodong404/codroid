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
import org.codroid.editor.utils.Block
import org.codroid.editor.utils.IntPair
import org.codroid.editor.utils.disassembleSpan
import org.codroid.editor.utils.hasIntersection
import java.util.*
import kotlin.collections.ArrayList

class Decorator {

    private val mSpanDecorations = TreeMap<Int, Spans>()

    private val mDynamicDecorationSet: HashSet<DynamicDecoration> = HashSet()

    private val mStaticDecorationSet: HashSet<StaticDecoration> = HashSet()

    fun addSpan(range: IntRange, decoration: SpanDecoration? = null) {
        val disassembledSpans = Spans()
        if (decoration != null) {
            disassembleSpan(decoration, disassembledSpans)
        }
        for (i in range) {
            if (mSpanDecorations.containsKey(i)) {
                mSpanDecorations[i]?.overrideSpans(disassembledSpans)?.let {
                    mSpanDecorations[i] = it
                }
            } else {
                mSpanDecorations[i] = disassembledSpans
            }
        }
    }

    fun insertSpan(range: IntRange, decoration: SpanDecoration? = null) {

    }

    fun addSpans(span: Map<IntRange, SpanDecoration>) {
        span.forEach {
            addSpan(it.key, it.value)
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

    fun addSpan(decoration: DynamicDecoration) {
        mDynamicDecorationSet.add(decoration)
    }

    fun addSpan(decoration: StaticDecoration) {
        mStaticDecorationSet.add(decoration)
    }

    fun removeSpan(range: IntRange, span: SpanDecoration? = null) {
        for (i in range) {
            mSpanDecorations[i]?.run {
                if (repaint == span || span == null) {
                    repaint = null
                }
                if (background.contains(span) || span == null) {
                    background.remove(span)
                }
                if (foreground.contains(span) || span == null) {
                    foreground.remove(span)
                }
                if (replacement.contains(span) || span == null) {
                    replacement.remove(span)
                }
            }
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
        return mSpanDecorations.size
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
        var replacement: LinkedList<ReplacementSpan> = LinkedList()
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

        public override fun clone(): Spans {
            return Spans(
                repaint,
                LinkedList(background),
                LinkedList(foreground),
                LinkedList(replacement)
            )
        }
    }
}