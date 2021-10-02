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

import android.util.Log;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class WritingProcessor implements Runnable {

    // Using a buffer to store the inputs that will be logged in.
    private Queue<LogStructure> inputBuffer;

    public WritingProcessor(){
        inputBuffer = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        process();
    }

    public WritingProcessor put(LogStructure structure) {
        this.inputBuffer.offer(structure);
        return this;
    }

    public LogStructure obtain() {
        return inputBuffer.poll();
    }

    /**
     * This abstract method will be implemented by subclasses
     * They can log what or where they want.
     */
    protected abstract void process();
}
