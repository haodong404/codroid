package org.codroid.interfaces.utils

import android.provider.Settings
import cc.ekblad.toml.configuration.TomlMapperConfigurator
import cc.ekblad.toml.decode
import cc.ekblad.toml.get
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.tomlMapper
import cc.ekblad.toml.transcoding.TomlDecoder
import cc.ekblad.toml.transcoding.decode
import org.codroid.interfaces.preference.Preferences
import org.codroid.interfaces.preference.Setting
import org.codroid.interfaces.preference.preferencesMapper
import org.junit.Assert.*

import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

class TomlKtTest {
    private val mapper = defaultTomlMapper()

    @Test
    fun toObject() {
        val map = decode2Map(
            "name=\"Zac\"\nversion=32\nlist=[10, 86, 32]\n[theme]\nname=\"Hello\"\nversion=3",
            mapper
        )
        val desc = toObject(map, Description::class.java)
        assertEquals("Zac", desc.name)
        assertEquals(32, desc.version)
        assertEquals(86, desc.list[1])
        assertEquals("Hello", desc.theme.name)
        assertEquals(3, desc.theme.version)
    }

    @Test
    fun defaultValue() {
        val map = decode2Map("", mapper)
        val desc = toObject(map, Description::class.java)
        assertEquals(null, desc.name)
        assertEquals(0, desc.version)
        assertEquals(null, desc.list)
    }
}

data class Entity(val name: String, val age: Int)