package org.codroid.interfaces.log;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Writing2File extends WritingProcessor {

    public static String LOG_FILE_NAME = "codroid.log";

    private String filePath;
    private File logFile;
    private FileOutputStream outputStream;

    public Writing2File(File filePath) throws IOException {
        this.filePath = filePath.toString();
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
        LogStructure structure = obtain();
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
