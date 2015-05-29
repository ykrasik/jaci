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

package com.github.ykrasik.jemi.cli.commandline;

import com.github.ykrasik.jemi.util.opt.Opt;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages a history of command lines.<br>
 * Maintains state, so 2 consecutive calls to {@link #getPrevCommandLine()} will return different results
 * from the history buffer.<br>
 * The history buffer is limited and the oldest entries will be discarded when the buffer is full.
 *
 * @author Yevgeny Krasik
 */
public class CommandLineHistory {
    private final int maxHistory;
    private final List<String> history = new LinkedList<>();

    private int currentIndex;

    public CommandLineHistory(int maxHistory) {
        this.maxHistory = maxHistory;
        resetCurrentIndex();
    }

    /**
     * @return Previous command line from history.
     */
    public Opt<String> getPrevCommandLine() {
        if (history.isEmpty()) {
            return Opt.absent();
        }

        // Don't let currentIndex go below 0.
        if (currentIndex > 0) {
            currentIndex--;
        }

        return getElementAtCurrentIndex();
    }

    /**
     * @return Next command line in history.
     */
    public Opt<String> getNextCommandLine() {
        if (history.isEmpty()) {
            return Opt.absent();
        }

        // Don't let currentIndex go above the length of 'history'.
        final int size = history.size();
        currentIndex = currentIndex < size - 1 ? currentIndex + 1 : size - 1;

        return getElementAtCurrentIndex();
    }

    private Opt<String> getElementAtCurrentIndex() {
        return Opt.of(history.get(currentIndex));
    }

    /**
     * Adds a new command line to the history buffer. Resets the current history index.
     * @param commandLine Command line to add to history.
     */
    public void pushCommandLine(String commandLine) {
        // Add new history entry to the end.
        history.add(commandLine);

        // Maintain max history size.
        if (history.size() > maxHistory) {
            history.remove(0);
        }

        // Reset the iteration index.
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        // Set 'currentIndex' to point after the end of the 'history',
        // so the next access to the next or prev commandLine returns the last element of 'history'.
        currentIndex = history.size();
    }
}
