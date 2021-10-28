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

import org.codroid.interfaces.AddonEnv;
import org.codroid.interfaces.evnet.Event;
import org.codroid.interfaces.evnet.EventCenter;
import org.codroid.interfaces.exceptions.AddonClassLoadException;
import org.codroid.interfaces.exceptions.AddonImportException;
import org.codroid.interfaces.exceptions.EventClassLoadException;
import org.codroid.interfaces.exceptions.IncompleteAddonDescriptionException;
import org.codroid.interfaces.exceptions.NoAddonDescriptionFoundException;
import org.codroid.interfaces.exceptions.PropertyInitException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class AddonLoader {

    public static AddonBase loadAddon(AddonDexClassLoader classLoader) throws AddonClassLoadException {
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

    public static AddonDexClassLoader generateClassLoader(AddonDescription description) {
        return new AddonDexClassLoader(description, Thread.currentThread().getContextClassLoader());
    }

    public static void loadEvents(AddonDexClassLoader classLoader, AddonEnv addon) throws EventClassLoadException {
        for (var i : classLoader.addonEvents()) {
            try {
                Class<?> eventClass = classLoader.loadClass(i);
                Event event = (Event) eventClass.getConstructors()[0].newInstance();
                event.init( addon);
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

    public static AddonDescription loadAddonDescriptionInJar(File file)
            throws IncompleteAddonDescriptionException, AddonImportException, NoAddonDescriptionFoundException {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry addonDesEntry = jarFile.getJarEntry(AddonDescription.ADDON_DESCRIPTION_FILE_NAME);
            if (addonDesEntry == null) {
                throw new NoAddonDescriptionFoundException(file.getPath());
            }
            InputStream inputStream = jarFile.getInputStream(addonDesEntry);
            byte[] desByte = new byte[inputStream.available()];
            inputStream.read(desByte);
            AddonDescription description = new AddonDescription(new String(desByte));
            if (!description.checkIntegrity().isEmpty()) {
                throw new IncompleteAddonDescriptionException(description.checkIntegrity());
            }
            if (AddonManager.get().isAddonExist(description.get().getPackage())) {
                throw new AddonImportException("The addon file: " + file.getName() +
                        " ( " + description.get().getName() + "/" + description.get().getPackage() +
                        " ) was imported! ");
            }
            return description;
        } catch (IOException e) {
            throw new AddonImportException("The file you selected is in an unsupported format.");
        }
    }

    public static AddonDescription getAddonDescription(Path tomlPath)
            throws NoAddonDescriptionFoundException, IncompleteAddonDescriptionException, PropertyInitException {
        return new AddonDescription(tomlPath);
    }
}
