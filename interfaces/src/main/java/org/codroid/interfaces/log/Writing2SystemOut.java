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
