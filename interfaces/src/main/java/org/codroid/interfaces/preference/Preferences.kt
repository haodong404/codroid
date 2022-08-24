package org.codroid.interfaces.preference

import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.tomlMapper
import cc.ekblad.toml.transcoding.TomlDecoder
import cc.ekblad.toml.transcoding.decode
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

data class Preferences(val title: String = "TITLE", val settings: Map<String, Setting>)

object SettingTypes {
    const val Input = "input"
    const val Textarea = "textarea"
    const val Switch = "switch"
    const val Select = "select"
}

interface Setting {
    val type: String
    val title: String
    val subtitle: String
}

data class InputSetting(
    override val type: String,
    override val title: String,
    override val subtitle: String,
    val placeholder: String,
    val valueType: String = "STRING",
    val defaultValue: Any,
) : Setting


data class SwitchSetting(
    override val type: String,
    override val title: String,
    override val subtitle: String,
    val defaultValue: Boolean
) : Setting

data class TextareaSetting(
    override val type: String,
    override val title: String,
    override val subtitle: String,
    val placeholder: String,
    val defaultValue: String,
) : Setting

data class SelectSetting(
    override val type: String,
    override val title: String,
    override val subtitle: String,
    val options: List<String>,
    val defaultValue: Int,
) : Setting

val preferencesMapper = tomlMapper {
    decoder { root: TomlValue.Map ->
        val params = Preferences::class.primaryConstructor?.parameters
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
        }?.let { Preferences::class.primaryConstructor?.callBy(it) }
    }
}

private fun decodeSetting(
    param: KParameter,
    value: TomlValue,
    decoder: TomlDecoder
): Pair<KParameter, Any?> {
    return param to (value as TomlValue.Map).properties.map { settingsMap ->
        val settingTomlMap = settingsMap.value as TomlValue.Map
        return@map when ((settingTomlMap.properties["type"] as TomlValue.String).value) {
            SettingTypes.Input -> settingsMap.key to convertSetting<InputSetting>(
                settingTomlMap,
                decoder
            )
            SettingTypes.Textarea -> settingsMap.key to convertSetting<TextareaSetting>(
                settingTomlMap,
                decoder
            )
            SettingTypes.Switch -> settingsMap.key to convertSetting<SwitchSetting>(
                settingTomlMap,
                decoder
            )
            SettingTypes.Select -> settingsMap.key to convertSetting<SelectSetting>(
                settingTomlMap,
                decoder
            )
            else -> settingsMap.key to null
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