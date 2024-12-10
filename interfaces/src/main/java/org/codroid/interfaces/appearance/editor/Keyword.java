package org.codroid.interfaces.appearance.editor;

import org.codroid.interfaces.appearance.parts.SemanticHighlightPart;

/**
 * This class defines a keyword semantic span.
 */
public class Keyword extends SemanticTextColorSpan {
    @Override
    public String type() {
        return SemanticHighlightPart.Attribute.KEYWORD;
    }
}