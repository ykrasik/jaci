/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli;

import com.github.ykrasik.jemi.cli.commandline.CommandLineHistory;
import com.github.ykrasik.jemi.cli.input.CliInput;
import com.github.ykrasik.jemi.util.opt.Opt;

import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class CliConsole {
    private final CliShell shell;
    private final CliInput input;
    private final CommandLineHistory history;

    public ConsoleImpl(CliShell shell, CliInput input) {
        this(shell, input, 30);
    }

    public ConsoleImpl(CliShell shell, CliInput input, int maxCommandHistory) {
        this.shell = Objects.requireNonNull(shell);
        this.input = Objects.requireNonNull(input);
        this.history = new CommandLineHistory(maxCommandHistory);
    }

    /**
     * Clear the command line.
     */
    public void clearCommandLine() {
        input.clear();
    }

    /**
     * Set the command line to the previous one from history.
     *
     * @return true if there was a previous command line in history.
     */
    public boolean setPrevCommandLineFromHistory() {
        final Opt<String> commandLine = history.getPrevCommandLine();
        return setCommandLineIfPresent(commandLine);
    }

    /**
     * Set the command line to the next one from history.
     *
     * @return true if there was a next command line in history.
     */
    public boolean setNextCommandLineFromHistory() {
        final Opt<String> commandLine = history.getNextCommandLine();
        return setCommandLineIfPresent(commandLine);
    }

    /**
     * Provide assistance for the command line.
     *
     * @return true if assistance could be provided for the command line.
     */
    public boolean assist() {
        final String commandLine = input.readUntilCaret();
        final Opt<String> newCommandLine = shell.assist(commandLine);
        return setCommandLineIfPresent(newCommandLine);
    }

    private boolean setCommandLineIfPresent(Opt<String> commandLine) {
        if (commandLine.isPresent()) {
            input.set(commandLine.get());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Execute the command line.
     *
     * @return true if the command line could be executed successfully.
     */
    public boolean execute() {
        final String commandLine = input.read();
        input.clear();

        // Save command in history, if it isn't empty.
        if (!commandLine.trim().isEmpty()) {
            history.pushCommandLine(commandLine);
        }

        // Execute.
        return shell.execute(commandLine);
    }
}
