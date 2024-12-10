package org.codroid.interfaces.exceptions;

/**
 * Thrown to indicate a event class loaded failed
 */
public class EventClassLoadException extends AddonClassLoadException {

    public EventClassLoadException(String cause) {
        super(cause);
    }
}
