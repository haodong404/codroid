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

import org.codroid.interfaces.env.AddonEnv;
import org.codroid.interfaces.appearance.ThemeBase;
import org.codroid.interfaces.Attachment;
import org.codroid.interfaces.evnet.EventCenter;
import org.codroid.interfaces.exceptions.AddonClassLoadException;
import org.codroid.interfaces.exceptions.AddonImportException;
import org.codroid.interfaces.exceptions.AppearanceClassLoadException;
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

/**
 * This file contains static methods that load classed or information in an addon.
 */
public final class AddonLoader {

    /**
     * Load an addon.
     *
     * @param classLoader specific classloader.
     * @return the AddonBase loaded completely.
     * @throws AddonClassLoadException if load failed.
     */
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

    /**
     * Generate a classloader after serializing the addon's description.
     * Each addon owns an exclusive class loader, which loads attachments as well.
     *
     * @param description AddonDescription
     * @return AddonDexClassLoader created.
     */
    public static AddonDexClassLoader generateClassLoader(AddonDescription description) {
        return new AddonDexClassLoader(description, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Load events defined by an addon.
     *
     * @param classLoader the classloader of host addon.
     * @param addon       the AddonEnv of host addon.
     * @throws EventClassLoadException if events loaded failed.
     */
    public static void loadEvents(AddonDexClassLoader classLoader, AddonEnv addon) throws EventClassLoadException {
        for (var i : classLoader.addonEvents()) {
            try {
                Class<?> eventClass = classLoader.loadClass(i);
                Attachment event = (Attachment) eventClass.getConstructors()[0].newInstance();
                event.attached(addon);
                Arrays.stream(eventClass.getInterfaces())
                        .filter(aClass -> AddonManager.get().eventCenter().isAnAddonEvent(aClass))
                        .forEach(it -> AddonManager.get().eventCenter().register(EventCenter.EventsEnum.getEnumByClass(it), event));
            } catch (ClassNotFoundException e) {
                throw new EventClassLoadException("Event class: " + i + " cannot found.");
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new EventClassLoadException("Cannot create the instance : " + i);
            }
        }
    }

    /**
     * Parsing a description from addon's jar.
     *
     * @param file addon's jar.
     * @return AddonDescription that pared.
     * @throws IncompleteAddonDescriptionException Some field might not be defined.
     * @throws AddonImportException                unsupported format.
     * @throws NoAddonDescriptionFoundException    No addon description found in this file.
     */
    public static AddonDescription parsingDescriptionInJar(File file)
            throws IncompleteAddonDescriptionException, AddonImportException, NoAddonDescriptionFoundException {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry addonDesEntry = jarFile.getJarEntry(AddonDescription.ADDON_DESCRIPTION_FILE_NAME);
            if (addonDesEntry == null) {
                throw new NoAddonDescriptionFoundException(file.getPath());
            }
            InputStream inputStream = jarFile.getInputStream(addonDesEntry);
            byte[] desBytes = new byte[inputStream.available()];
            inputStream.read(desBytes);
            AddonDescription description = AddonDescription.parseString(new String(desBytes));
            if (!description.checkIntegrity().isEmpty()) {
                throw new IncompleteAddonDescriptionException(description.checkIntegrity());
            }
            return description;
        } catch (IOException e) {
            throw new AddonImportException("The file you selected is in an unsupported format.");
        }
    }

    /**
     * Find AddonDescription that was imported.
     *
     * @param tomlPath file path
     * @return AddonDescription that be founded.
     * @throws NoAddonDescriptionFoundException    Not found.
     * @throws IncompleteAddonDescriptionException Some field might not be defined.
     * @throws PropertyInitException               the description file could be broken.
     */
    public static AddonDescription findAddonDescription(Path tomlPath)
            throws NoAddonDescriptionFoundException, IncompleteAddonDescriptionException, PropertyInitException {
        return new AddonDescription(tomlPath);
    }

    /**
     * Load theme defined by ad addon.
     *
     * @param classLoader the classloader of host addon.
     * @return the ThemeBase of host addon.
     * @throws AppearanceClassLoadException class not found.
     */
    public static ThemeBase loadTheme(AddonDexClassLoader classLoader) throws AppearanceClassLoadException {
        try {
            Class<?> clazz = classLoader.loadClass(classLoader.addonTheme());
            return (ThemeBase) clazz.getConstructors()[0].newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new AppearanceClassLoadException("Cannot create the instance: " + classLoader.addonTheme());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new AppearanceClassLoadException("Class not found: " + classLoader.addonTheme());
        }
    }
}
