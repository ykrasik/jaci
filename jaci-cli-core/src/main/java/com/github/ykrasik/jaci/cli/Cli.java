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

package com.github.ykrasik.jaci.cli;

import com.github.ykrasik.jaci.cli.commandline.CommandLineManager;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.Objects;

/**
 * A CLI is a component that can read from a command line via a {@link CommandLineManager},
 * pass the command line to a {@link CliShell} to be processed, and according to the return value of the shell
 * manipulate the command line as required and write the new command line to the CommandLineManager.
 *
 * @author Yevgeny Krasik
 */
public class Cli {
    private final CliShell shell;
    private final CommandLineManager commandLineManager;

    public Cli(CliShell shell, CommandLineManager commandLineManager) {
        this.shell = Objects.requireNonNull(shell, "shell");
        this.commandLineManager = Objects.requireNonNull(commandLineManager, "commandLineManager");
    }

    /**
     * Provide assistance for the command line.
     *
     * @return {@code true} if assistance could be provided for the command line.
     */
    public boolean assist() {
        // Read the command line and provide assistance for the command line before the caret.
        final String commandLine = commandLineManager.getCommandLine();
        final int caret = commandLineManager.getCaret();
        final String commandLineToAssist = commandLine.substring(0, caret);
        final Opt<String> autoCompletedSuffix = shell.assist(commandLineToAssist);
        if (!autoCompletedSuffix.isPresent()) {
            return false;
        }

        // Don't overwrite any extra characters on the command line that came after the caret.
        final String extraCommandLine = commandLine.substring(caret);
        final String autoCompletedCommandLine = commandLineToAssist + autoCompletedSuffix.get();
        commandLineManager.setCommandLine(autoCompletedCommandLine + extraCommandLine);
        commandLineManager.setCaret(autoCompletedCommandLine.length());
        return true;
    }

    /**
     * Execute the command line.
     *
     * @return {@code true} if the command line could be executed successfully.
     */
    public boolean execute() {
        final String commandLine = commandLineManager.getCommandLine();
        clearCommandLine();
        return shell.execute(commandLine);
    }

    /**
     * Clear the command line.
     */
    public void clearCommandLine() {
        commandLineManager.setCommandLine("");
        commandLineManager.setCaret(0);
    }

    /**
     * Set the command line to the previous one from history.
     *
     * @return {@code true} if there was a previous command line in history.
     */
    public boolean setPrevCommandLineFromHistory() {
        return setCommandLineIfPresent(shell.getPrevCommandLineFromHistory());
    }

    /**
     * Set the command line to the next one from history.
     *
     * @return {@code true} if there was a next command line in history.
     */
    public boolean setNextCommandLineFromHistory() {
        return setCommandLineIfPresent(shell.getNextCommandLineFromHistory());
    }

    private boolean setCommandLineIfPresent(Opt<String> commandLine) {
        if (commandLine.isPresent()) {
            commandLineManager.setCommandLine(commandLine.get());
            commandLineManager.setCaret(commandLine.get().length());
        }
        return commandLine.isPresent();
    }
}
