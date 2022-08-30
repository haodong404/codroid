package org.codroid.interfaces.preference

import cc.ekblad.toml.TomlMapper
import com.tencent.mmkv.MMKV
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.codroid.interfaces.env.AddonEnv
import org.codroid.interfaces.env.Property
import java.io.InputStream
import kotlin.reflect.typeOf

class PreferenceProperty : Property<Preference>, PreferenceOperation {

    private val mmkv: MMKV

    constructor(addonEnv: AddonEnv, relativePath: String) : super(
        addonEnv,
        relativePath,
        typeOf<Preference>()
    ) {
        mmkv = MMKV.mmkvWithID(addonEnv.identify + "-kv", addonEnv.addonRootDir.absolutePath)
        init()
    }

    constructor(id: String, mmkvPath: String, inputStream: InputStream) : super(
        inputStream,
        typeOf<Preference>()
    ) {
        mmkv = MMKV.mmkvWithID(id, mmkvPath)
        init()
    }

    fun init() {
        entity?.settings?.forEach { (k, v) ->
            when (v) {
                is InputSetting -> {
                    if (v.valueType == "STRING") {
                        putString(k, v.valueType)
                    } else {
                        putInt(k, v.defaultValue.toInt())
                    }
                }
                is TextareaSetting -> putString(k, v.defaultValue)
                is SwitchSetting -> putBoolean(k, v.defaultValue)
                is SelectSetting -> putInt(k, v.defaultValue)
            }
        }
    }

    override fun putString(key: String, value: String) {
        mmkv.encode(key, value)
    }

    override fun putBoolean(key: String, value: Boolean) {
        mmkv.encode(key, value)
    }

    override fun putInt(key: String, value: Int) {
        mmkv.encode(key, value)
    }

    override fun getString(key: String) = mmkv.decodeString(key) ?: ""

    override fun getBoolean(key: String) = mmkv.decodeBool(key)

    override fun getInt(key: String) = mmkv.decodeInt(key)

    override fun getTomlMapper(): TomlMapper {
        return preferencesMapper
    }

    fun toJson(): String {
        if (entity != null) {
            return Json.encodeToString(entity)
        }
        return ""
    }
}