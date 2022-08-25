package org.codroid.interfaces.preference;

import com.tencent.mmkv.MMKV;

import org.codroid.interfaces.env.AddonEnv;
import org.codroid.interfaces.env.Property;
import org.codroid.interfaces.utils.PathUtils;
import org.codroid.interfaces.utils.TomlKt;

import java.nio.file.Path;
import java.util.function.BiConsumer;

import cc.ekblad.toml.TomlMapper;

public class PreferencesProperty extends Property {

    private final MMKV mmkv;
    private Preferences preferences;

    public PreferencesProperty(Path path) {
        super(path);
        this.mmkv = MMKV.mmkvWithID(path.getFileName().toString() + "-kv", path.toAbsolutePath().toString());
        initDefaultValues();
    }

    public PreferencesProperty(AddonEnv addonEnv, String relativePathStr) {
        super(addonEnv, relativePathStr);
        this.mmkv = MMKV.mmkvWithID(addonEnv.getIdentify() + "-kv", addonEnv.getAddonRootDir().getAbsolutePath());
        initDefaultValues();
    }

    private void initDefaultValues() {
        this.preferences = getAs(Preferences.class);
        this.preferences.getSettings().forEach((k, v) -> {
            if (v instanceof InputSetting) {
                if (((InputSetting) v).getValueType().equals("STRING")) {
                    putString(k, ((InputSetting) v).getValueType());
                } else {
                    putInt(k, Integer.parseInt(((InputSetting) v).getDefaultValue().toString()));
                }
            } else if (v instanceof TextareaSetting) {
                putString(k, ((TextareaSetting) v).getDefaultValue());
            } else if (v instanceof SwitchSetting) {
                putBoolean(k, ((SwitchSetting) v).getDefaultValue());
            } else if (v instanceof SelectSetting) {
                putInt(k, ((SelectSetting) v).getDefaultValue());
            }
        });
    }

    public String getString(String key) {
        return this.mmkv.decodeString(key);
    }

    public int getInt(String key) {
        return this.mmkv.decodeInt(key);
    }

    public boolean getBoolean(String key) {
        return this.mmkv.decodeBool(key);
    }

    public void putString(String key, String value) {
        this.mmkv.encode(key, value);
    }

    public void putInt(String key, int value) {
        this.mmkv.encode(key, value);
    }

    public void putBoolean(String key, boolean value) {
        this.mmkv.encode(key, value);
    }

    @Override
    public TomlMapper getTomlMapper() {
        return PreferencesKt.getPreferencesMapper();
    }
}
