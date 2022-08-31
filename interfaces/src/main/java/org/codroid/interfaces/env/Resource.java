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

import androidx.annotation.NonNull;

import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.nio.file.Path;

public abstract class Resource {
    private Path path;

    public Resource(@NonNull AddonEnv addonEnv, String path) {
        this.path = PathUtils.splice(addonEnv.getAddonRootDir(), path);
    }

    public Resource(Path path){
        this.path = path;
    }

    public Path toPath() {
        return this.path;
    }

    public File toFile(){
        return path.toFile();
    }
}