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
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;

/**
 * Displays information to the user.
 *
 * @author Yevgeny Krasik
 */
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
     */
    void displayWelcomeMessage(String welcomeMessage);

    /**
     * Display an empty line. Usually called when an empty command line was executed.
     */
    void displayEmptyLine();

    /**
     * Display text. Usually generated as output from commands.
     */
    void displayText(String text);

    // FIXME: JavaDoc

    /**
     * Display command info along with it's parsed args.
     */
    void displayCommandInfo(CommandInfo commandInfo);

    /**
     * Display suggestions. Called either because assistance was requested, or due to an error.
     */
    void displaySuggestions(Suggestions suggestions);

    /**
     * Display the directory structure.
     */
    void displayDirectory(ShellDirectory directory);

    /**
     * Display command info.
     */
    void displayCommand(Command command);

    /**
     * Display the parse error that occurred while parsing the command line.
     */
    void displayParseError(ParseError error, String errorMessage);

    /**
     * Display the execution error that was thrown while executing the command line.
     */
    void displayExecuteError(ExecuteException e);

    /**
     * Display the unhandled exception that was thrown while operating on the command line.
     */
    void displayUnhandledException(Exception e);

    // TODO: Add a 'setPath' call, for 'cd'.
}
