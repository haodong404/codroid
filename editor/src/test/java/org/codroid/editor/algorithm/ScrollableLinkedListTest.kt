package org.codroid.editor.algorithm

import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalArgumentException

class ScrollableLinkedListTest {
    @Test
    fun `Test initialize by appending to first`() {
        val list = ScrollableLinkedList<Int>()
        assertTrue(list.empty())
        list.appendFirst(23)

        assertEquals("[23]", list.toString())
        assertFalse(list.empty())
    }

    @Test
    fun `Test initialize by appending to last`() {
        val list = ScrollableLinkedList<Int>()
        assertTrue(list.empty())
        list.appendLast(24)

        assertEquals("[24]", list.toString())
        assertFalse(list.empty())
    }

    @Test
    fun `Test appendFirst`() {
        val list = ScrollableLinkedList<Int>()
        assertTrue(list.empty())
        list.appendFirst(3)
        list.appendFirst(2)
        list.appendFirst(1)

        assertFalse(list.empty())
        assertEquals("[1, 2, 3]", list.toString())
    }

    @Test
    fun `Test appendLast`() {
        val list = ScrollableLinkedList<Int>()
        assertTrue(list.empty())
        list.appendLast(1)
        list.appendLast(2)
        list.appendLast(3)

        assertFalse(list.empty())
        assertEquals(3, list.size())
        assertEquals("[1, 2, 3]", list.toString())
    }

    @Test
    fun `Can move forward`() {
        val list = ScrollableLinkedList(10) {
            it + 1
        }
        assertEquals(10, list.size())

        val it = list.iterator()
        assertEquals(3, it.moveForward(3)?.value)
        assertEquals(4, it.moveForward(1)?.value)
        assertEquals(4, it.moveForward(0)?.value)
        assertEquals(10, it.moveForward(6)?.value)

        assertThrows(IllegalArgumentException::class.java) {
            it.moveForward(-1)
        }
    }

    @Test
    fun `Can move backward`() {
        val list = ScrollableLinkedList(10) {
            it + 1
        }
        assertEquals(10, list.size())

        val it = list.iterator()
        it.moveForward(10)
        assertEquals(10, it.moveBackward(0)?.value)
        assertEquals(9, it.moveBackward(1)?.value)
        assertEquals(6, it.moveBackward(3)?.value)
        assertEquals(1, it.moveBackward(5)?.value)

        assertThrows(IllegalArgumentException::class.java) {
            it.moveForward(-1)
        }
    }

    @Test
    fun `Test moveBy`() {
        val list = ScrollableLinkedList(10) {
            it + 1
        }
        assertEquals(10, list.size())

        val it = list.iterator()
        assertEquals(3, it.moveBy(3)?.value)
        assertEquals(2, it.moveBy(-1)?.value)
        assertEquals(2, it.moveBy(0)?.value)
        assertEquals(10, it.moveBy(8)?.value)
        assertEquals(5, it.moveBy(-5)?.value)
    }
}