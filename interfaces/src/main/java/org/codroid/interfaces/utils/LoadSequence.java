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

package org.codroid.interfaces.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class LoadSequence<T> {

    private List<String> seq;

    private Map<String, T> elements;

    public LoadSequence(Map<String, T> elements) {
        this.elements = elements;
        this.seq = new ArrayList<>(elements.keySet());
    }

    public LoadSequence(Map<String, T> elements, List<String> order) {
        this.seq = order;
        this.elements = elements;
    }

    public Collection<T> raw() {
        return elements.values();
    }

    public Collection<T> ordered() throws NoSuchElementException {
        List<T> order = new ArrayList<>(elements.size());
        seq.forEach(it -> {
            T t = elements.get(it);
            if(t == null) {
                throw new NoSuchElementException("The sequence from datastore: " + seq.toString());
            }
            order.add(t);
        });
        return order;
    }
}
