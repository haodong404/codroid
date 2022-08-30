package org.codroid.body.ui.preferences

import android.util.Log
import android.webkit.JavascriptInterface
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.preference.CodroidPreferenceGroup
import org.codroid.interfaces.preference.PreferenceOperation
import org.codroid.interfaces.preference.PreferenceProperty
import kotlin.text.StringBuilder

class PreferencesInjection : PreferenceOperation {

    private var currentPreference: PreferenceProperty? = null

    /**
     * The json example:
     * {
     *     "id": {
     *         "title": "TITLE",
     *         "settings": {},
     *         ...
     *     },
     *     "id2": {},
     *     ...
     * }
     */
    @JavascriptInterface
    fun allPreferences(): String {
        val builder = StringBuilder("{")
        AddonManager.get().codroidPreferences.forEach {
            builder.append("\"${it.key}\":")
            builder.append(it.value.toJson())
            builder.append(",")
        }

        // Insert a divider to distinguish between Codroid Preferences and Custom Preferences.
        builder.append("\"DIVIDER\": {}")

        AddonManager.get().customPreferences.forEach {
            builder.append("\"${it.key}\":")
            builder.append(it.value.toJson())
            builder.append(",")
        }
        builder.deleteCharAt(builder.length - 1)
        builder.append("}")
        Log.i("Zac", builder.toString())
        return builder.toString()
    }

    @JavascriptInterface
    fun selectPreference(id: String, fromCodroid: Boolean) {
        if (fromCodroid) {
            this.currentPreference = AddonManager.get()
                .getCodroidPreference(CodroidPreferenceGroup.valueOf(id))
        } else {
            this.currentPreference = AddonManager.get()
                .customPreferences[id]
        }
    }

    @JavascriptInterface
    override fun putString(key: String, value: String) {
        this.currentPreference?.run {
            this.putString(key, value)
        }
    }

    @JavascriptInterface
    override fun putInt(key: String, value: Int) {
        this.currentPreference?.run {
            this.putInt(key, value)
        }
    }

    @JavascriptInterface
    override fun putBoolean(key: String, value: Boolean) {
        this.currentPreference?.run {
            this.putBoolean(key, value)
        }
    }

    @JavascriptInterface
    override fun getString(key: String): String {
        this.currentPreference?.run {
            return this.getString(key)
        }
        return ""
    }

    @JavascriptInterface
    override fun getInt(key: String): Int {
        this.currentPreference?.run {
            return this.getInt(key)
        }
        return 0
    }

    @JavascriptInterface
    override fun getBoolean(key: String): Boolean {
        this.currentPreference?.run {
            return this.getBoolean(key)
        }
        return false
    }
}