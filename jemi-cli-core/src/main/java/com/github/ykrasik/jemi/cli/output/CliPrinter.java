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

package com.github.ykrasik.jemi.cli.output;

import com.github.ykrasik.jemi.cli.assist.CommandInfo;
import com.github.ykrasik.jemi.cli.assist.Suggestions;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import lombok.NonNull;

/**
 * A component that serializes CLI entities into {@link String}s and prints them to a {@link CliOutput}.
 *
 * @author Yevgeny Krasik
 */
public class CliPrinter {
    private final CliOutput output;
    private final CliSerializer serializer;

    public CliPrinter(@NonNull CliOutput output, @NonNull CliSerializer serializer) {
        this.output = output;
        this.serializer = serializer;
    }

    /**
     * Called before anything is printed, to allow the implementation to prepare itself.
     * Will not be called again before {@link #end()} is called.
     */
    public void begin() {
        output.begin();
    }

    /**
     * Called when all printing has finished.
     * {@link #begin()} will be called again before anything else is printed.
     */
    public void end() {
        output.end();
    }

    /**
     * Print a single line.
     *
     * @param text Text to print.
     */
    public void println(String text) {
        output.println(text);
    }

    /**
     * Print a single error line.
     *
     * @param text Text to print as an error.
     */
    public void errorPrintln(String text) {
        output.errorPrintln(text);
    }

    /**
     * Set the 'working directory' to the given directory.
     * This is a visual detail that simply displays what the current 'working directory' is.
     *
     * @param directory Working directory to set.
     */
    public void setWorkingDirectory(CliDirectory directory) {
        final String path = serializer.serializePathToDirectory(directory);
        output.setWorkingDirectory(path);
    }

    /**
     * Print the command line.
     *
     * @param commandLine Command line to print.
     */
    public void printCommandLine(String commandLine) {
        final String serializedCommandLine = serializer.serializeCommandLine(commandLine);
        output.println(serializedCommandLine);
    }

    /**
     * Print a directory and it's content.
     *
     * @param directory Directory to print.
     * @param recursive Whether to recurse into sub-directories.
     */
    public void printDirectory(CliDirectory directory, boolean recursive) {
        final Serialization serialization = serializer.serializeDirectory(directory, recursive);
        printSerialization(serialization);
    }

    /**
     * Print a command (it's name, description and parameters).
     *
     * @param command Command to describe.
     */
    public void printCommand(CliCommand command) {
        final Serialization serialization = serializer.serializeCommand(command);
        printSerialization(serialization);
    }

    /**
     * Print an exception.
     *
     * @param e Exception to print.
     */
    public void printException(Exception e) {
        final Serialization serialization = serializer.serializeException(e);
        for (String line : serialization) {
            errorPrintln(line);
        }
    }

    /**
     * Print information about a command. Called to display assistance information about a command, or if a parse
     * error occurred while parsing the command's parameters.
     *
     * @param info Command info to print.
     */
    public void printCommandInfo(CommandInfo info) {
        final Serialization serialization = serializer.serializeCommandInfo(info);
        printSerialization(serialization);
    }

    /**
     * Print suggestions.
     *
     * @param suggestions Suggestions to print.
     */
    public void printSuggestions(Suggestions suggestions) {
        final Serialization serialization = serializer.serializeSuggestions(suggestions);
        printSerialization(serialization);
    }

    private void printSerialization(Serialization serialization) {
        for (String line : serialization) {
            println(line);
        }
    }
}
