package org.codroid.interfaces.utils

import cc.ekblad.toml.TomlMapper
import cc.ekblad.toml.decode
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.serialization.from
import cc.ekblad.toml.tomlMapper
import cc.ekblad.toml.util.InternalAPI
import org.codroid.interfaces.addon.Addon
import org.codroid.interfaces.addon.AddonDescription
import java.io.InputStream
import java.lang.reflect.Modifier
import java.nio.file.Path
import java.util.function.LongFunction
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.cast
import kotlin.reflect.full.cast
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

fun defaultTomlMapper(): TomlMapper {
    return tomlMapper { }
}

fun decode2Map(path: Path, mapper: TomlMapper): Map<String, Any> {
    return mapper.decode(path)
}

fun decode2Map(content: String, mapper: TomlMapper): Map<String, Any> {
    return mapper.decode(content)
}

fun decode2Map(inputStream: InputStream, mapper: TomlMapper): Map<String, Any> {
    return mapper.decode(inputStream)
}

@OptIn(InternalAPI::class)
fun <T> decode(content: String, clazz: Class<T>): T {
    return defaultTomlMapper().decode(
        Reflection.createKotlinClass(clazz).createType(),
        TomlValue.from(content)
    )
}

fun descriptionMapper(): TomlMapper {
    val mapper = tomlMapper {
        mapping<AddonDescription.Addon>("package" to "_package")
    }
    return mapper
}

fun decode2Description(content: String): AddonDescription.Addon {
    return descriptionMapper().decode(content)
}

fun decode2Description(path: Path): AddonDescription.Addon {
    return descriptionMapper().decode(path)
}

fun <T> toObject(map: Map<*, *>, clazz: Class<T>): T {
    val result = clazz.newInstance()
    val fields = clazz.declaredFields
    fields.forEach {
        val mod = it.modifiers
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
            return@forEach
        }
        it.isAccessible = true
        if (!map.containsKey(it.name)) {
            return@forEach
        }
        if (map[it.name] is Map<*, *>) {
            it.set(result, toObject(map[it.name] as Map<*, *>, it.type))
        } else {
            it.set(result, map[it.name])
        }
    }
    return result
}