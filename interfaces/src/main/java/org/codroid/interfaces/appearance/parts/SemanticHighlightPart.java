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

package org.codroid.interfaces.appearance.parts;

import org.codroid.interfaces.appearance.AppearanceProperty;
import org.codroid.interfaces.appearance.Part;

import me.grison.jtoml.impl.Toml;

public class SemanticHighlightPart extends Part {

    public interface Attribute {
        String KEYWORD = "keyword";
        String OPERATOR = "operator";
    }

    /**
     * Construct it with a instance of Toml.
     *
     * @param toml instance.
     * @throws IllegalArgumentException if toml is null.
     */
    public SemanticHighlightPart(Toml toml) throws IllegalArgumentException {
        super(toml);
    }

    @Override
    public AppearanceProperty.PartEnum part() {
        return AppearanceProperty.PartEnum.SEMANTIC_HIGHLIGHT;
    }
}
