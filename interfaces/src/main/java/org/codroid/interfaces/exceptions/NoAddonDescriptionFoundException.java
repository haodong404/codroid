package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

/**
 * Thrown to indicate no addon's description file found.
 */
public class NoAddonDescriptionFoundException extends AddonException {
    public String name;

    public NoAddonDescriptionFoundException(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public String getMessage() {
        return name + " : No description found";
    }
}
