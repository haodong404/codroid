package org.codroid.interfaces.utils

import cc.ekblad.toml.decode
import org.codroid.interfaces.addon.AddonDescription
import org.junit.Assert.*

import org.junit.Test

class TomlKtTest {
    private val mapper = defaultTomlMapper()

    @Test
    fun toObject() {
        val desc = mapper.decode<Description>(
            "name=\"Zac\"\nversion=32\nlist=[10, 86, 32]\n[theme]\nname=\"Hello\"\nversion=3",
        )
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

    @Test
    fun decodeAddonDescription() {
        val result = decode2Map(
            "name = \"AddonSample\"\n" +
                    "package = \"org.example\"\n" +
                    "enterPoint = \".Main\"\n" +
                    "author = \"Haodong\"\n" +
                    "versionCode = 1\n" +
                    "versionDes = \"1\"\n" +
                    "supportVersion = \"1\"\n" +
                    "description = \"First addon for Codroid\"\n" +
                    "link=\"https://github.com/haodong404/codroid\"", mapper
        )
        val desc = toObject(result, AddonDescription.Addon::class.java)
        assertEquals("org.example", desc.`package`)
    }
}

data class Entity(val name: String, val age: Int)