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

package org.codroid.editor

import android.app.Application
import android.content.Context
import android.util.Log
import org.codroid.editor.addon.AddonManager
import org.codroid.editor.log.LogStream
import org.codroid.editor.log.Logger
import java.lang.Exception

class Codroid : Application() {

    lateinit var context: Context;

    override fun onCreate() {
        super.onCreate()
        AddonManager.get().initialize(this);
        val result = AddonManager.get().loadAddons()
        if (!result.isSucceed) {
            Log.i("Zac", result.message ?: "Failed")
        } else {
            Log.i("Zac", "Succeed")
        }
    }
}