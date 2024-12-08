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

package org.codroid.interfaces.log;

import org.codroid.interfaces.addon.AddonManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This file is responsible for writing inputs to different outputs.
 */
public class LogStream {

    private int mCorePoolSize = 2;
    private int mMaximumPoolSize = 3;
    private long mKeepAliveTime = 1;

    private ExecutorService mExecutorService;

    private List<WritingProcessor> outputs;

    public LogStream(File dir) {
        if (mExecutorService == null) {
            initialize();
            mExecutorService = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        }
        outputs = new ArrayList<>(3);
        outputs.add(new Writing2SystemOut());
        try {
            outputs.add(new Writing2File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the thread pool.
     * Compute each arguments roughly base on cpu and addons that imported.
     */
    private void initialize() {
        int cpuProcessor = Runtime.getRuntime().availableProcessors();
        int addonCount = AddonManager.get().addonLoadedCount();
        float factor = 0.8f;
        if (cpuProcessor < 4) {
            factor = 1.2f;
        }
        mCorePoolSize = cpuProcessor + (int) Math.ceil(addonCount * factor);
        mMaximumPoolSize = mCorePoolSize + (int) Math.ceil(addonCount * factor);
        mKeepAliveTime = (long) (3 * factor);
    }

    /**
     * Write a log structure to different outputs.
     *
     * @param structure input
     */
    private void write(LogStructure structure) {
        for (var i : outputs) {
            try {
                mExecutorService.submit(i.put(structure));
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write bytes to different outputs directly,
     * without formatting.
     *
     * @param bytes input bytes;
     */
    public void writeDirectly(byte[] bytes) {
        write(new LogStructure.Builder().setRawBytes(bytes).build());
    }

    /**
     * Writing with formatting.
     *
     * @param level   log level, from LogStream.
     * @param origin  where the log is from.
     * @param content input bytes.
     */
    public void writeFormat(int level, String origin, String content) {
        LogStructure structure = new LogStructure.Builder()
                .setLevel(level)
                .setContent(content)
                .setCalendar(Calendar.getInstance())
                .setOrigin(origin)
                .build();
        write(structure);
    }
}
