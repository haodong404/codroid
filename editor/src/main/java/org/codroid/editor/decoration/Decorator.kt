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

import org.codroid.editor.Interval
import org.codroid.editor.Vector
import org.codroid.editor.makePair
import java.util.*

class Decorator {
    private val mSpanDecorationTree: TreeMap<Interval, LinkedList<SpanDecoration>> =
        TreeMap { t1, t2 ->
            if (t1.start > t2.start) {
                return@TreeMap 1
            } else if (t1.start < t2.start) {
                return@TreeMap -1
            }
            return@TreeMap 0
        }

    private val mDynamicDecorationSet: HashSet<DynamicDecoration> = HashSet()

    private val mStaticDecorationSet: HashSet<StaticDecoration> = HashSet()

    fun addSpan(start: Int, end: Int, decoration: SpanDecoration) {
        val interval = Interval(makePair(start, end))
        if (!mSpanDecorationTree.containsKey(interval)) {
            mSpanDecorationTree[interval] = LinkedList<SpanDecoration>().apply { add(decoration) }
        } else {
            mSpanDecorationTree[interval]?.add(decoration)
        }
    }

    fun addSpan(decoration: DynamicDecoration) {
        mDynamicDecorationSet.add(decoration)
    }

    fun addSpan(decoration: StaticDecoration) {
        mStaticDecorationSet.add(decoration)
    }

    fun spanDecorationSequence(): Sequence<Map.Entry<Interval, LinkedList<SpanDecoration>>> {
        return mSpanDecorationTree.asSequence()
    }

    fun dynamicDecorationSequence(): Sequence<DynamicDecoration> {
        return mDynamicDecorationSet.asSequence()
    }

    fun staticDecorationSequence(): Sequence<StaticDecoration> {
        return mStaticDecorationSet.asSequence()
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
}