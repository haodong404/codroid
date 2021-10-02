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

package org.codroid.editor.log;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Writing2File extends WritingProcessor {

    public static String LOG_FILE_NAME = "codroid.log";

    private String filePath;
    private File logFile;
    private FileOutputStream outputStream;

    public Writing2File(String filePath) throws IOException {
        this.filePath = filePath;
        initLogFile();
    }

    public Writing2File(Context context) throws IOException {
        filePath = context.getExternalFilesDir("logs").getPath();
        initLogFile();
    }

    private void initLogFile() throws IOException {
        logFile = new File(filePath + File.separator + LOG_FILE_NAME);
        outputStream = new FileOutputStream(logFile, true);
        if (!logFile.exists()) {
            if (!logFile.createNewFile()) {
                throw new IOException("Log file created Failed!");
            }
        }
    }


    @Override
    protected synchronized void process() {
        LogStructure structure = obtain(); // Sava a copy to avoid duplicate fetching.
        try {
            if (structure.getRawBytes() != null) {
                outputStream.write(structure.getRawBytes());
            } else {
                outputStream.write(structure.toStandardOutput());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
