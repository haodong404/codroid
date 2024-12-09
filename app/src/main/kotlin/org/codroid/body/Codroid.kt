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