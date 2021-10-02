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

package org.codroid.editor.addon;

import org.codroid.editor.addon.exception.IncompleteAddonDescription;
import org.codroid.editor.addon.exception.NoAddonDescriptionFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

public class AddonLoader {

    public static String ADDON_DESCRIPTION_FILE_NAME = "addon-des.toml";


    public AddonBase loadAddon(AddonDescription description, String dexPath, String optimizedDirectory) throws NoAddonDescriptionFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        DexClassLoader classLoader = new DexClassLoader(dexPath, optimizedDirectory, null, Thread.currentThread().getContextClassLoader());
        Class<?> addonBaseClass = null;
        addonBaseClass = classLoader.loadClass(description.get().getPackage() + "." + description.get().getEnterPoint());
        return (AddonBase) addonBaseClass.getConstructors()[0].newInstance();
    }

    public AddonDescription getAddonDescription(String filePath) throws IOException, NoAddonDescriptionFoundException, IncompleteAddonDescription {
        JarFile jarFile = new JarFile(filePath);
        JarEntry jarEntry = jarFile.getJarEntry(ADDON_DESCRIPTION_FILE_NAME);
        if (jarEntry == null) {
            throw new NoAddonDescriptionFoundException(filePath);
        }
        InputStream inputStream = jarFile.getInputStream(jarEntry);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        AddonDescription description = new AddonDescription(new String(bytes));
        if(!description.checkIntegrity().isEmpty()) {
            throw new IncompleteAddonDescription(description.checkIntegrity());
        }
        return description;
    }
}
