package org.codroid.editor.algorithm

import java.lang.IllegalArgumentException

class ScrollableLinkedList<T>() : Iterable<T> {
    private var mHeaderNode: Node<T>? = null
    private var mTailNode: Node<T>? = null

    constructor(capacity: Int, initialize: (index: Int) -> T) : this() {
        repeat(capacity) {
            appendLast(initialize(it))
        }
    }

    private var mCount = 0

    data class Node<T>(
        var previous: Node<T>? = null,
        var next: Node<T>? = null,
        var value: T? = null
    ) {
        override fun toString(): String {
            return "Node(value: ${this.value}, previous: ${this.previous == null}, next: ${this.next == null} )"
        }
    }

    fun empty() = mCount == 0

    fun size() = mCount

    fun appendLast(value: T) {
        if (mTailNode == null) {
            init(value)
        } else {
            val newNode = Node(previous = mTailNode, value = value)
            mTailNode?.next = newNode
            mTailNode = newNode
        }
        mCount++
    }

    fun appendFirst(value: T) {
        if (mHeaderNode == null) {
            init(value)
        } else {
            val newNode = Node(next = mHeaderNode, value = value)
            mHeaderNode?.previous = newNode
            mHeaderNode = newNode
        }
        mCount++
    }

    private fun init(value: T) {
        val newNode = Node(value = value)
        mHeaderNode = newNode
        mTailNode = newNode
    }

    override fun toString(): String {
        val builder = StringBuilder("[")
        var isFirst = true
        for (item in this) {
            if (isFirst) {
                isFirst = false
            } else {
                builder.append(", ")
            }
            builder.append(item.toString())
        }
        builder.append("]")
        return builder.toString()
    }

    override fun iterator() = iterator(Node(next = mHeaderNode))

    fun iterator(header: Node<T>) = Iterator(header)

    inner class Iterator(head: Node<T>) : kotlin.collections.Iterator<T> {

        private var mCurrentNode: Node<T>? = head

        override fun hasNext(): Boolean = mCurrentNode?.next != null

        override fun next(): T {
            mCurrentNode = mCurrentNode?.next
            return getCurrent()
        }

        fun moveForward(distance: Int): Node<T>? {
            if (distance < 0) {
                throw IllegalArgumentException("The moving distance cannot be a negative number $distance.")
            }
            repeat(distance) {
                mCurrentNode?.run {
                    mCurrentNode = next
                }
            }
            return mCurrentNode
        }

        fun moveBackward(distance: Int): Node<T>? {
            if (distance < 0) {
                throw IllegalArgumentException("The moving distance cannot be a negative number $distance.")
            }
            repeat(distance) {
                mCurrentNode?.run {
                    mCurrentNode = previous
                }
            }
            return mCurrentNode
        }

        fun moveBy(distance: Int): Node<T>? {
            return if (distance > 0) {
                moveForward(distance)
            } else {
                moveBackward(-distance)
            }
        }

        fun getCurrent(): T {
            return mCurrentNode?.value
                ?: throw IndexOutOfBoundsException("No next value found in ScrollableLinkedList.")
        }

        fun getCurrentOrNull(): T? {
            return try {
                getCurrent()
            } catch (_: IndexOutOfBoundsException) {
                null
            }
        }
    }
}