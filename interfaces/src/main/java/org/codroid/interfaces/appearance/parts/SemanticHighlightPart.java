package org.codroid.interfaces.appearance.parts;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;

import java.util.Map;

public class SemanticHighlightPart extends Part {

    public interface Attribute {

        String KEYWORD = "keyword";
        String OPERATOR = "operator";
    }

    public SemanticHighlightPart(Map<?, ?> attr) throws IllegalArgumentException {
        super(attr);
    }

    @Override
    public AppearanceProperty.PartEnum part() {
        return AppearanceProperty.PartEnum.SEMANTIC_HIGHLIGHT;
    }
}
