package org.codroid.interfaces.log;

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
