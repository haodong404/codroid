package org.codroid.interfaces.appearance;

import android.graphics.Color;

import org.codroid.interfaces.env.AddonEnv;
import org.codroid.interfaces.env.Property;
import org.codroid.interfaces.exceptions.UnknownColorException;

public class AppearanceProperty extends Property {

    public AppearanceProperty(AddonEnv addonEnv, String relativePathStr) {
        super(addonEnv, relativePathStr);
    }

    public enum Attribute {

        EDITOR_BACKGROUND("editor_background");

        private String str;

        Attribute(String str) {
            this.str = str;
        }
    }

    public Color getColor(Attribute attribute) throws UnknownColorException {
        String value = get(attribute);
        if (!value.startsWith("#")) {
            throw new UnknownColorException("Color string must start with # (number sign)! ");
        }
        try {
            return Color.valueOf(Color.parseColor(value));
        } catch (Exception e) {
            throw new UnknownColorException("Unknown color string: " + value);
        }
    }

    public String get(Attribute attribute) {
        if (toml == null) {
            open();
        }
        return toml.getString(attribute.str);
    }
}
