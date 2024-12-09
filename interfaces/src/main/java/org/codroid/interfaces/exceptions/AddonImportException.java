package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

public class AddonImportException extends AddonException {
    private String message;

    public AddonImportException (String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
