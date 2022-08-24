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

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.preference.PreferencesProperty;

public class ResourceFactory {

    public static final int RAW_RESOURCE = 0;
    public static final int RAW_PROPERTY = 1;
    public static final int APPEARANCE_PROPERTY = 2;
    public static final int IMAGE_RESOURCE = 3;
    public static final int PREFERENCES_PROPERTY = 4;

    public Resource createResource(AddonEnv addonEnv, String path, int type) {
        switch (type) {
            case RAW_RESOURCE:
                return new ResourceRaw(addonEnv, path);
            case RAW_PROPERTY:
                return new Property(addonEnv, path);
            case APPEARANCE_PROPERTY:
                return new AppearanceProperty(addonEnv, path);
            case IMAGE_RESOURCE:
                return new ImageResource(addonEnv, path);
            case PREFERENCES_PROPERTY:
                return new PreferencesProperty(addonEnv, path);
            default:
                return null;
        }
    }
}
