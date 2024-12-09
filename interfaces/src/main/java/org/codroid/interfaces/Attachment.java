package org.codroid.interfaces;

import org.codroid.interfaces.env.AddonEnv;

/**
 * It allows a class to attach an addon.
 * And then the subclass can access resources from addon environment.
 */
public interface Attachment {

    /**
     * Invoked before the subclass is loaded by Codroid.
     *
     * @param addon which addon to attach to.
     */
    void attached(AddonEnv addon);

}
