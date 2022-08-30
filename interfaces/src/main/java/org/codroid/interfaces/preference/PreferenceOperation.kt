package org.codroid.interfaces.preference

interface PreferenceOperation {
    fun putString(key: String, value: String)
    fun putInt(key: String, value: Int)
    fun putBoolean(key: String, value: Boolean)

    /**
     * Find a String, returns an empty string if not found.
     */
    fun getString(key: String): String

    fun getInt(key: String): Int
    fun getBoolean(key: String): Boolean
}