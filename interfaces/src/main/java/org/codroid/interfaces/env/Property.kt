package org.codroid.interfaces.env

import cc.ekblad.toml.TomlMapper
import cc.ekblad.toml.decode
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.serialization.from
import cc.ekblad.toml.util.InternalAPI
import org.codroid.interfaces.utils.PathUtils
import org.codroid.interfaces.utils.defaultTomlMapper
import java.io.InputStream
import java.nio.file.Path
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

@OptIn(InternalAPI::class)
open class Property<T> : Resource {
    var entity: T? = null

    constructor(path: Path?, kType: KType) : super(path) {
        if (path != null) {
            entity = this.getTomlMapper().decode(kType, TomlValue.from(path))
        }
    }

    constructor(addonEnv: AddonEnv, relativePath: String, kType: KType) : super(
        addonEnv,
        relativePath
    ) {
        entity = this.getTomlMapper().decode(kType, TomlValue.from(this.toPath()))
    }

    constructor(path: Path?, clazz: Class<T>) :
            this(path, Reflection.createKotlinClass(clazz).createType())

    constructor(addonEnv: AddonEnv, relativePath: String, clazz: Class<T>) : this(
        addonEnv,
        relativePath,
        Reflection.createKotlinClass(clazz).createType()
    )

    constructor(inputStream: InputStream, kType: KType) : this(null, kType) {
        entity = this.getTomlMapper().decode(kType, TomlValue.from(inputStream))
    }

    constructor(inputStream: InputStream, clazz: Class<T>) : this(
        inputStream, Reflection.createKotlinClass(clazz).createType()
    )


    open fun getTomlMapper(): TomlMapper = defaultTomlMapper()
}