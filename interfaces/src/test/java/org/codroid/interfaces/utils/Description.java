package org.codroid.interfaces.utils;

import java.util.List;

public class Description {
    String name;
    long version;
    List<Long> list;
    Theme theme;

    public Description(String name, long version, List<Long> list, Theme theme) {
        this.name = name;
        this.version = version;
        this.list = list;
        this.theme = theme;
    }

    static class Theme {
        String name;
        long version;
    }
}

