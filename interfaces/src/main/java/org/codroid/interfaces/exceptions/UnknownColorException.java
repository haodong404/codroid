package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

public class UnknownColorException extends AddonException {
    private String message;

    public UnknownColorException(String message){
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
