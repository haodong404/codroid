package org.codroid.body.ui

import android.graphics.Bitmap

data class FileItem(
    val name: String,
    var icon: Bitmap?,
    val textColor: Int,
    val type: String,
    var isExpanded: Boolean,
    val level: Int
)


data class AddonItem(
    val name: String,
    val version: String,
    val description: String,
    val author: String,
    val link: String
)

data class SymbolItem(val value: String, var label: String? = null) {
    init {
        if (label == null) {
            this.label = value
        }
    }
}