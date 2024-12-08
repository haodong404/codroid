package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

public class PreferenceNotFoundException extends AddonException {

    private String message;

    public PreferenceNotFoundException(String preference) {
        this.message = "Preference " + preference + " not found.";
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

}
