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

package com.github.ykrasik.jaci.cli.output;

import com.github.ykrasik.jaci.cli.assist.CommandInfo;
import com.github.ykrasik.jaci.cli.assist.Suggestions;
import com.github.ykrasik.jaci.cli.command.CliCommand;
import com.github.ykrasik.jaci.cli.directory.CliDirectory;

/**
 * A component that can serialize CLI entities into {@link String}s or {@link Serialization}s (which is a sequence
 * of lines) to be printed.
 * This is the interface to implement in order to customize the CLI's print behavior.
 *
 * @author Yevgeny Krasik
 */
public interface CliSerializer {
    /**
     * Serialize the path to a CLI directory. Used to display the current 'working directory'.
     *
     * @param directory Directory to serialize the path to.
     * @return The path to given directory.
     */
    String serializePathToDirectory(CliDirectory directory);

    /**
     * Serialize the command line. Called before printing the command line to the output,
     * may be used to prepend or append characters to the command line.
     *
     * @param workingDirectory Current working directory.
     * @param commandLine Command line that was typed.
     * @return Command line to be printed.
     */
    String serializeCommandLine(CliDirectory workingDirectory, String commandLine);

    /**
     * Serialize a directory and it's content.
     *
     * @param directory Directory to serialize.
     * @param recursive Whether to recurse into sub-directories.
     * @return A serialized representation of the directory and it's content.
     */
    Serialization serializeDirectory(CliDirectory directory, boolean recursive);

    /**
     * Serialize a command (it's name, description and parameters).
     *
     * @param command Command to serialize.
     * @return A serialized representation of the command.
     */
    Serialization serializeCommand(CliCommand command);

    /**
     * Serialize a throwable.
     *
     * @param e Throwable to serialize.
     * @return A serialized representation of the exception.
     */
    Serialization serializeThrowable(Throwable e);

    /**
     * Serialize information about a command. Called to display assistance information about a command, or if a parse
     * error occurred while parsing the command's parameters.
     *
     * @param info Command info to serialize.
     * @return A serialized representation of the command info.
     */
    Serialization serializeCommandInfo(CommandInfo info);

    /**
     * Serialize suggestions.
     *
     * @param suggestions Suggestions to serialize.
     * @return A serialized representation of the suggestions.
     */
    Serialization serializeSuggestions(Suggestions suggestions);
}
