package org.codroid.interfaces.appearance;

import org.codroid.interfaces.appearance.parts.EditorPart;
import org.codroid.interfaces.appearance.parts.SemanticHighlightPart;
import org.codroid.interfaces.env.AddonEnv;
import org.codroid.interfaces.env.Property;
import org.codroid.interfaces.exceptions.AttributeNotFoundException;

import java.util.Map;

public class AppearanceProperty extends Property<Object> {

    public AppearanceProperty(AddonEnv addonEnv, String relativePathStr) {
        super(addonEnv, relativePathStr, Object.class);
    }

    public enum PartEnum {
        EDITOR("editor"),

        SEMANTIC_HIGHLIGHT("semantic_highlight");

        private String str;

        PartEnum(String str) {
            this.str = str;
        }

        public String value() {
            return this.str;
        }
    }

    public Part part(PartEnum partEnum) throws IllegalArgumentException, AttributeNotFoundException {
        if (!getAttributes().containsKey(partEnum.value())) {
            throw new AttributeNotFoundException("Part: " + partEnum.value() + " not found!");
        } else if (!(getAttributes().get(partEnum.value()) instanceof Map<?, ?>)) {
            throw new AttributeNotFoundException(String.format("%s is not a [part].", partEnum.value()));
        }
        var part = (Map<?, ?>) getAttributes().get(partEnum.value());
        switch (partEnum) {
            case EDITOR:
                return new EditorPart(part);
            case SEMANTIC_HIGHLIGHT:
                return new SemanticHighlightPart(part);
            default:
                throw new AttributeNotFoundException("Part: " + partEnum.value() + " not found!");
        }
    }

    private Map<String, Object> getAttributes() {
        return (Map<String, Object>) getEntity();
    }

    public static class Appearances {

    }
}
