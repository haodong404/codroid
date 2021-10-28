/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
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

package org.codroid.editor.ui

class Response<T> (val state: Int) {

    constructor(state: Int, result: T) : this(state) {
        this.result = result
    }

    constructor(state: Int, message: String) : this(state) {
        this.errorMessage = message
    }

    var stateCode: Int = state
    var result: T? = null
    var errorMessage: String? = null

    companion object {
        val SUCCEED = 1;
        val FAILED = 2;
    }

    fun isSucceed(): Boolean {
        return stateCode == SUCCEED
    }
}