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
        val preference = Preference(
            settings = mapOf(
                "input-type" to InputSetting(
                    category = "input",
                    title = "This is an input box.",
                    summary = "summary",
                    valueType = "STRING",
                    defaultValue = "Text value",
                    placeholder = "Please enter..."
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preference>(toml)
        assertEquals(preference, actual)
    }

    @Test
    fun `Can parse input setting with number value`() {
        val preference = Preference(
            settings = mapOf(
                "input-type" to InputSetting(
                    category = "input",
                    title = "This is an input box.",
                    summary = "summary",
                    valueType = "NUMBER",
                    defaultValue = "100",
                    placeholder = "Please enter..."
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preference>(toml)
        assertEquals(preference, actual)
    }


    @Test
    fun `Can parse switch setting with string value`() {
        val preference = Preference(
            settings = mapOf(
                "switch-type" to SwitchSetting(
                    category = "switch",
                    title = "This is a switch button.",
                    summary = "summary",
                    defaultValue = false
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preference>(toml)
        assertEquals(preference, actual)
    }

    @Test
    fun `Can parse textarea setting with string value`() {
        val preference = Preference(
            settings = mapOf(
                "switch-type" to TextareaSetting(
                    category = "textarea",
                    title = "This is a switch button.",
                    summary = "summary",
                    placeholder = "placeholder",
                    defaultValue = "Content"
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preference>(toml)
        assertEquals(preference, actual)
    }

    @Test
    fun `Can parse select setting with string value`() {
        val preference = Preference(
            settings = mapOf(
                "switch-type" to SelectSetting(
                    category = "select",
                    title = "This is a switch button.",
                    summary = "summary",
                    options = listOf("Aa", "Bb", "Cc"),
                    defaultValue = 2
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preference>(toml)
        assertEquals(preference, actual)
    }

    @Test
    fun `Can parse nullable attribute`() {
        val preference = Preference(
            settings = mapOf(
                "switch-type" to SelectSetting(
                    category = "select",
                    title = "This is a switch button.",
                    summary = null,
                    options = listOf("Aa", "Bb", "Cc"),
                    defaultValue = 2
                )
            )
        )
        val toml = defaultMapper.encodeToString(preference)
        val actual = preferencesMapper.decode<Preference>(toml)
        assertEquals(preference, actual)
    }

    @Test
    fun `Throw an exception when missing settings`() {
        val toml = """
        """.trimIndent()
        assertThrows(IllegalArgumentException::class.java) {
            preferencesMapper.decode<Preference>(toml)
        }
    }

    @Test
    fun `Throw an exception when missing type`() {
        val toml = """
            [settings."missing-type"]
            title = "This is an input"
            summary = ""
            valueType = "STRING"  # STRING, NUMBER
            defaultValue = "1234"
            placeholder = "Please enter..."
        """.trimIndent()
        assertThrows(IllegalArgumentException::class.java) {
            preferencesMapper.decode<Preference>(toml)
        }
    }

    @Test
    fun `Throw an exception when unknown type`() {
        val toml = """
            [settings."missing-type"]
            type = "unknown"
            title = "This is an input"
            summary = ""
            valueType = "STRING"  # STRING, NUMBER
            defaultValue = "1234"
            placeholder = "Please enter..."
        """.trimIndent()
        assertThrows(IllegalArgumentException::class.java) {
            preferencesMapper.decode<Preference>(toml)
        }
    }

    @Test
    fun `Throw an exception when missing a required attribute`() {
        val toml = """
            [settings."missing-attribute"]
            type = "input"
            valueType = "STRING"  # STRING, NUMBER
            defaultValue = "1234"
            placeholder = "Please enter..."
        """.trimIndent()
        assertThrows(IllegalArgumentException::class.java) {
            preferencesMapper.decode<Preference>(toml)
        }
    }
}