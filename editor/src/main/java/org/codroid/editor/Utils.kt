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

import org.codroid.editor.decoration.Decorator
import org.codroid.editor.decoration.SpanDecoration
import org.codroid.editor.graphics.TextPaint
import java.util.*

typealias IntPair = ULong

fun IntPair.first(): Int = ((this and 0xFFFFFFFF00000000U) shr 32).toInt()

fun IntPair.second(): Int = (this and 0xFFFFFFFFU).toInt()

fun makePair(first: Int, second: Int): IntPair = (first.toULong() shl 32) or second.toULong()

/**
 * This Vector class could be used to represent coordinate of x and y.
 */
data class Vector(var x: Int = 0, var y: Int = 0) {

    /**
     * A.deltaFrom(B),
     * Which means the delta from A to B(B - A).
     */
    fun deltaFrom(x: Int, y: Int): Vector {
        return Vector(
            x - this.x,
            y - this.y
        )
    }

    /**
     * Convenient to compute delta.
     */
    fun deltaFrom(vector: Vector): Vector {
        return deltaFrom(vector.x, vector.y)
    }

    /**
     * Reset the values.
     */
    fun reset(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) {
            return false
        }
        if (other.x == this.x && other.y == this.y) {
            return true
        }
        return false
    }

    operator fun unaryMinus(): Vector {
        reset(-x, -y)
        return this
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}


class Interval(private val pair: IntPair) {

    val start: Int
        get() = pair.first()
    val end: Int
        get() = pair.second()

    fun isOverlapping(other: Interval): Boolean {
        return other.start in start..end || other.end in start..end
    }

    fun length(): Int {
        return end - start
    }
}

data class Block(val substring: String, val spans: Decorator.Spans? = null)

@JvmInline
value class Row(val blocks: LinkedList<Block> = LinkedList()) {

    fun appendBlock(block: Block) {
        blocks.add(block)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (item in blocks) {
            builder.append(item.substring)
        }
        return builder.toString()
    }

}

class LineAnchor(paint: TextPaint) {
    private var mLineHeight = paint.getLineHeight()
    private val mBaselineHeight = paint.getBaselineHeight()
    var top = 0F
    var bottom = mLineHeight
    var baseline = mBaselineHeight
    var lineNumber = 0

    fun height(): Float = bottom - top

    fun increase() {
        this.top = bottom
        this.bottom += mLineHeight
        this.baseline = top + mBaselineHeight
        this.lineNumber++
    }

    fun reset() {
        resetByRow(0)
    }

    fun resetByRow(row: Int) {
        this.lineNumber = row + 1
        this.top = row * mLineHeight
        this.bottom = (row + 1) * mLineHeight
        this.baseline = (row + 1) * mBaselineHeight
    }
}


fun IntRange.isIn(other: IntRange): Boolean = this.first >= other.first && this.last <= other.last