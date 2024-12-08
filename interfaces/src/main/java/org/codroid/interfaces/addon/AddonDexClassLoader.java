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

import org.codroid.interfaces.utils.PathUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dalvik.system.DexClassLoader;

/**
 * This file is an dex class loader for loading addons.
 */
public class AddonDexClassLoader extends DexClassLoader {

    private AddonDescription addonDescription;
    public static final String DEX_FILE = "classes.dex";

    public AddonDexClassLoader(AddonDescription description, ClassLoader parent) {
        super(PathUtils.splice(AddonManager.get().getAddonRootDir(description.get().getPackage()), DEX_FILE).toString(),
                AddonManager.get().getAddonsDir().getPath(),
                null, parent);
        this.addonDescription = description;
    }

    public AddonDescription getAddonDescription() {
        return addonDescription;
    }

    /**
     * Return the main class of the addon.
     *
     * @return full path of the addon.
     */
    public String addonMainClass() {
        if (addonDescription.get().getEnterPoint().startsWith(".")) {
            return addonDescription.get().getPackage() + addonDescription.get().getEnterPoint();
        } else {
            return addonDescription.get().getEnterPoint();
        }
    }

    /**
     * Get events.
     *
     * @return full path of each event class.
     */
    public List<String> addonEvents() {
        List<String> temp = addonDescription.get().getEvents();
        if (temp == null) temp = Collections.emptyList();
        return temp.stream().map(s -> {
            if (s.startsWith(".")) {
                return addonDescription.get().getPackage() + s;
            } else {
                return s;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Get theme
     * @return the full path of the theme.
     */
    public String addonTheme() {
        if (addonDescription.get().getTheme() == null) {
            return "";
        }
        if (addonDescription.get().getTheme().startsWith(".")) {
            return addonDescription.get().getPackage() + addonDescription.get().getTheme();
        } else {
            return addonDescription.get().getTheme();
        }

    }
}
