package org.codroid.interfaces.evnet;

import java.io.File;

public interface AddonImportEvent extends Event {

    /**
     * This function will be invoked before importing an external addon jar.
     *
     * IT IS DESIGNED FOR CONVERT ORIGINAL ADDONS.
     * NOT RECOMMENDED FOR USE !!!!
     *
     * @param externalJar external addon jar.
     * @return the file you processed.
     */
    File beforeImport(File externalJar);

}
