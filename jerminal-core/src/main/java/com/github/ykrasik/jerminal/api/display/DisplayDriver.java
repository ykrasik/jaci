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

package com.github.ykrasik.jerminal.api.display;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;

import java.util.List;

/**
 * Displays information to the user.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Should this be renamed to Output?
public interface DisplayDriver {
    /**
     * Called before any other events are called, to allow the driver to prepare itself.<br>
     * Will not be called again before {@link #end()} is called.
     */
    void begin();

    /**
     * Called when a single event flow has finished.<br>
     * {@link #begin()} will be called before any more events arrive.
     */
    void end();

    /**
     * Display the welcome message.
     *
     * @param welcomeMessage Welcome message to display.
     */
    void displayWelcomeMessage(String welcomeMessage);

    /**
     * Display an empty line. Usually called when an empty command line was executed.
     */
    void displayEmptyLine();

    /**
     * Display text. Usually generated as output from commands.
     *
     * @param text Text to display.
     */
    void displayText(String text);

    /**
     * Display command info along with it's parsed args.<br>
     * Called when assistance was requested or if a parsing error occurred.<br>
     * Differs from {@link #displayCommand(Command)} in that {@link #displayCommand(Command)} is called when
     * the user requests to display information about a command without executing it.
     *
     * @param commandInfo Command info to display.
     */
    void displayCommandInfo(CommandInfo commandInfo);

    /**
     * Display suggestions. Called either because assistance was requested or due to an error.
     *
     * @param suggestions Suggestions to display.
     */
    void displaySuggestions(Suggestions suggestions);

    /**
     * Display the directory structure.
     *
     * @param directory Directory to display.
     */
    void displayDirectory(ShellDirectory directory);

    /**
     * Display command info.<br>
     * Called when information about a specific command was requested.<br>
     *
     * @param command Command to display.
     */
    void displayCommand(Command command);

    // TODO: Does this 100% belong here? This is the only call that assumes state.
    /**
     * Set the path to the current working directory. Only called when when the working directory changes.
     * The path will always be non-empty and the first element will always be the name of the root directory.
     *
     * @param path New working path.
     */
    void setWorkingDirectory(List<String> path);

    /**
     * Display the parse error that occurred while parsing the command line.
     *
     * @param error The parse error.
     * @param errorMessage The error message details.
     */
    void displayParseError(ParseError error, String errorMessage);

    /**
     * Display the exception that was thrown while executing the command line.
     *
     * @param e Exception to display.
     */
    void displayException(Exception e);
}
