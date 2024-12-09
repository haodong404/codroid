package org.codroid.interfaces.env;

import androidx.annotation.NonNull;

import org.codroid.interfaces.utils.PathUtils;

import java.io.File;
import java.nio.file.Path;

public abstract class Resource {
    private Path path;

    public Resource(@NonNull AddonEnv addonEnv, String path) {
        this.path = PathUtils.splice(addonEnv.getAddonRootDir(), path);
    }

    public Resource(Path path){
        this.path = path;
    }

    public Path toPath() {
        return this.path;
    }

    public File toFile(){
        return path.toFile();
    }
}