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

package org.codroid.interfaces;

import android.content.Context;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.exceptions.PropertyInitException;
import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.log.Logger;
import org.codroid.interfaces.utils.PathUtils;

import java.io.File;

public class AddonEnv extends CodroidEnv implements Loggable {

    private String identify;
    private Logger logger;

    public AddonEnv() {

    }

    public AddonEnv(String identify, Context context) {
        super(context);
        this.identify = identify;
    }

    public void setIdentify(String id) {
        this.identify = id;
    }

    public String getIdentify() {
        return identify;
    }


    protected Property findProperty(String relativePath) {
        return new Property(PathUtils.splice(getAddonRoot(getIdentify()), relativePath));
    }

    protected AppearanceProperty openAppearanceProperty(String fileName) throws PropertyInitException {
        return (AppearanceProperty) findProperty(fileName).open();
    }

    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(getContext());
        return logger.with(getIdentify());
    }
}
