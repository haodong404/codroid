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
import android.text.TextUtils;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.log.Logger;
import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class CodroidEnv implements Loggable {

    private Context context;
    public static String ADDONS_DIR_NAME = "addons";
    public static String LOG_FILE_DIR = "logs";
    public static String TEMP_DIR = "temp";

    private Logger logger;

    // This is the final appearance after overlay.
    private AppearanceProperty overlayAppearance;

    public CodroidEnv(Context context) {
        this.context = context;
    }

    public CodroidEnv() {

    }

    public void createCodroidEnv(CodroidEnv codroidEnv) {
        initContext(codroidEnv.getContext());
    }

    protected void initContext(Context context) {
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

    public AppearanceProperty appearance() {
        return overlayAppearance;
    }

    public File getAddonsDir() {
        return getCodroidExternalDir(ADDONS_DIR_NAME);
    }

    public File getAddonRoot(String _package) {
        return PathUtils.splice(getAddonsDir(), _package).toFile();
    }

    public File getCodroidExternalDir(String subDir) {
        return context.getExternalFilesDir(subDir);
    }

    public File getTempDir() {
        return getCodroidExternalDir(TEMP_DIR);
    }

    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(context);
        return logger.with("Codroid");
    }
}
