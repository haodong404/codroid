package org.codroid.interfaces.utils

import org.codroid.interfaces.addon.OptionalField

data class Description(
    val name: String,
    val `package`: String,
    val enterPoint: String,
    val author: String,
    val versionCode: Long,
    val versionDes: String,
    val supportVersion: String,
    val description: String,
    val link: String,
    @OptionalField val events: List<String>? = null,
    @OptionalField val theme: String? = null
)