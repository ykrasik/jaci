package com.rawcod.jerminal.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:15
 */
public class OutputBufferImpl implements OutputBuffer {
    private final List<String> outputBuffer;

    public OutputBufferImpl() {
        this.outputBuffer = new ArrayList<>();
    }

    public boolean isEmpty() {
        return outputBuffer.isEmpty();
    }

    public List<String> getOutputBuffer() {
        return Collections.unmodifiableList(outputBuffer);
    }

    @Override
    public void println(String message) {
        outputBuffer.add(message);
    }

    @Override
    public void println(String format, Object... args) {
        println(String.format(format, args));
    }
}
