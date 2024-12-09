package org.codroid.interfaces.appearance.editor;

import android.graphics.Color;

import org.codroid.interfaces.addon.AddonManager;
import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;
import org.codroid.interfaces.appearance.Semantic;

import java.util.Optional;

public abstract class SemanticTextColorSpan extends TextColorSpan implements Semantic {

    private Optional<Part> part;

    public SemanticTextColorSpan() {
        super();
        part = AddonManager.get().appearancePart(AppearanceProperty.PartEnum.SEMANTIC_HIGHLIGHT);
    }

    @Override
    protected Color getColor() {
        if (part.isPresent()) {
            if (part.get().getColor(type()).isPresent()) {
                return part.get().getColor(type()).get();
            }
        }
        return Color.valueOf(Color.RED);
    }

    public abstract String type();
}
