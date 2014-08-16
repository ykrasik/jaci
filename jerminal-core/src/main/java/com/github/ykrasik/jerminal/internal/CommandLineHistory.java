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

package com.github.ykrasik.jerminal.internal;

import com.google.common.base.Optional;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages a history of command lines.
 *
 * @author Yevgeny Krasik
 */
public class CommandLineHistory {
    // FIXME: This class needs testing.
    // FIXME: Wouldn't it be easier to just keep an int index?
    private final Deque<String> prevCommandLines;
    private final Deque<String> nextCommandLines;

    private final int maxCommandLineHistory;

    public CommandLineHistory(int maxCommandLineHistory) {
        this.maxCommandLineHistory = maxCommandLineHistory;
        this.prevCommandLines = new ArrayDeque<>(maxCommandLineHistory);
        this.nextCommandLines = new ArrayDeque<>(maxCommandLineHistory);
    }

    public Optional<String> getPrevCommandLine() {
        if (prevCommandLines.isEmpty()) {
            return Optional.absent();
        }
        if (prevCommandLines.size() == 1) {
            return Optional.of(prevCommandLines.peek());
        }

        final String prevCommand = prevCommandLines.pollLast();
        nextCommandLines.addFirst(prevCommand);
        return Optional.of(prevCommand);
    }

    public Optional<String> getNextCommandLine() {
        if (nextCommandLines.isEmpty()) {
            return Optional.absent();
        }
        if (nextCommandLines.size() == 1){
            return Optional.of(nextCommandLines.peek());
        }

        final String nextCommand = nextCommandLines.pollFirst();
        prevCommandLines.addLast(nextCommand);
        return Optional.of(nextCommand);
    }

    public void pushCommandLine(String commandLine) {
        resetCommandLines();
        if (prevCommandLines.size() >= maxCommandLineHistory) {
            prevCommandLines.removeFirst();
        }
        prevCommandLines.addLast(commandLine);
    }

    private void resetCommandLines() {
        // Move all commandsLines currently in nextCommandLines to prevCommandLines.
        // This is done before a commandLine is pushed.
        prevCommandLines.addAll(nextCommandLines);
        nextCommandLines.clear();
    }
}
