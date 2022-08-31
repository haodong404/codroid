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

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class LogStructure {

    private int level;
    private String content;
    private Calendar calendar;
    private String origin;
    private byte[] rawBytes;

    public LogStructure(Builder builder) {
        this.level = builder.level;
        this.content = builder.content;
        this.calendar = builder.calendar;
        this.origin = builder.origin;
        this.rawBytes = builder.rawBytes;
    }

    public int getLevel() {
        return level;
    }

    public String getContent() {
        return content;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getOrigin() {
        return origin;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public byte[] toStandardOutput() {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        return ("[" +
                simpleDateFormat.format(calendar.getTime()) +
                "] " +
                origin +
                " " +
                levelConvert(level) +
                " : " +
                content +
                "\n").getBytes(StandardCharsets.UTF_8);
    }

    public String levelConvert(int level) {
        switch (level) {
            case 1:
                return "I";
            case 2:
                return "W";
            case 3:
                return "E";
            default:
                return "N";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "LogStructure{" +
                "level=" + level +
                ", content='" + content + '\'' +
                ", calendar=" + calendar +
                ", origin='" + origin + '\'' +
                ", rawBytes=" + Arrays.toString(rawBytes) +
                '}';
    }

    public static class Builder {

        private int level;
        private String content;
        private Calendar calendar;
        private String origin;
        private byte[] rawBytes;

        public int getLevel() {
            return level;
        }

        public String getContent() {
            return content;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public String getOrigin() {
            return origin;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setCalendar(Calendar calendar) {
            this.calendar = calendar;
            return this;
        }

        public Builder setOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder setRawBytes(byte[] rawBytes) {
            this.rawBytes = rawBytes;
            return this;
        }

        public LogStructure build() {
            return new LogStructure(this);
        }

    }
}
