package org.codroid.interfaces.preference;

import androidx.annotation.Nullable;

public enum CodroidPreferenceGroup {
    TEXT_EDITOR("text-editor.toml"),
    DEVELOPER("developer.toml");

    private String filename;

    CodroidPreferenceGroup(String filename) {
        this.filename = filename;
    }

    public static CodroidPreferenceGroup fromFilename(String filename) {
        for (CodroidPreferenceGroup item : values()) {
            if (item.getFilename().equals(filename)) {
                return item;
            }
        }
        return null;
    }

    public String getFilename() {
        return this.filename;
    }
}
