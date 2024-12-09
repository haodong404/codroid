package org.codroid.interfaces.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static Path splice(String first, String... more) {
        StringBuilder builder = new StringBuilder(first);
        if (!first.endsWith(File.separator)) {
            builder.append(File.separator);
        }

        for (int i = 0; i < more.length; i++) {
            if (i == 0){
                if (more[i].startsWith(File.separator)) {
                    builder.append(more[i].substring(1));
                } else {
                    builder.append(more[i]);
                }
            } else {
                builder.append(more[i]);
            }
        }
        return Paths.get(builder.toString());
    }

    public static Path splice(File file, String... more) {
        return splice(file.getPath(), more);
    }

    public static Path splice(File file, File... more) {
        String[] temp = new String[more.length];
        for (int i = 0 ;i < more.length; i ++){
            temp[i] = more[i].getPath();
        }
        return splice(file, temp);
    }
}
