/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.interfaces.env;

import org.codroid.interfaces.utils.PathUtils;
import org.codroid.interfaces.utils.TomlKt;

import java.nio.file.Path;
import java.util.Map;

import cc.ekblad.toml.TomlMapper;

public class Property extends Resource {

    protected Map<String, Object> map;
    protected static final TomlMapper mDefaultMapper = TomlKt.defaultTomlMapper();

    public Property(Path path) {
        super(path);
        if (path != null) {
            this.map = TomlKt.decode2Map(path, getTomlMapper());
        }
    }

    public <T> T getAs(Class<T> clazz) {
        return TomlKt.toObject(getAttributes(), clazz);
    }

    public Map<String, Object> getAttributes() {
        return map;
    }

    public Property(AddonEnv addonEnv, String relativePathStr) {
        super(addonEnv, relativePathStr);
        map = TomlKt.decode2Map(toPath(), getTomlMapper());
    }

    public TomlMapper getTomlMapper() {
        return mDefaultMapper;
    }
}
