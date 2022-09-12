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

package org.codroid.editor.utils

import org.codroid.editor.decoration.Decorator
import java.util.*

data class Block(val substring: String, val spans: Decorator.Spans? = null) {
    fun isEmpty() = substring.isEmpty()
}

@JvmInline
value class Row(val blocks: LinkedList<Block> = LinkedList()) {

    fun appendBlock(block: Block) {
        if (!block.isEmpty()) {
            blocks.add(block)
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (item in blocks) {
            builder.append(item.substring)
        }
        return builder.toString()
    }

}