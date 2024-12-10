package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

public class AttributeNotFoundException extends AddonException {
    private String message;
    public AttributeNotFoundException(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
