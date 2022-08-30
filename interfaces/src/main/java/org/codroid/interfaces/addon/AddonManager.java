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

import androidx.room.Room;

import com.tencent.mmkv.MMKV;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.ThemeBase;
import org.codroid.interfaces.database.AddonDao;
import org.codroid.interfaces.database.AddonDatabase;
import org.codroid.interfaces.database.AddonsEntity;
import org.codroid.interfaces.env.CodroidEnv;
import org.codroid.interfaces.evnet.AddonImportEvent;
import org.codroid.interfaces.evnet.EventCenter;
import org.codroid.interfaces.exceptions.AddonClassLoadException;
import org.codroid.interfaces.exceptions.AddonImportException;
import org.codroid.interfaces.exceptions.IncompleteAddonDescriptionException;
import org.codroid.interfaces.exceptions.NoAddonDescriptionFoundException;
import org.codroid.interfaces.exceptions.PropertyInitException;
import org.codroid.interfaces.log.Logger;
import org.codroid.interfaces.preference.CodroidPreferenceGroup;
import org.codroid.interfaces.preference.PreferenceProperty;
import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * This class manages all the addons, include importing and loading.
 * DO NOT USE IT IN ADDONS !!
 */
public final class AddonManager extends CodroidEnv {

    private static AddonManager mInstance = null;
    private Context context = null;

    public static synchronized AddonManager get() {
        if (mInstance == null) mInstance = new AddonManager();
        return mInstance;
    }

    private static EventCenter eventCenter;
    private AddonDatabase database;
    private Logger logger;

    // It stores all instances of loaded addons.
    public Map<String, Addon> addons = new HashMap<>();

    public AddonManager() {
        if (eventCenter == null) {
            eventCenter = new EventCenter();
        }
    }

    @Override
    protected void registerPreference(String path) {
        try {
            var preferences = this.context.getAssets().list("preferences");
            for (String preference : preferences) {
                if (TextUtils.equals(preference, "text-editor.toml")) {
                    var inputStream = this.context.getAssets().open("preferences/" + preference);
                    codroidPreferences.put(CodroidPreferenceGroup.TEXT_EDITOR,
                            new PreferenceProperty("preference-text-editor-kv", getPreferencesDir().getPath(), inputStream));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public enum ImportStage {
        READING_DESCRIPTION,
        PROCESSING
    }

    public enum LoadStage {
        LOADING
    }

    public interface ProgressCallback<T, V> {

        void progress(int total, int now, T stage, V attachment);

        void done();

        void error(Throwable e);
    }

    public void importExternalAddonAsync(File file, ProgressCallback<ImportStage, String> callback) {
        new Thread(() -> importExternalAddon(file, callback)).start();
    }

    public void importExternalAddon(File file) {
        importExternalAddon(file, null);
    }

    /**
     * Import an addon from external storage.
     *
     * @param file     the File requires to import.
     * @param callback progress callbacks
     */
    public void importExternalAddon(File file, ProgressCallback<ImportStage, String> callback) {
        try {
            File temp = file;
            if (callback != null) {
                callback.progress(2, 1, ImportStage.READING_DESCRIPTION, file.getName());
            }
            JarFile jarFile = new JarFile(temp);
            // Read the description first.
            AddonDescription description = AddonLoader.parsingDescriptionInJar(temp);

            if (isLoaded(description.get().getPackage())) {
                if (callback != null) {
                    callback.error(new AddonImportException("The addon (" + description.get().getPackage() + ") has been loaded before."));
                }
                return;
            }

            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry it = entries.nextElement();
                if (!it.isDirectory() && !it.getName().endsWith(".class") && !it.getName().endsWith(".MF")) {
                    InputStream inputStream = jarFile.getInputStream(it);
                    var compressedPath = PathUtils.splice(getAddonRootDir(description.get().getPackage()), it.getName());
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

            if (callback != null) {
                callback.progress(2, 2, ImportStage.PROCESSING, "");
            }

            // Invoke all the events from addons.
            for (var it : eventCenter().<AddonImportEvent>execute(EventCenter.EventsEnum.ADDON_IMPORT)) {
                try {
                    temp = it.beforeImport(temp);
                } catch (Exception e) {
                    getLogger().e("Event: " + it.getClass().getName() + ", calls failed! (" + e.toString() + ")");
                }
            }

            // Copy the addon file to specific directory.
            Files.copy(temp.toPath(),
                    PathUtils.splice(getAddonRootDir(description.get().getPackage()), AddonDexClassLoader.DEX_FILE),
                    StandardCopyOption.REPLACE_EXISTING);

            // Insert the addon to database
            getAddonDao().insertAddon(new AddonsEntity(description.get().getName(),
                    description.get().getPackage(),
                    getAddonRootDir(description.get().getPackage()).getPath(),
                    1));

            if (callback != null) {
                callback.done();
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.error(e);
            }
        } catch (NoAddonDescriptionFoundException | AddonImportException | IncompleteAddonDescriptionException e) {
            e.printStackTrace(getLogger());
            if (callback != null) {
                callback.error(e);
            }
        }
    }

    /**
     * Load addons asynchronously
     *
     * @param callback progress callback
     */
    public void loadAddonsAsync(ProgressCallback<LoadStage, String> callback) {
        new Thread(() -> loadAddons(callback)).start();
    }

    /**
     * Load addons.
     */
    public void loadAddons() {
        loadAddons(null);
    }

    /**
     * There is the core function of loading addons,
     * which will scan the directory 'org.codroid.editor/files/addons'.
     * The addon will not be loaded if it has been loaded or caused some error while loading.
     *
     * @param callback progress callback
     */
    public void loadAddons(ProgressCallback<LoadStage, String> callback) {
        File pluginFile = getAddonsDir();
        if (!pluginFile.exists()) pluginFile.mkdir();

        getLogger().i("Start loading addons.");
        if (pluginFile.list() != null) {
            int progress = 1;
            for (var it : Objects.requireNonNull(pluginFile.list())) {
                if (callback != null) {
                    callback.progress(Objects.requireNonNull(pluginFile.list()).length, progress, LoadStage.LOADING, it);
                }
                progress++;
                try {
                    AddonDescription description = AddonLoader.findAddonDescription(PathUtils.splice(getAddonRootDir(it),
                            AddonDescription.ADDON_DESCRIPTION_FILE_NAME));
                    AddonDexClassLoader classLoader = AddonLoader.generateClassLoader(description);
                    if (!isLoaded(description.get().getPackage())) {
                        AddonBase addon = AddonLoader.loadAddon(classLoader);
                        addon.createAddonEvn(this, description.get().getPackage());
                        addon.onLoading();
                        // Load events from the addon.
                        if (description.get().getEvents() != null) {
                            AddonLoader.loadEvents(classLoader, addon);
                        }
                        // Load themes if exists.
                        if (description.get().getTheme() != null) {
                            ThemeBase themeBase = AddonLoader.loadTheme(classLoader);
                            themeBase.attached(addon);
                            AppearanceProperty property = themeBase.loadTheme();
                            activeAppearances.put(classLoader.addonTheme(), property);
                        }
                        addons.put(description.get().getPackage(), addon);
                    } else {
                        getLogger().w(description.get().getName() + " was loaded before, so this loading is invalid.");
                    }
                } catch (NoAddonDescriptionFoundException | AddonClassLoadException |
                        IncompleteAddonDescriptionException | PropertyInitException e) {
                    e.printStackTrace(getLogger());
                    if (callback != null) {
                        callback.error(e);
                    }
                }
            }
        }
    }

    /**
     * The number of addons loaded.
     *
     * @return counts
     */
    public int addonLoadedCount() {
        return addons.size();
    }

    /**
     * Get all imported AddonDescriptions.
     *
     * @return addon descriptions.
     */
    public List<AddonDescription> allAddonDescriptions() {
        return getAddonDao().findAll().stream().map(addonsEntity -> {
            try {
                return AddonLoader.findAddonDescription(PathUtils.splice(addonsEntity.addonPath, AddonDescription.ADDON_DESCRIPTION_FILE_NAME));
            } catch (NoAddonDescriptionFoundException | IncompleteAddonDescriptionException | PropertyInitException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::isNull).collect(Collectors.toList());
    }

    /**
     * Initialize the AddonManager.
     * It must be called at Application.
     */
    public void initialize(Context context) {
        this.context = context;
        MMKV.initialize(context);
        createCodroidEnv(context.getExternalFilesDir(null));
        this.database = Room.databaseBuilder(context, AddonDatabase.class, "addon-database")
                .allowMainThreadQueries()
                .build();
        registerPreference("");
    }

    /**
     * Get the addon's dao.
     *
     * @return addon dao.
     */
    public AddonDao getAddonDao() {
        return database.addonDao();
    }


    public void terminateAllAddons() {
        addons.values().forEach(Addon::onAppExited);
    }

    /**
     * Check whether the addon has been loaded.
     *
     * @param pkg addon's package name.
     * @return true if loaded.
     */
    private boolean isLoaded(String pkg) {
        for (var it : addons.keySet()) {
            if (TextUtils.equals(it, pkg))
                return true;
        }
        return false;
    }

    /**
     * Get the event center to manage events.
     *
     * @return EventCenter
     */
    public EventCenter eventCenter() {
        return eventCenter;
    }


    @Override
    public Logger getLogger() {
        if (logger == null) {
            logger = new Logger(getLogsDir(), "Codroid");
        }
        return logger;
    }
}
