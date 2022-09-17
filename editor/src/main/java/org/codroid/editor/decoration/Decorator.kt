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

    fun addSpan(range: IntRange, decoration: SpanDecoration) {
        val disassembledSpans = Spans()
        disassembleSpan(decoration, disassembledSpans)
        for (i in range) {
            if (mSpanDecorations.containsKey(i)) {
                mSpanDecorations[i]?.overrideSpans(disassembledSpans)
            } else {
                mSpanDecorations[i] = disassembledSpans
            }
        }
    }

    fun addSpans(span: Map<IntRange, SpanDecoration>) {
        span.forEach {
            addSpan(it.key, it.value)
        }
    }

    private fun Spans.overrideSpans(spans: Decorator.Spans) {
        if (spans.repaint != null) {
            this.repaint = spans.repaint
        }
        if (spans.background != null) {
            this.background = spans.background
        }
        if (spans.foreground != null) {
            this.foreground = spans.foreground
        }
        if (spans.replacement != null) {
            this.replacement = spans.replacement
        }
    }

    fun addSpan(decoration: DynamicDecoration) {
        mDynamicDecorationSet.add(decoration)
    }

    fun addSpan(decoration: StaticDecoration) {
        mStaticDecorationSet.add(decoration)
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
        var background: BackgroundSpan? = null,
        var foreground: ForegroundSpan? = null,
        var replacement: ReplacementSpan? = null
    ) {

        override fun equals(other: Any?): Boolean {
            if (other is Spans) {
                return this.repaint == other.repaint && this.background == other.background
                        && this.foreground == other.foreground && this.replacement == other.replacement
            }
            return false
        }

        override fun hashCode(): Int {
            var result = repaint?.hashCode() ?: 0
            result = 31 * result + (background?.hashCode() ?: 0)
            result = 31 * result + (foreground?.hashCode() ?: 0)
            result = 31 * result + (replacement?.hashCode() ?: 0)
            return result
        }
    }
}