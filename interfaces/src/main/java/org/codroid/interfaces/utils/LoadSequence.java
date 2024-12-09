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
