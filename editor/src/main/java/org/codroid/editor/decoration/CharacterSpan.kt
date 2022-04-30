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

import android.graphics.Paint
import android.graphics.Typeface

class CharacterSpan() : ReplacementSpan {

    private val mPaint: Paint = Paint()

    override fun onPainting(paint: Paint, content: String): Pair<Paint, String> {
        return Pair(mPaint, content)
    }

    fun setTypeface(typeface: Typeface): CharacterSpan {
        mPaint.typeface = typeface
        return this
    }

    fun setTextColor(color: Int): CharacterSpan {
        mPaint.color = color
        return this
    }
}