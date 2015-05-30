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
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor
public class CliPrinter {
    @NonNull private final CliOutput output;
    @NonNull private final CliSerializer serializer;

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

    // TODO: JavaDoc
    public void println(String text) {
        output.println(text);
    }

    // TODO: JavaDoc
    public void errorPrintln(String text) {
        output.errorPrintln(text);
    }

    // TODO: JavaDoc
    public void setCommandLine(String commandLine) {
        output.setCommandLine(commandLine);
    }

    // TODO: JavaDoc
    public void setWorkingDirectory(CliDirectory directory) {
        final String path = serializer.serializePathToDirectory(directory);
        output.setWorkingDirectory(path);
    }

    // TODO: JavaDoc
    public void printCommandLine(String commandLine) {
        final String serializedCommandLine = serializer.serializeCommandLine(commandLine);
        output.println(serializedCommandLine);
    }

    // TODO: JavaDoc
    public void printDirectory(CliDirectory directory, boolean recursive) {
        final List<String> serialization = serializer.serializeDirectory(directory, recursive);
        printSerialization(serialization);
    }

    // TODO: JavaDoc
    public void printCommand(CliCommand command) {
        final List<String> serialization = serializer.serializeCommand(command);
        printSerialization(serialization);
    }

    // TODO: JavaDoc
    public void printException(Exception e) {
        final List<String> serialization = serializer.serializeException(e);
        for (String line : serialization) {
            errorPrintln(line);
        }
    }

    // TODO: JavaDoc
    public void printCommandInfo(CommandInfo info) {
        final List<String> serialization = serializer.serializeCommandInfo(info);
        printSerialization(serialization);
    }

    // TODO: JavaDoc
    public void printSuggestions(Suggestions suggestions) {
        final List<String> serialization = serializer.serializeSuggestions(suggestions);
        printSerialization(serialization);
    }

    private void printSerialization(List<String> serialization) {
        for (String line : serialization) {
            println(line);
        }
    }
}
