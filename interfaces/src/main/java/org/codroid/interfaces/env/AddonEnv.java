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
import org.codroid.interfaces.log.Logger;

import java.io.File;

/**
 * This class is the environment of each addon.
 * It contains various interfaces to access the information of an addon.
 */
public class AddonEnv extends CodroidEnv {

    // This field aims to identify an addon uniquely.
    // The addon package name is usually used.
    private String identify;

    private Logger logger;

    private ResourceFactory resourceFactory = new ResourceFactory();

    public AddonEnv() {

    }

    public AddonEnv(String identify) {
        this.identify = identify;
    }

    public void createAddonEvn(CodroidEnv parent, String identify) {
        this.rootFile = parent.rootFile;
        this.identify = identify;
    }

    public String getIdentify() {
        return identify;
    }

    /**
     * Find an {@link AppearanceProperty} by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @return an {@link AppearanceProperty}.
     */
    public AppearanceProperty getAppearanceProperty(String relativePath) {
        return (AppearanceProperty) getResourceByType(relativePath, ResourceFactory.APPEARANCE_PROPERTY);
    }

    /**
     * Find a {@link Property} file by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @return a {@link Property}.
     */
    public Property getProperty(String relativePath) {
        return (Property) getResourceByType(relativePath, ResourceFactory.RAW_PROPERTY);
    }

    /**
     * Return the directory that belongs to the specific addon.
     *
     * @return the addon's root directory.
     */
    public File getAddonRootDir() {
        return getAddonRootDir(getIdentify());
    }

    /**
     * Find a {@link Resource} by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @return the {@link ResourceRaw}.
     */
    public ResourceRaw getResource(String relativePath) {
        return (ResourceRaw) getResourceByType(relativePath, ResourceFactory.RAW_RESOURCE);
    }

    /**
     * Find a {@link Resource} by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @param type         The type is in the {@link ResourceFactory}
     * @return The {@link Resource}
     */
    private Resource getResourceByType(String relativePath, int type) {
        return resourceFactory.createResource(this, relativePath, type);
    }

    /**
     * Return the specific {@link Logger} of the addon.
     *
     * @return The {@link Logger}
     */
    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(getLogsDir());
        return logger.with(getIdentify());
    }
}
