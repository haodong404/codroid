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

import android.graphics.Color
import android.graphics.Paint
import org.codroid.editor.Interval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DecoratorTest {
    private val decorator = Decorator()

    @Test
    fun addSpanDecoration() {
        val charSpan = CharacterSpan()
            .setTextColor(Color.RED)
        decorator.addSpan(IntRange(0, 9), charSpan)
        decorator.addSpan(IntRange(20, 22), charSpan)
        assertEquals(2, decorator.spanSize())
        decorator.spanDecorationSequence()
            .forEach {
                if (it.key == IntRange(0, 9)) {
                    assertTrue(it.value.first is CharacterSpan)
                }
            }
        assertEquals(0, decorator.staticSize())
        assertEquals(0, decorator.dynamicSize())
    }
}