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
import org.codroid.interfaces.evnet.Event;
import org.codroid.interfaces.evnet.EventCenter;
import org.codroid.interfaces.evnet.exception.EventClassLoadException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonLoader {

    public static String ADDON_DESCRIPTION_FILE_NAME = "addon-des.toml";


    public AddonBase loadAddon(AddonDexClassLoader classLoader) throws AddonClassLoadException {
        try {
            Class<?> addonBaseClass = classLoader.loadClass(classLoader.addonMainClass());
            return (AddonBase) addonBaseClass.getConstructors()[0].newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new AddonClassLoadException("Cannot create the instance of " +
                    classLoader.addonMainClass() +
                    ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new AddonClassLoadException("Main class(" + classLoader.addonMainClass() +
                    ") not found, please ensure that you have filled the field of package and enterPoint correctly in addon-des.toml");
        }
    }

    public AddonDexClassLoader generateClassLoader(AddonDescription description) {
        return new AddonDexClassLoader(description, Thread.currentThread().getContextClassLoader());
    }

    public void loadEvents(AddonDexClassLoader classLoader, Addon addon) throws EventClassLoadException {
        for (var i : classLoader.addonEvents()) {
            try {
                Class<?> eventClass = classLoader.loadClass(i);
                Event event = (Event) eventClass.getConstructors()[0].newInstance();
                event.init(addon);
                Arrays.stream(eventClass.getInterfaces())
                        .filter(aClass -> AddonManager.get().eventCenter().isAnAddonEvent(aClass))
                        .forEach(it -> AddonManager.get().eventCenter().register(EventCenter.EventsEnum.getEnumByClass(it), event));
            } catch (ClassNotFoundException e) {
                throw new EventClassLoadException("Event class: " + i + " cannot found.");
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new EventClassLoadException("Cannot create the instance of the event: " + i);
            }
        }
    }

    public AddonDescription getAddonDescription(String dirPath, String name) throws NoAddonDescriptionFoundException, IncompleteAddonDescriptionException {
        String filePath = dirPath + File.separator + name;
        try {
            JarFile jarFile = new JarFile(filePath);
            JarEntry jarEntry = jarFile.getJarEntry(ADDON_DESCRIPTION_FILE_NAME);
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            if (jarEntry == null) {
                throw new NoAddonDescriptionFoundException(filePath);
            }
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            AddonDescription description = new AddonDescription(dirPath, name, new String(bytes));
            if (!description.checkIntegrity().isEmpty()) {
                throw new IncompleteAddonDescriptionException(description.checkIntegrity());
            }
            return description;
        } catch (IOException e) {
            e.printStackTrace();
            throw new NoAddonDescriptionFoundException(filePath);
        }
    }
}
