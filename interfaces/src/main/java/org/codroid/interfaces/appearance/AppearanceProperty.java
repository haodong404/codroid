package org.codroid.interfaces.appearance;

import android.graphics.Color;

import org.codroid.interfaces.Property;

import java.io.File;
import java.nio.file.Path;

public class AppearanceProperty extends Property {

    public AppearanceProperty(Path path) {
        super(path);
    }

    public enum Attribute {

        EDITOR_BACKGROUND("editor_background");

        private String str;

        Attribute(String str) {
            this.str = str;
        }
    }

    public Color getColor(Attribute attribute) {
        String value = get(attribute);
        if (!value.startsWith("#")) {
            return null;
        }
        int[] rgb = {0,0,0};
        for (int i = 1; i <= 6 ; i += 2) {
            rgb[(i + 1)/2] = Integer.decode("0x" + value.substring(i, i + 2));
        }
        return Color.valueOf(rgb[0], rgb[1], rgb[2]);
    }

    public String get(Attribute attribute) {
        return toml.getString(attribute.str);
    }
}
