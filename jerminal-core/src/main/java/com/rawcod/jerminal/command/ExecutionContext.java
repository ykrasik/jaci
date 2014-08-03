package com.rawcod.jerminal.command;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:15
 */
public class ExecutionContext {
    private final List<String> outputBuffer;

    public ExecutionContext(List<String> outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    public void println(String message) {
        outputBuffer.add(message);
    }

    public void println(String format, Object... args) {
        println(String.format(format, args));
    }
}
