/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.api.command.OutputBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation for a {@link OutputBuffer}.
 *
 * @author Yevgeny Krasik
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
