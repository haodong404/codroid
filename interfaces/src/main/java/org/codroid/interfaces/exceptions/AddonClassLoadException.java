package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

/**
 * Thrown to indicate an addon's class loaded failed.
 */
public class AddonClassLoadException extends AddonException{

    private String cause;

    public AddonClassLoadException(String cause){
        this.cause = cause;
    }

    @Nullable
    @Override
    public String getMessage() {
        return cause;
    }
}
