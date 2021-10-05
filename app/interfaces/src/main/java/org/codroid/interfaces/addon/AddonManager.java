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

import android.content.Context;
import android.text.TextUtils;

import org.codroid.interfaces.addon.exception.AddonClassLoadException;
import org.codroid.interfaces.addon.exception.IncompleteAddonDescriptionException;
import org.codroid.interfaces.addon.exception.NoAddonDescriptionFoundException;
import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.log.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages all the addons, include importing and loading.
 * DO NOT USE IT IN ADDONS !!
 */
public final class AddonManager implements Loggable {

    private static AddonManager mInstance = null;

    public static synchronized AddonManager get() {
        if (mInstance == null) mInstance = new AddonManager();
        return mInstance;
    }

    private Map<AddonDescription, Addon> addons = new HashMap<>();

    private Context context;
    private static Logger logger;
    public static String ADDON_DIR_NAME = "addons";

    /**
     * Import an addon from external storage.
     *
     * @param file the File requires to import.
     * @return the result of processing.
     */
    public Result importExternalAddon(File file) {
        try {
            File externalAddon = new File(getAddonDir(), file.getName());
            Files.copy(file.toPath(), externalAddon.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(Result.FAILED, e.getMessage());
        }
        return new Result(Result.SUCCESS);
    }

    /**
     * There is the core function of loading addons,
     * which will scan the directory 'org.codroid.editor/files/plugins'.
     * The addon will not be loaded if it has been loaded or caused some error while loading.
     *
     * @return the result of processing.
     */
    public Result loadAddons() {
        File pluginFile = context.getExternalFilesDir(ADDON_DIR_NAME);
        if (!pluginFile.exists()) pluginFile.mkdir();
        AddonLoader loader = new AddonLoader();
        getLogger().i("Start loading addons.");
        if (pluginFile.list() != null) {
            for (var it : pluginFile.list()) {
                try {
                    String path = pluginFile + File.separator + it;
                    AddonDescription description = loader.getAddonDescription(path);
                    if (!isLoaded(description)) {
                        AddonBase addon = loader.loadAddon(description, path, pluginFile.getCanonicalPath());
                        addon.assignLogger(new Logger(context, description.get().getName() + "/" + description.get().getPackage()));
                        addon.onLoading();
                        addons.put(description, addon);
                    } else {
                        getLogger().w(description.get().getName() + " was loaded before, so this loading is invalid.");
                    }
                } catch (NoAddonDescriptionFoundException | AddonClassLoadException | IncompleteAddonDescriptionException e) {
                    e.printStackTrace(getLogger());
                } catch (IOException e) {
                    e.printStackTrace();
                    getLogger().e(it + " not found");
                }
            }
            return new Result(Result.SUCCESS);
        }
        return new Result(Result.FAILED, "No addon exists.");
    }

    /**
     * The number of addons loaded.
     *
     * @return counts
     */
    public int getAddonCountLoaded() {
        return addons.size();
    }

    public int getAddonCountImported() {
        File temp = getAddonDir();
        return temp.list() != null ? temp.list().length : 0;
    }

    public Map<AddonDescription, Addon> loadedAddons() {
        return addons;
    }

    /**
     * Initialize the AddonManager
     * It must be called at Application
     *
     * @param context app context
     */
    public void initialize(Context context) {
        this.context = context;
    }

    public void terminateAllAddons() {
        loadedAddons().values().forEach(Addon::onAppExited);
    }

    private boolean isLoaded(AddonDescription description) {
        for (var it : addons.keySet()) {
            if (TextUtils.equals(it.get().getPackage(), description.get().getPackage()))
                return true;
        }
        return false;
    }

    private File getAddonDir() {
        return context.getExternalFilesDir(ADDON_DIR_NAME);
    }


    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(context, "Codroid");
        return logger;
    }

    public static class Result {

        public static int SUCCESS = 0;
        public static int FAILED = 1;

        private int mCode;
        private String message;
        private int loadedCount;

        public Result(int code) {
            this.mCode = code;
            this.message = "No message";
        }

        public Result(int code, String message) {
            this.mCode = code;
            this.message = message;
        }

        public int getLoadedCount() {
            return loadedCount;
        }

        public void setLoadedCount(int loadedCount) {
            this.loadedCount = loadedCount;
        }

        public boolean isSucceed() {
            return mCode == SUCCESS;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
