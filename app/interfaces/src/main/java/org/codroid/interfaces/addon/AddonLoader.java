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

package org.codroid.interfaces.addon;

import org.codroid.interfaces.addon.exception.AddonClassLoadException;
import org.codroid.interfaces.addon.exception.IncompleteAddonDescriptionException;
import org.codroid.interfaces.addon.exception.NoAddonDescriptionFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

public class AddonLoader {

    public static String ADDON_DESCRIPTION_FILE_NAME = "addon-des.toml";


    public AddonBase loadAddon(AddonDescription description, String dexPath, String optimizedDirectory) throws AddonClassLoadException {
        DexClassLoader classLoader = new DexClassLoader(dexPath, optimizedDirectory, null, Thread.currentThread().getContextClassLoader());
        try {
            Class<?> addonBaseClass = classLoader.loadClass(description.get().getPackage() + "." + description.get().getEnterPoint());
            return (AddonBase) addonBaseClass.getConstructors()[0].newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new AddonClassLoadException("Cannot create the instance of " +
                    description.get().getPackage() + "." + description.get().getEnterPoint() +
                    ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new AddonClassLoadException("Main class(" + description.get().getPackage() + "." + description.get().getName() +
                    ") not found, please ensure that you have filled the field of package and enterPoint correctly in addon-des.toml");
        }
    }

    public AddonDescription getAddonDescription(String filePath) throws NoAddonDescriptionFoundException, IncompleteAddonDescriptionException {
        try {
            JarFile jarFile = new JarFile(filePath);
            JarEntry jarEntry = jarFile.getJarEntry(ADDON_DESCRIPTION_FILE_NAME);
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            if (jarEntry == null) {
                throw new NoAddonDescriptionFoundException(filePath);
            }
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            AddonDescription description = new AddonDescription(new String(bytes));
            if(!description.checkIntegrity().isEmpty()) {
                throw new IncompleteAddonDescriptionException(description.checkIntegrity());
            }
            return description;
        } catch (IOException e) {
            e.printStackTrace();
            throw new NoAddonDescriptionFoundException(filePath);
        }
    }
}
