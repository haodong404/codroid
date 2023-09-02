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

package org.codroid.body

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.color.DynamicColors
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.database.AddonDatabase

class Codroid : Application() {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    lateinit var THEME: Preferences.Key<String>
    external fun stringFromJNI(): Int

    companion object {
        lateinit var addonDb: AddonDatabase
        const val SDCARD_ROOT_DIR = "/storage/emulated/0"
    }

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("codroid")
        DynamicColors.applyToActivitiesIfAvailable(this)
        THEME = stringPreferencesKey("theme")
        AddonManager.get().initialize(this);
        AddonManager.get().loadAddons()
        fork()
    }

    fun fork() {

        Log.i("fork", "before fork >> ")
        val forkRet = stringFromJNI()
        Log.i("fork", "after fork >> forkRet: $forkRet pid: ${android.os.Process.myPid()}")
    }
}