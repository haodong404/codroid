package org.codroid.interfaces.log;

import android.util.Log;

import java.nio.charset.StandardCharsets;

public class Writing2SystemOut extends WritingProcessor {

    @Override
    protected void process(){
        LogStructure logStructure = obtain(); // Sava a copy to avoid duplicate fetching.
        if (logStructure.getRawBytes() == null) {
            Log.println(Log.INFO, logStructure.getOrigin(), logStructure.getContent());
        } else {
            Log.println(Log.INFO, "RAW", new String(logStructure.getRawBytes(), StandardCharsets.UTF_8));
        }
    }
}
