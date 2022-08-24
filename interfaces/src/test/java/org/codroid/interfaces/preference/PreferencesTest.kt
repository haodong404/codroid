package org.codroid.interfaces.preference

import cc.ekblad.toml.decode
import cc.ekblad.toml.encodeToString
import cc.ekblad.toml.tomlMapper
import org.junit.Test
import org.junit.Assert.*

class PreferencesTest {

    private val defaultMapper = tomlMapper { }

    @Test
    fun `Can parse input setting with string value`() {
        val preference = Preferences(
            settings = mapOf(
                "input-type" to InputSetting(
                    type = "input",
                    title = "This is an input box.",
                    subtitle = "Subtitle",
                    valueType = "STRING",
                    defaultValue = "Text value",
                    placeholder = "Please enter..."
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preferences>(toml)
        assertEquals(preference, actual)
    }

}