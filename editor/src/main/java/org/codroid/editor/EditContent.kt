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

import org.codroid.editor.buffer.TextSequence
import org.codroid.editor.decoration.Decorator

class EditContent(
    private val mTextSequence: TextSequence,
    private val visibleLines: Int = 10
) {

    private val mDecorator: Decorator = Decorator()


    fun up(distance: Int) {

    }

    fun down(distance: Int) {

    }

    fun length(): Int {
        return mTextSequence.length()
    }

    inner class RowIterator {

    }

    class Range(visibleLines: Int, bufferSize: Int) {

    }
}