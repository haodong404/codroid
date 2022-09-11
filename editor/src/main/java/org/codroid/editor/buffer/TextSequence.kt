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

import org.codroid.editor.IntPair
import java.io.InputStream

/**
 * The implemented classes from this abstract class are used to
 * store the text content of the [org.codroid.editor.CodroidEditor].
 *
 * That means you could implement any data structures to represent the text sequence.
 */
abstract class TextSequence : Iterable<String> {

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
     * The index should starting from 0.
     *
     * @param index An integer starting from 0.
     * @return The string of a row.
     */
    abstract fun rowAt(index: Int): String

    abstract fun rowAtOrNull(index: Int): String?

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
     * @param row which row, starting from 0.
     * @param col which column, starting from 0.
     */
    abstract fun insert(content: CharSequence, row: Int, col: Int)

    /**
     * Removes the characters in a substring of this sequence.
     * The substring begins at the specified start and extends to the character at index end - 1.
     *
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     */
    abstract fun delete(start: Int, end: Int)

    /**
     * Replaces the characters in a substring of this sequence with characters in the specified.
     * The substring begins at the specified start and extends to the character at index end - 1
     *
     * @param content String that will replace previous contents.
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     */
    abstract fun replace(content: String, start: Int, end: Int)

    /**
     * Returns an index of a char using the position of row and column.
     * In each row, col '0' represents a line separator if this row is a new line.
     * e.g.
     * pos(0, 0) is the line separator of first line, but no lines above the first line. so pos(0, 0) is an invalid position.
     * pos(1, 1) is the first character in the second line.
     *
     * @param row a [Int] starting from 0.
     * @param col a [Int] starting from 0.
     *
     * @return an index for a a given row and column position
     */
    abstract fun charIndex(row: Int, col: Int): Int

    /**
     * Returns an [IntPair], the first value represents a row, and the second represents a column.
     * The principle of representing a character using row and column can be found in [charIndex].
     *
     * @param position the index of a character, starting from 0.
     *
     * @return an [IntPair], the first is a row, and the second is a column.
     */
    abstract fun getRowAndCol(position: Int): IntPair

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

    abstract fun longestLineLength(): Int

    /**
     * Returns the string represented by this sequence.
     * The converted string should contains line separators.
     * And the line separator defined in [org.codroid.editor.config.TextBufferConfig].
     *
     * @return a string representation of this sequence.
     */
    abstract override fun toString(): String
}