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

import android.content.Context;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.log.Logger;

import java.io.File;

public class AddonEnv extends CodroidEnv {

    private String identify;
    private Logger logger;

    private ResourceFactory resourceFactory = new ResourceFactory();

    public AddonEnv() {

    }

    public AddonEnv(String identify, Context context) {
        super(context);
        this.identify = identify;
    }

    public void createAddonEvn(Context context, String identify) {
        createCodroidEnv(context);
        this.identify = identify;
    }

    public String getIdentify() {
        return identify;
    }

    public AppearanceProperty getAppearanceProperty(String relativePath) {
        return (AppearanceProperty) getResourceByType(relativePath, ResourceFactory.APPEARANCE_PROPERTY);
    }

    public Property getProperty(String relativePath) {
        return (Property) getResourceByType(relativePath, ResourceFactory.RAW_PROPERTY);
    }

    public File getAddonRootDir() {
        return getAddonRootDir(getIdentify());
    }

    public ResourceRaw getResource(String relativePath) {
        return (ResourceRaw) getResourceByType(relativePath, ResourceFactory.RAW_RESOURCE);
    }

    private Resource getResourceByType(String relativePath, int type) {
        return resourceFactory.createResource(this, relativePath, type);
    }

    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(getContext());
        return logger.with(getIdentify());
    }
}
