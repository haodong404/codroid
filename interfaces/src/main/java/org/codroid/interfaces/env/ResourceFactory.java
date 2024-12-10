package org.codroid.interfaces.env;

import org.codroid.interfaces.appearance.AppearanceProperty;

public class ResourceFactory {

    public static final int RAW_RESOURCE = 0;
    public static final int RAW_PROPERTY = 1;
    public static final int APPEARANCE_PROPERTY = 2;
    public static final int IMAGE_RESOURCE = 3;
    public static final int PREFERENCES_PROPERTY = 4;

    public Resource createResource(AddonEnv addonEnv, String path, int type) {
        switch (type) {
            case RAW_RESOURCE:
                return new ResourceRaw(addonEnv, path);
            case RAW_PROPERTY:
                return new Property<>(addonEnv, path, Object.class);
            case IMAGE_RESOURCE:
                return new ImageResource(addonEnv, path);
            default:
                return null;
        }
    }
}
