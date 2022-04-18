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

