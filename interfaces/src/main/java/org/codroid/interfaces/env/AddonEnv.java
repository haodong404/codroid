package org.codroid.interfaces.env;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.exceptions.PreferenceNotFoundException;
import org.codroid.interfaces.log.Logger;
import org.codroid.interfaces.preference.PreferenceProperty;

import java.io.File;

/**
 * This class is the environment of each addon.
 * It contains various interfaces to access the information of an addon.
 */
public class AddonEnv extends CodroidEnv {

    private final static String PREFERENCE_FILE = "preferences.toml";

    // This field aims to identify an addon uniquely.
    // The addon package name is usually used.
    private String identify;

    private Logger logger;

    private ResourceFactory resourceFactory = new ResourceFactory();
    private PreferenceProperty preference = null;

    public AddonEnv() {

    }

    /**
     * You don't need to call this method for register.
     * Codroid will register a preference(preferences.toml) in your root path by default.
     *
     * @param relativePath relative path
     */
    @Override
    protected void registerPreference(String relativePath) {
        this.preference = (PreferenceProperty) getResourceByType(relativePath, ResourceFactory.PREFERENCES_PROPERTY);
    }

    public AddonEnv(String identify) {
        this.identify = identify;
    }

    public void createAddonEvn(CodroidEnv parent, String identify) {
        this.rootFile = parent.rootFile;
        this.identify = identify;
    }

    public String getIdentify() {
        return identify;
    }

    /**
     * Find an {@link AppearanceProperty} by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @return an {@link AppearanceProperty}.
     */
    public AppearanceProperty getAppearanceProperty(String relativePath) {
        return new AppearanceProperty(this, relativePath);
    }

    /**
     * Find a {@link Property} file by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @return a {@link Property}.
     */
    public Property getProperty(String relativePath) {
        return (Property) getResourceByType(relativePath, ResourceFactory.RAW_PROPERTY);
    }

    /**
     * Return the directory that belongs to the specific addon.
     *
     * @return the addon's root directory.
     */
    public File getAddonRootDir() {
        return getAddonRootDir(getIdentify());
    }

    /**
     * Find a {@link Resource} by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @return the {@link ResourceRaw}.
     */
    public ResourceRaw getResource(String relativePath) {
        return (ResourceRaw) getResourceByType(relativePath, ResourceFactory.RAW_RESOURCE);
    }

    /**
     * Find a {@link Resource} by using a relative path.
     *
     * @param relativePath Path relative to root.
     * @param type         The type is in the {@link ResourceFactory}
     * @return The {@link Resource}
     */
    private Resource getResourceByType(String relativePath, int type) {
        return resourceFactory.createResource(this, relativePath, type);
    }

    /**
     * Return the specific {@link Logger} of the addon.
     *
     * @return The {@link Logger}
     */
    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(getLogsDir());
        return logger.with(getIdentify());
    }

    public PreferenceProperty getPreference() throws PreferenceNotFoundException {
        if (this.preference == null) {
            try {
                this.preference = new PreferenceProperty(this, PREFERENCE_FILE);
                customPreferences.put(getIdentify(), preference);
            } catch (Exception e) {
                throw new PreferenceNotFoundException(getIdentify());
            }
        }
        return this.preference;
    }
}
