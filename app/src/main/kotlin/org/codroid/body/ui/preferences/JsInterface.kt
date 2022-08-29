package org.codroid.body.ui.preferences

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.preference.CodroidPreferenceGroup

class JsInterface(private val mContext: Context) {

    private var current = 0;

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun increment(): Int {
        return current++
    }

    @JavascriptInterface
    fun json(): String {
        return AddonManager.get().getCodroidPreference(CodroidPreferenceGroup.TEXT_EDITOR).toJson()
    }
}

data class Entity(val name: String)