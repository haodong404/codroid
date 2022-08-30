package org.codroid.interfaces.preference

import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.tomlMapper
import cc.ekblad.toml.transcoding.TomlDecoder
import cc.ekblad.toml.transcoding.decode
import kotlinx.serialization.Serializable
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

@Serializable
data class Preference(val title: String = "TITLE", val settings: Map<String, Setting>)

object SettingCategory {
    const val Input = "input"
    const val Textarea = "textarea"
    const val Switch = "switch"
    const val Select = "select"
}

@Serializable
sealed class Setting {
    abstract val category: String
    abstract val title: String
    abstract val summary: String?
}

@Serializable
data class InputSetting(
    override val category: String,
    override val title: String,
    override val summary: String?,
    val placeholder: String,
    val valueType: String = "STRING",
    var defaultValue: String = "",
) : Setting()

@Serializable
data class SwitchSetting(
    override val category: String,
    override val title: String,
    override val summary: String?,
    var defaultValue: Boolean = false
) : Setting()

@Serializable
data class TextareaSetting(
    override val category: String,
    override val title: String,
    override val summary: String?,
    val placeholder: String,
    var defaultValue: String = "",
) : Setting()

@Serializable
data class SelectSetting(
    override val category: String,
    override val title: String,
    override val summary: String?,
    val options: List<String>,
    var defaultValue: Int = 0,
) : Setting()

val preferencesMapper = tomlMapper {
    decoder { root: TomlValue.Map ->
        val params = Preference::class.primaryConstructor?.parameters
        params?.associate { param ->
            val value = root.properties[param.name]
            if (value == null) {
                if (!param.type.isMarkedNullable && !param.isOptional) {
                    throw IllegalArgumentException("Attribute ${param.name} is required but missing!")
                }
            }
            value?.let {
                if (param.name == "settings") {
                    return@associate decodeSetting(param, value, this@decoder)
                } else {
                    return@associate param to decode<Any>(it, param.type)
                }
            }
            param to value
        }?.filterNot {
            it.key.isOptional && it.value == null
        }?.let { Preference::class.primaryConstructor?.callBy(it) }
    }
}

private fun decodeSetting(
    param: KParameter,
    value: TomlValue,
    decoder: TomlDecoder
): Pair<KParameter, Any?> {
    return param to (value as TomlValue.Map).properties.map { settingsMap ->
        val settingTomlMap = settingsMap.value as TomlValue.Map
        if (!settingTomlMap.properties.containsKey("category")) {
            throw IllegalArgumentException("Attribute type is required but missing.")
        }
        return@map when ((settingTomlMap.properties["category"] as TomlValue.String).value) {
            SettingCategory.Input -> settingsMap.key to convertSetting<InputSetting>(
                settingTomlMap,
                decoder
            )
            SettingCategory.Textarea -> settingsMap.key to convertSetting<TextareaSetting>(
                settingTomlMap,
                decoder
            )
            SettingCategory.Switch -> settingsMap.key to convertSetting<SwitchSetting>(
                settingTomlMap,
                decoder
            )
            SettingCategory.Select -> settingsMap.key to convertSetting<SelectSetting>(
                settingTomlMap,
                decoder
            )
            else -> throw IllegalArgumentException("Unknown setting type: ${(settingTomlMap.properties["type"] as TomlValue.String).value}")
        }
    }.toMap()
}

private inline fun <reified T : Setting> convertSetting(
    map: TomlValue.Map,
    decoder: TomlDecoder
): Any? {
    return convertSetting(map, decoder, T::class)
}

private fun <T : Any> convertSetting(
    map: TomlValue.Map,
    decoder: TomlDecoder,
    clazz: KClass<T>
): Any? {
    clazz.primaryConstructor?.parameters?.associate { settingParam ->
        val settingValue = map.properties[settingParam.name]
        if (settingValue == null) {
            if (!settingParam.type.isMarkedNullable && !settingParam.isOptional) {
                throw IllegalArgumentException("Attribute ${settingParam.name} is required but missing!")
            }
        }
        settingValue?.let { value ->
            return@associate settingParam to decoder.decode<Any>(
                value,
                settingParam.type
            )
        }
        settingParam to settingValue
    }?.filterNot {
        it.key.isOptional && it.value == null
    }?.let { result ->
        return clazz.primaryConstructor?.callBy(result)
    }
    return null
}