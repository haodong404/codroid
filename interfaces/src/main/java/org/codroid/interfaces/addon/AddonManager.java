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
import android.util.Log;

import org.codroid.interfaces.CodroidEnv;
import org.codroid.interfaces.evnet.AddonImportEvent;
import org.codroid.interfaces.evnet.EventCenter;
import org.codroid.interfaces.exceptions.AddonClassLoadException;
import org.codroid.interfaces.exceptions.AddonImportException;
import org.codroid.interfaces.exceptions.IncompleteAddonDescriptionException;
import org.codroid.interfaces.exceptions.NoAddonDescriptionFoundException;
import org.codroid.interfaces.exceptions.PropertyInitException;
import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class manages all the addons, include importing and loading.
 * DO NOT USE IT IN ADDONS !!
 */
public final class AddonManager extends CodroidEnv implements Loggable {

    private static AddonManager mInstance = null;

    public static synchronized AddonManager get() {
        if (mInstance == null) mInstance = new AddonManager();
        return mInstance;
    }

    private Map<AddonDescription, Addon> addons = new HashMap<>();

    private static EventCenter eventCenter;

    public AddonManager() {
        if (eventCenter == null) {
            eventCenter = new EventCenter();
        }
    }

    /**
     * Import an addon from external storage.
     *
     * @param file the File requires to import.
     * @return the result of processing.
     */
    public Result importExternalAddon(File file) {
        try {
            File temp = file;
            JarFile jarFile = new JarFile(temp);
            AddonDescription description = AddonLoader.loadAddonDescriptionInJar(temp);
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry it = entries.nextElement();
                if (!it.isDirectory() && !it.getName().endsWith(".class")) {
                    InputStream inputStream = jarFile.getInputStream(it);
                    var compressedPath = PathUtils.splice(getAddonRoot(description.get().getPackage()), it.getName());
                    if (!Files.exists(compressedPath, LinkOption.NOFOLLOW_LINKS)) {
                        Files.createDirectories(compressedPath.getParent());
                    }
                    File compressed = compressedPath.toFile();
                    if (!compressed.exists()) compressed.createNewFile();
                    Files.copy(inputStream,
                            compressed.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
            for (var it : eventCenter().<AddonImportEvent>execute(EventCenter.EventsEnum.ADDON_IMPORT)) {
                temp = it.beforeImport(temp);
            }

            Files.copy(temp.toPath(),
                    PathUtils.splice(getAddonRoot(description.get().getPackage()), AddonDexClassLoader.DEX_FILE),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(Result.FAILED, e.getMessage());
        } catch (NoAddonDescriptionFoundException | AddonImportException | IncompleteAddonDescriptionException e) {
            e.printStackTrace(getLogger());
            return new Result(Result.FAILED, e.getMessage());
        }
        return new Result(Result.SUCCESS);
    }

    /**
     * There is the core function of loading addons,
     * which will scan the directory 'org.codroid.editor/files/addons'.
     * The addon will not be loaded if it has been loaded or caused some error while loading.
     *
     * @return the result of processing.
     */
    public Result loadAddons() {
        File pluginFile = getAddonsDir();
        if (!pluginFile.exists()) pluginFile.mkdir();

        getLogger().i("Start loading addons.");
        if (pluginFile.list() != null) {
            for (var it : pluginFile.list()) {
                try {
                    AddonDescription description = AddonLoader.getAddonDescription(PathUtils.splice(getAddonRoot(it),
                            AddonDescription.ADDON_DESCRIPTION_FILE_NAME));
                    AddonDexClassLoader classLoader = AddonLoader.generateClassLoader(description);
                    if (!isLoaded(description)) {
                        AddonBase addon = AddonLoader.loadAddon(classLoader);
                        addon.createCodroidEnv(this);
                        addon.setIdentify(description.get().getPackage());
                        addon.onLoading();
                        addons.put(description, addon);
                        AddonLoader.loadEvents(classLoader, addon);
                    } else {
                        getLogger().w(description.get().getName() + " was loaded before, so this loading is invalid.");
                    }
                } catch (NoAddonDescriptionFoundException | AddonClassLoadException |
                        IncompleteAddonDescriptionException | PropertyInitException e) {
                    e.printStackTrace(getLogger());
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
        File temp = getAddonsDir();
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
        initContext(context);
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

    public EventCenter eventCenter() {
        return eventCenter;
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
