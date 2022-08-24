package org.codroid.interfaces.preference;

import com.tencent.mmkv.MMKV;

import org.codroid.interfaces.env.AddonEnv;
import org.codroid.interfaces.env.Property;
import org.codroid.interfaces.utils.PathUtils;
import org.codroid.interfaces.utils.TomlKt;

import java.nio.file.Path;

public class PreferencesProperty extends Property {

    private final MMKV mmkv;
    private Preferences preferences;

    public PreferencesProperty(Path path) {
        super(path);
        this.preferences = getAs(Preferences.class);
        this.mmkv = MMKV.mmkvWithID(path.getFileName().toString() + "-kv", path.toAbsolutePath().toString());
    }

    public PreferencesProperty(AddonEnv addonEnv, String relativePathStr) {
        super(addonEnv, relativePathStr);
        this.preferences = getAs(Preferences.class);
        this.mmkv = MMKV.mmkvWithID(addonEnv.getIdentify() + "-kv", addonEnv.getAddonRootDir().getAbsolutePath());
    }

    public String getString(String key) {
        return this.mmkv.decodeString(key);
    }

    public int getInt(String key) {
        return this.mmkv.decodeInt(key);
    }

    public void putString(String key, String value) {
        this.mmkv.encode(key, value);
    }

    public void putInt(String key, int value) {
        this.mmkv.encode(key, value);
    }
}
