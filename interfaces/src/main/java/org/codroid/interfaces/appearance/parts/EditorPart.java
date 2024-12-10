package org.codroid.interfaces.appearance.parts;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;

import java.util.Map;

/**
 * Editor part of appearance.
 */
public class EditorPart extends Part {

   public interface Attribute {
       String BACKGROUND = "background";
   }

    public EditorPart(Map<?, ?> attr) throws IllegalArgumentException {
        super(attr);
    }

    @Override
    public AppearanceProperty.PartEnum part() {
        return AppearanceProperty.PartEnum.EDITOR;
    }
}
