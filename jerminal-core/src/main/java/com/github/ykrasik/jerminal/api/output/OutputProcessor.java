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

package com.github.ykrasik.jerminal.api.output;

import com.github.ykrasik.jerminal.api.assist.AssistInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;
import com.google.common.base.Optional;

/**
 * In charge of displaying information to the user.<br>
 * Receives it's commands from the {@link com.github.ykrasik.jerminal.api.Shell Shell} to which it is attached.<br>
 *
 * @author Yevgeny Krasik
 */
public interface OutputProcessor {
    /**
     * Called before any other events are called, to allow the {@link OutputProcessor} to prepare itself.<br>
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

    /**
     * The user requested assistance with the command line, display the results.
     */
    void displayAssistance(Optional<AssistInfo> assistInfo, Optional<Suggestions> suggestions);

    /**
     * The user requested to display the directory structure of a directory.
     */
    void displayShellEntryView(ShellEntryView shellEntryView);

    /**
     * The user requested to display information about a command.
     */
    void displayShellCommandView(ShellCommandView shellCommandView);

    /**
     * A parse error occurred while parsing the command line.
     */
    // TODO: Put all these params in a single ParseErrorContext?
    void parseError(ParseError error, String errorMessage, Optional<Suggestions> suggestions);

    /**
     * An execution error occurred while executing the command line.<br>
     */
    void executeError(ExecuteException e);

    /**
     * An unhandled exception was thrown while executing the command line.<br>
     * This is not an internal error -
     * this exception was thrown from within the code associated with the command being run.
     */
    void executeUnhandledException(Exception e);

    /**
     * An internal error has occurred. Shouldn't happen :)
     */
    void internalError(Exception e);

    // TODO: Add a 'setPath' call, for 'cd'.
}
