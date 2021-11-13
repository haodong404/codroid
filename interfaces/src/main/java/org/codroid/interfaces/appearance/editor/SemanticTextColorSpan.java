/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.interfaces.appearance.editor;

import android.graphics.Color;

import org.codroid.interfaces.addon.AddonManager;
import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;
import org.codroid.interfaces.appearance.Semantic;

import java.util.Optional;

public abstract class SemanticTextColorSpan extends TextColorSpan implements Semantic {

    private Optional<Part> part;

    public SemanticTextColorSpan(){
        super();
        part = AddonManager.get().appearancePart(AppearanceProperty.PartEnum.SEMANTIC_HIGHLIGHT);
    }

    @Override
    protected Color getColor() {
        if (part.isPresent()){
            if (part.get().getColor(type()).isPresent()){
                return part.get().getColor(type()).get();
            }
        }
        return Color.valueOf(Color.RED);
    }

    public abstract String type();
}
