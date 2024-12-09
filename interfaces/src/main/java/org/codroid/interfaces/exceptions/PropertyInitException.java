package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

/**
 * Thrown to indicate the property file is broken.
 */
public class PropertyInitException extends AddonException {
    private String message;

    public PropertyInitException(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
