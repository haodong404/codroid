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

package org.codroid.interfaces.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static Path splice(String first, String... more) {
        StringBuilder builder = new StringBuilder(first);
        if (!first.endsWith(File.separator)) {
            builder.append(File.separator);
        }

        for (int i = 0; i < more.length; i++) {
            if (i == 0){
                if (more[i].startsWith(File.separator)) {
                    builder.append(more[i].substring(1));
                } else {
                    builder.append(more[i]);
                }
            } else {
                builder.append(more[i]);
            }
        }
        return Paths.get(builder.toString());
    }

    public static Path splice(File file, String... more) {
        return splice(file.getPath(), more);
    }

    public static Path splice(File file, File... more) {
        String[] temp = new String[more.length];
        for (int i = 0 ;i < more.length; i ++){
            temp[i] = more[i].getPath();
        }
        return splice(file, temp);
    }
}
