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

package org.codroid.interfaces;

import org.codroid.interfaces.exceptions.PropertyInitException;

import java.io.File;
import java.nio.file.Path;

import me.grison.jtoml.impl.Toml;

public class Property {

    protected Toml toml;

    private Path path;

    public Property(Path path) {
        this.path = path;
    }

    public Property open() throws PropertyInitException {
        try {
            if (path == null) {
                throw new PropertyInitException("The property file is null !!");
            }
            toml = Toml.parse(path.toFile());
        } catch (Exception e) {
            throw new PropertyInitException("Toml parsed error: " + e.getMessage());
        }
        return this;
    }


    public Property open(String content) {
        toml = Toml.parse(content);
        return this;
    }

    public void close() {
        toml = null;
    }
}
