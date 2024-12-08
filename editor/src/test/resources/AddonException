package org.codroid.interfaces.exceptions;


import org.codroid.interfaces.log.Logger;

/**
 * This is a superclass that should only be inherited by the exceptions about addon.
 */
public class AddonException extends Exception {

    /**
     * Print the stack trace by addon logger.
     *
     * @param logger which logger you want to use.
     */
    public void printStackTrace(Logger logger) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.toString());
        for (var i : getStackTrace()) {
            builder.append("\n\tat ");
            builder.append(i.toString());
        }
        logger.e(builder.toString());
    }
}
