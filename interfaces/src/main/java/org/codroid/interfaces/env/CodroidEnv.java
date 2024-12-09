package org.codroid.interfaces.env;

import android.text.TextUtils;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;
import org.codroid.interfaces.exceptions.AttributeNotFoundException;
import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.preference.CodroidPreferenceGroup;
import org.codroid.interfaces.preference.PreferenceProperty;
import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/**
 * This class is the basic environment of Codroid addon,
 * which contains global information about addons and provided by Codroid.
 * <p>
 * It allows to access specific directories, loggers, theme, etc.
 */
public abstract class CodroidEnv implements Loggable {

    public final static String ADDONS_DIR = "addons";
    public final static String LOG_FILE_DIR = "logs";
    public final static String TEMP_DIR = "temp";
    // This preference dir is belong to Codroid, not plugins.
    public final static String PREFERENCES_DIR = "preferences";

    protected File rootFile;

    protected Map<String, AppearanceProperty> activeAppearances = new HashMap<>();
    protected Map<CodroidPreferenceGroup, PreferenceProperty> codroidPreferences =
            new EnumMap<>(CodroidPreferenceGroup.class);
    protected Map<String, PreferenceProperty> customPreferences = new TreeMap<>();

    public CodroidEnv(File root) {
        this.rootFile = root;
    }

    public CodroidEnv() {
    }

    public void createCodroidEnv(File root) {
        this.rootFile = root;
    }

    /**
     * Return the directory where logs stored.
     *
     * @return logs directory.
     */
    public File getLogsDir() {
        return getCodroidExternalDir(LOG_FILE_DIR);
    }

    /**
     * Determine whether the addon has existed through the packages in the addon directory.
     *
     * @param _package addon package
     * @return true if exists.
     */
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

    /**
     * Return the addon directory.
     * Android/data/org.codroid.editor/files/addons
     *
     * @return the addons directory
     */
    public File getAddonsDir() {
        return getCodroidExternalDir(ADDONS_DIR);
    }

    /**
     * Return the specific directory of the addon.
     *
     * @param _package addon package.
     * @return addon directory.
     */
    public File getAddonRootDir(String _package) {
        return PathUtils.splice(getAddonsDir(), _package).toFile();
    }

    /**
     * Return Codroid's external directory,
     * which is Android/data/org.codroid.body/files/${subDir}
     *
     * @param subDir subfolder.
     * @return Directory
     */
    public File getCodroidExternalDir(String subDir) {
        return new File(rootFile, subDir);
    }

    /**
     * Return the directory where temporary files are stored.
     * All the things in this directory will be cleared during Codroid startup.
     *
     * @return Temporary directory.
     */
    public File getTempDir() {
        return getCodroidExternalDir(TEMP_DIR);
    }

    public File getPreferencesDir() {
        return getCodroidExternalDir(PREFERENCES_DIR);
    }

    public PreferenceProperty getCodroidPreference(CodroidPreferenceGroup group) {
        return this.codroidPreferences.get(group);
    }

    public Map<CodroidPreferenceGroup, PreferenceProperty> getCodroidPreferences() {
        return this.codroidPreferences;
    }

    public void registerCodroidPreference(String name, InputStream stream) {
        CodroidPreferenceGroup group = CodroidPreferenceGroup.fromFilename(name);
        if (group != null) {
            this.codroidPreferences.put(group, new PreferenceProperty("preference" + name.split("\\.")[0] + "-kv", getPreferencesDir().getPath(), stream));
        }
    }

    public Map<String, PreferenceProperty> getCustomPreferences() {
        return customPreferences;
    }

    protected abstract void registerPreference(String path);
}
