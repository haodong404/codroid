package org.codroid.interfaces.log;

/**
 * If a class could be logged, it should implement this interface.
 */
public interface Loggable {

    /**
     * Each addon or class could have it's own logger,
     * which can identify itself.
     * @return the logger you created or assigned by others.
     */
    Logger getLogger();
}
