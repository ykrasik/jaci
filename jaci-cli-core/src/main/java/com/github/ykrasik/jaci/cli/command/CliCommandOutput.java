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

package com.github.ykrasik.jaci.cli.command;

import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.cli.directory.CliDirectory;
import com.github.ykrasik.jaci.cli.gui.CliGui;
import com.github.ykrasik.jaci.cli.output.CliPrinter;

import java.util.Objects;

/**
 * A CLI implementation of a {@link CommandOutput}.
 * Extends the API with some CLI-specific calls.
 *
 * @author Yevgeny Krasik
 */
public class CliCommandOutput implements CommandOutput {
    private final CliGui gui;
    private final CliPrinter out;
    private final CliPrinter err;

    private boolean printDefaultExecutionMessage = true;

    /**
     * @param gui GUI controller.
     * @param out stdOut.
     * @param err stdErr.
     */
    public CliCommandOutput(CliGui gui, CliPrinter out, CliPrinter err) {
        this.gui = Objects.requireNonNull(gui, "gui");
        this.out = Objects.requireNonNull(out, "out");
        this.err = Objects.requireNonNull(err, "err");
    }

    @Override
    public void message(String text) {
        out.println(text);
        suppressDefaultExecutionMessage();
    }

    @Override
    public void error(String text) {
        err.println(text);
        suppressDefaultExecutionMessage();
    }

    /**
     * If {@code true}, a default 'command executed successfully' message will be printed after the command is executed.
     *
     * @return Whether the default 'command executed successfully' message should be printed after this command.
     */
    public boolean isPrintDefaultExecutionMessage() {
        return printDefaultExecutionMessage;
    }

    /**
     * Set the working directory. Only called when the working directory changes.
     *
     * @param directory Directory to set as working directory.
     */
    public void setWorkingDirectory(CliDirectory directory) {
        gui.setWorkingDirectory(directory);
        suppressDefaultExecutionMessage();
    }

    /**
     * Print a directory and it's content.
     *
     * @param directory Directory to print.
     * @param recursive Whether to recurse into sub-directories.
     */
    public void printDirectory(CliDirectory directory, boolean recursive) {
        out.printDirectory(directory, recursive);
        suppressDefaultExecutionMessage();
    }

    /**
     * Print a command (it's name, description and parameters).
     *
     * @param command Command to describe.
     */
    public void printCommand(CliCommand command) {
        out.printCommand(command);
        suppressDefaultExecutionMessage();
    }

    private void suppressDefaultExecutionMessage() {
        // Called any time there is any interaction with this output.
        // The default message should only be printed if the command didn't print anything by itself.
        printDefaultExecutionMessage = false;
    }
}
