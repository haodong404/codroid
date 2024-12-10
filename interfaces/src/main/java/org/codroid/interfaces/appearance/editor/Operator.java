package org.codroid.interfaces.appearance.editor;

import org.codroid.interfaces.appearance.parts.SemanticHighlightPart;

/**
 * This class defines a operator semantic span.
 */
public class Operator extends SemanticTextColorSpan {

    @Override
    public String type() {
        return SemanticHighlightPart.Attribute.OPERATOR;
    }
}
