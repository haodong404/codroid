package org.codroid.editor

import org.junit.Test
import org.junit.Assert.*

class UtilsTest {
    @Test
    fun intPairTest() {
        val pair = makePair(30, 50)
        assertEquals(30, pair.first())
        assertEquals(50, pair.second())

        val pair0 = makePair(0, 0)
        assertEquals(0, pair0.first())
        assertEquals(0, pair0.second())

        val pairMax = makePair(Int.MAX_VALUE, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, pairMax.first())
        assertEquals(Int.MAX_VALUE, pairMax.second())

        val pairMix = makePair(Int.MAX_VALUE, 0)
        assertEquals(Int.MAX_VALUE, pairMix.first())
        assertEquals(0, pairMix.second())

        val pairMix2 = makePair(0, Int.MAX_VALUE)
        assertEquals(0, pairMix2.first())
        assertEquals(Int.MAX_VALUE, pairMix2.second())
    }
}