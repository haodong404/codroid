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

package org.codroid.editor.decoration

interface Decoration {

}

/**
 * If there are decorations inside the characters, implement this interface.
 * It positioned by the interval in [org.codroid.editor.buffer.TextSequence].
 *
 */
interface SpanDecoration : Decoration {

}

/**
 * If there are decorations that span multiple lines, implement this interface.
 * It positioned by row and column.
 *
 */
interface DynamicDecoration : Decoration {

}

/**
 * If there are decorations fixed to the window, implement this interface.
 * It's provided a fixed position.
 *
 */
interface StaticDecoration : Decoration {

}