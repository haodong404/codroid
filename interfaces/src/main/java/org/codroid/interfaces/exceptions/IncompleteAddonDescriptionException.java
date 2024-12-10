package org.codroid.interfaces.exceptions;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * This exception raised when a description lack of necessary fields.
 */
public class IncompleteAddonDescriptionException extends AddonException {

    private Set<String> brokenFields;

    public IncompleteAddonDescriptionException(Set<String> brokenFields) {
        this.brokenFields = brokenFields;
        if (brokenFields == null) this.brokenFields = Collections.emptySet();
    }

    public Set<String> brokenFields(){
        return brokenFields;
    }

    @Nullable
    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (var i : brokenFields) {
            builder.append(i);
            builder.append(" ");
        }
        return builder.toString();
    }
}
