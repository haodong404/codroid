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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.database.AddonDatabase

class Codroid : Application() {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    lateinit var context: Context;
    lateinit var THEME: Preferences.Key<String>

    companion object {
        lateinit var addonDb: AddonDatabase
        const val SDCARD_ROOT_DIR = "/storage/emulated/0"
    }

    override fun onCreate() {
        super.onCreate()
        context = this;
        addonDb =
            Room.databaseBuilder(this, AddonDatabase::class.java, "addon-database")
                .allowMainThreadQueries()
                .build()
        THEME = stringPreferencesKey("theme")
        AddonManager.get().initialize(getExternalFilesDir(null), addonDb);
        AddonManager.get().loadAddons()
    }
}