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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dalvik.system.DexClassLoader;

public class AddonDexClassLoader extends DexClassLoader {

    private AddonDescription addonDescription;

    public AddonDexClassLoader(AddonDescription description, ClassLoader parent) {
        super(description.getFilePath(), description.getFilePath(), null, parent);
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
     * Return the events of the addon.
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
}
