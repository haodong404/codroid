package org.codroid.editor

import org.codroid.editor.utils.Vector
import org.junit.Assert.assertEquals
import org.junit.Test

class VectorTest {

    private val vector = Vector(10, 11)

    @Test
    fun deltaFrom() {
        assertEquals(Vector(10, 11).toString(), vector.toString())
        assertEquals(Vector(-6, -5).toString(), vector.deltaFrom(4, 6).toString())
        assertEquals(Vector(0, 0).toString(), vector.deltaFrom(Vector(10, 11)).toString())
        assertEquals(Vector(10, 15).toString(), vector.deltaFrom(20, 26).toString())
    }

    @Test
    fun reset() {
        assertEquals(Vector(10, 11).toString(), vector.toString())
        vector.reset(1, 2)
        assertEquals(Vector(1, 2).toString(), vector.toString())
        vector.reset(0, 0)
        assertEquals(Vector(0, 0).toString(), vector.toString())
    }

    @Test
    fun plus() {
        val b = Vector(3, -1)
        assertEquals(Vector(13, 10).toString(), (vector + b).toString())
        val c = Vector(-10, -11)
        assertEquals(Vector(0, 0).toString(), (vector + c).toString())
    }
}