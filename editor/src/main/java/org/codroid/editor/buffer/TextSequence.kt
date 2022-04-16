package org.codroid.editor.buffer

import java.io.InputStream

abstract class TextSequence {

    constructor(inputStream: InputStream)

    constructor(charSequence: String)

    abstract fun rowAt(index: Int): String

    abstract fun insert(content: String, position: Int)

    abstract fun insert(content: String, row: Int, col: Int)

    abstract fun delete(start: Int, end: Int)

    abstract fun replace(content: String, start: Int, end: Int)

    abstract fun length(): Int

    abstract fun rows(): Int

    abstract override fun toString(): String
}