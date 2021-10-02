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

import org.codroid.editor.addon.AddonManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogStream {

    private int mCorePoolSize = 2;
    private int mMaximumPoolSize = 3;
    private long mKeepAliveTime = 1;

    private ExecutorService mExecutorService;

    private List<WriteProcessor> outputs;

    public LogStream(Context context) {
        if (mExecutorService == null) {
            initialize(context);
            mExecutorService = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(mMaximumPoolSize));
        }
        outputs = new ArrayList<>(3);
        outputs.add(new Write2SystemOut());
        try {
            outputs.add(new Write2File(context));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the thread pool
     * Compute each arguments roughly base on cpu and addons that imported.
     * @param context app context
     */
    private void initialize(Context context) {
        int cpuProcessor = Runtime.getRuntime().availableProcessors();
        int addonCount = AddonManager.get().getAddonCountImported(context);
        float factor = 0.8f;
        if (cpuProcessor < 4) {
            factor = 1.2f;
        }
        mCorePoolSize = cpuProcessor + (int) Math.ceil(addonCount * factor);
        mMaximumPoolSize = mCorePoolSize + (int) Math.ceil(addonCount * factor);
        mKeepAliveTime = (long) (3 * factor);
    }

    /**
     * Write bytes to different outputs.
     * @param bytes input
     */
    public void write(byte[] bytes) {
        for (var i : outputs) {
            mExecutorService.submit(i.put(bytes));
        }
    }
}
