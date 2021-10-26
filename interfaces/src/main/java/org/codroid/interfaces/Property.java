package org.codroid.interfaces;

import java.io.File;

import me.grison.jtoml.impl.Toml;

public class Property {

    private Toml toml;

    private File file;

    public Property(File file) {
        this.file = file;
    }

    public Property open() {
        try {
            toml = Toml.parse(file);
        } catch (Exception e) {

        }

        return this;
    }


    public void close() {
        toml = null;
    }
}
