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

package org.codroid.editor.buffer

import java.io.InputStream

/**
 * The implemented classes from this abstract class are used to
 * store the text content of the [org.codroid.editor.CodroidEditor].
 *
 * That means you could implement any data structures to represent the text sequence.
 */
abstract class TextSequence {

    /**
     * Constructs the TextSequence from an input stream.
     *
     * @param inputStream Input stream.
     */
    constructor(inputStream: InputStream)

    /**
     * Constructs the TextSequence from a String.
     *
     * @param str Input string.
     */
    constructor(str: String)

    /**
     * Gets the string of rows by its index.
     * The index should start at 0.
     *
     * @param index A integer start at 0.
     * @return The string of a row.
     */
    abstract fun rowAt(index: Int): String

    /**
     * Inserts a string to this sequence at the specific position.
     *
     * @param content Content to insert.
     * @param position Where to insert.
     */
    abstract fun insert(content: String, position: Int)

    /**
     * Inserts a string to this sequence at the specific position,
     * which is positioned by the row and column.
     *
     * @param content Content to insert.
     * @param row which row, starting at 0.
     * @param col which column, starting at 0.
     */
    abstract fun insert(content: String, row: Int, col: Int)

    /**
     * Removes the characters in a substring of this sequence.
     * The substring begins at the specified start and extends to the character at index end - 1.
     *
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     */
    abstract fun delete(start: Int, end: Int)

    /**
     * Replaces the characters in a substring of this sequence with characters in the specified
     * The substring begins at the specified start and extends to the character at index end - 1
     *
     * @param content String that will replace previous contents.
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     */
    abstract fun replace(content: String, start: Int, end: Int)

    /**
     * Returns the length of this sequence.
     * The length is equal to the number of Unicode code units in the string.
     *
     * @return The length of the sequence of characters.
     */
    abstract fun length(): Int

    /**
     * Returns the number of rows in this sequence.
     *
     * @return the number of rows in this sequence.
     */
    abstract fun rows(): Int

    /**
     * Returns the string represented by this sequence.
     * The converted string should contains line separators.
     * And the line separator defined in [org.codroid.editor.config.TextBufferConfig].
     *
     * @return a string representation of this sequence.
     */
    abstract override fun toString(): String
}