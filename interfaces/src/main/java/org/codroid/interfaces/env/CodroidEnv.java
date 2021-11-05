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
import android.text.TextUtils;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;
import org.codroid.interfaces.exceptions.AttributeNotFoundException;
import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The basic environment of Codroid addon,
 * which contains global information about addons and provided by Codroid.
 * <p>
 * It allows to access specific directories, loggers, theme, etc.
 */
public abstract class CodroidEnv implements Loggable {

    private Context context;

    public static String ADDONS_DIR = "addons";
    public static String LOG_FILE_DIR = "logs";
    public static String TEMP_DIR = "temp";


    protected Map<String, AppearanceProperty> activeAppearances = new HashMap<>();

    public CodroidEnv(Context context) {
        this.context = context;
    }

    public CodroidEnv() {

    }

    public void createCodroidEnv(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public File getLogsDir() {
        return getCodroidExternalDir(LOG_FILE_DIR);
    }

    public boolean isAddonExist(String _package) {
        for (var i : Objects.requireNonNull(getAddonsDir().list())) {
            if (TextUtils.equals(i, _package)) {
                return true;
            }
        }
        return false;
    }


    public Optional<Part> appearancePart(AppearanceProperty.PartEnum partEnum) {
        if (activeAppearances.keySet().stream().findFirst().isPresent()) {
            return appearancePart(activeAppearances.keySet().stream().findFirst().get(),
                    partEnum);
        }
        return Optional.empty();
    }

    private Optional<Part> appearancePart(String which, AppearanceProperty.PartEnum partEnum) {
        AppearanceProperty appearanceProperty = activeAppearances.get(which);
        if (appearanceProperty != null) {
            try {
                return Optional.of(appearanceProperty.part(partEnum));
            } catch (AttributeNotFoundException e) {
                e.printStackTrace(getLogger());
            }
        }
        return Optional.empty();
    }

    public File getAddonsDir() {
        return getCodroidExternalDir(ADDONS_DIR);
    }

    public File getAddonRootDir(String _package) {
        return PathUtils.splice(getAddonsDir(), _package).toFile();
    }

    public File getCodroidExternalDir(String subDir) {
        return context.getExternalFilesDir(subDir);
    }

    public File getTempDir() {
        return getCodroidExternalDir(TEMP_DIR);
    }

}
