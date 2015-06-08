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

package com.github.ykrasik.jaci.cli.hierarchy;

import com.github.ykrasik.jaci.cli.command.CliCommand;
import com.github.ykrasik.jaci.cli.directory.CliDirectory;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.cli.assist.AutoComplete;

/**
 * A container for a hierarchy of {@link CliDirectory} and {@link CliCommand}.<br>
 * Can parse and auto-complete paths to {@link CliDirectory}s and {@link CliCommand}s.
 * Paths are expected to be separated by the delimiter '/', like "path/to/element".
 * Trying to parse or auto-complete an invalid path will result in a {@link ParseException} being thrown.<br>
 * <br>
 * Keeps a current working directory. Any path that doesn't explicitly start from the root (doesn't start with a '/')
 * will be calculated from the current working directory.<br>
 *
 * @author Yevgeny Krasik
 */
public interface CliCommandHierarchy {
    /**
     * @return The current working directory.
     */
    CliDirectory getWorkingDirectory();

    /**
     * Set the working directory.
     *
     * @param workingDirectory Working directory to set.
     */
    void setWorkingDirectory(CliDirectory workingDirectory);

    /**
     * Parse the string as a path to a {@link CliDirectory}.<br>
     * Parsing a path always starts from the current working directory, unless the path explicitly starts from root.
     *
     * @param rawPath Path to parse.
     * @return The {@link CliDirectory} pointed to by the path.
     * @throws ParseException If the path is invalid or doesn't point to an {@link CliDirectory}.
     */
    CliDirectory parsePathToDirectory(String rawPath) throws ParseException;

    /**
     * Parse the string as a path to an {@link CliCommand}.<br>
     * Parsing a path always starts from the working directory, unless the path explicitly starts from root.
     *
     * @param rawPath Path to parse.
     * @return The {@link CliCommand} pointed to by the path.
     * @throws ParseException If the path is invalid or doesn't point to an {@link CliCommand}.
     */
    CliCommand parsePathToCommand(String rawPath) throws ParseException;

    /**
     * Provide auto complete suggestions for the path to an {@link CliDirectory}.<br>
     * The path is expected to be valid all the way except the last element, which will be auto completed.
     *
     * @param rawPath Path to auto complete.
     * @return Auto complete suggestions for the next {@link CliDirectory} in this path.
     * @throws ParseException If the path is invalid.
     */
    AutoComplete autoCompletePathToDirectory(String rawPath) throws ParseException;

    /**
     * Provide auto complete suggestions for the path either to a {@link CliDirectory} or to a {@link CliCommand}.<br>
     * The path is expected to be valid all the way except the last element, which will be auto completed.
     *
     * @param rawPath Path to auto complete.
     * @return Auto complete suggestions for the next {@link CliDirectory} or {@link CliCommand} in this path.
     * @throws ParseException If the path is invalid.
     */
    AutoComplete autoCompletePath(String rawPath) throws ParseException;
}
