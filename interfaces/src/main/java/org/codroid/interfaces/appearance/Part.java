package org.codroid.interfaces.appearance;

import android.graphics.Color;

import org.codroid.interfaces.addon.AddonManager;

import java.util.Map;
import java.util.Optional;


/**
 * This class is a superclass for each appearance part in Codroid.
 * It contains the methods that obtain the attributes of color or others.
 */
public abstract class Part {

    private final Map<?, ?> mAttributes;

    public interface OnFound<T> {
        void found(T value);
    }


    public Part(Map<?, ?> attr) throws IllegalArgumentException {
        if (attr == null) {
            throw new IllegalArgumentException("Attributes cannot be null !");
        }
        this.mAttributes = attr;
    }

    /**
     * Get the color in this part.
     *
     * @param attribute what the color's attribute is.
     * @return an Optional instance.
     */
    public Optional<Color> getColor(String attribute) {
        String value = null;
        try {
            value = (String) mAttributes.get(attribute);
            return Optional.of(Color.valueOf(Color.parseColor(value)));
        } catch (Exception e) {
            AddonManager.get().getLogger().e("Unknown color: " + value);
        }
        return Optional.empty();
    }

    public void findColor(String attr, OnFound<Color> callback) {
        if (getColor(attr).isPresent()) {
            if (callback != null) {
                callback.found(getColor(attr).get());
            }
        }
    }

    /**
     * This method should be implemented by subclasses,
     * it can return what the appearance part the subclass is.
     *
     * @return appearance part.
     */
    public abstract AppearanceProperty.PartEnum part();
}
