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

package com.github.ykrasik.jerminal.api;

import com.github.ykrasik.jerminal.internal.CommandLineHistory;
import com.google.common.base.Optional;

import java.util.Objects;

/**
 * A convenience wrapper for a {@link Shell} that integrates it with a {@link CommandLineDriver} to
 * be able to perform common command line logic.
 *
 * @author Yevgeny Krasik
 */
public class ConsoleImpl implements Console {
    private final Shell shell;
    private final CommandLineDriver commandLineDriver;
    private final CommandLineHistory history;

    public ConsoleImpl(Shell shell, CommandLineDriver commandLineDriver) {
        this(shell, commandLineDriver, 30);
    }

    public ConsoleImpl(Shell shell, CommandLineDriver commandLineDriver, int maxCommandHistory) {
        this.shell = Objects.requireNonNull(shell);
        this.commandLineDriver = Objects.requireNonNull(commandLineDriver);
        this.history = new CommandLineHistory(maxCommandHistory);
    }

    @Override
    public void clearCommandLine() {
        commandLineDriver.clear();
    }

    @Override
    public boolean setPrevCommandLineFromHistory() {
        final Optional<String> commandLine = history.getPrevCommandLine();
        return setCommandLineIfPresent(commandLine);
    }

    @Override
    public boolean setNextCommandLineFromHistory() {
        final Optional<String> commandLine = history.getNextCommandLine();
        return setCommandLineIfPresent(commandLine);
    }

    @Override
    public boolean assist() {
        final String commandLine = commandLineDriver.readUntilCaret();
        final Optional<String> newCommandLine = shell.assist(commandLine);
        return setCommandLineIfPresent(newCommandLine);
    }

    private boolean setCommandLineIfPresent(Optional<String> commandLine) {
        if (commandLine.isPresent()) {
            commandLineDriver.set(commandLine.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean execute() {
        final String commandLine = commandLineDriver.read();
        commandLineDriver.clear();

        // Save command in history, if it isn't empty.
        if (!commandLine.trim().isEmpty()) {
            history.pushCommandLine(commandLine);
        }

        // Execute.
        return shell.execute(commandLine);
    }
}
