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

public final class Logger {

    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_ERROR = 3;

    private static LogStream logStream;
    private String origin;

    public Logger(Context context, String origin) {
        if (logStream == null) {
            logStream = new LogStream(context);
        }
        this.origin = origin;
    }

    public void i(String content) {
        log(LEVEL_INFO, origin, content);
    }

    public void i(double content) {
        i(String.valueOf(content));
    }

    public void i(float content) {
       i(String.valueOf(content));
    }

    public void i(int content) {
        i(String.valueOf(content));
    }

    public void e() {

    }

    public void log(int level, String origin, String content) {
        logStream.writeFormat(level, origin, content);
    }
}
