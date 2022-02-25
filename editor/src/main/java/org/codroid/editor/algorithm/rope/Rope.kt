/*
 *     Copyright (c) 2022 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.editor.algorithm.rope

import org.codroid.editor.algorithm.TextSequence
import java.util.concurrent.LinkedBlockingQueue

class Rope : TextSequence {
    companion object {
        val MIN_LEAF = 16
        val MAX_LEAF = 32
    }

    var root: Internal = Internal(null, null, 0)


    fun fromString(content: String) {
        if (content.length > MAX_LEAF) {
            return
        }

        val nodeQueue = LinkedBlockingQueue<Leaf>()
        while (true) {
            content.subSequence(0, 2)
        }
    }

    override fun insert(position: Int, content: String) {
        TODO("Not yet implemented")
    }

    override fun delete(start: Int, end: Int) {
        TODO("Not yet implemented")
    }

    override fun itemAt(target: String) {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return super.toString()
    }

    interface Node {

    }

    data class Internal(
        val left: Node?,
        val right: Node?,
        var height: Int = 0,
        var weight: Int = 0
    ) : Node

    data class Leaf(val value: String, val length: Int) : Node
}