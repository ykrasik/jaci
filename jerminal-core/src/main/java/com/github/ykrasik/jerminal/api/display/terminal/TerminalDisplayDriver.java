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

package com.github.ykrasik.jerminal.api.display.terminal;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;

import java.util.Objects;

/**
 * A {@link DisplayDriver} that translates all received events into text through a {@link TerminalSerializer}
 * and sends them to a {@link Terminal} to be printed.
 *
 * @author Yevgeny Krasik
 */
public class TerminalDisplayDriver implements DisplayDriver {
    private final Terminal terminal;
    private final TerminalSerializer serializer;

    private int numInteractions;

    public TerminalDisplayDriver(Terminal terminal) {
        this(terminal, new DefaultTerminalSerializer());
    }

    public TerminalDisplayDriver(Terminal terminal, TerminalSerializer serializer) {
        this.terminal = Objects.requireNonNull(terminal);
        this.serializer = Objects.requireNonNull(serializer);
    }

    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public void begin() {
        terminal.begin();
        numInteractions = 0;
    }

    @Override
    public void end() {
        if (numInteractions != 0) {
            // If anything was printed, add an empty line afterwards.
            displayEmptyLine();
        }
        terminal.end();
    }

    @Override
    public void displayWelcomeMessage(String welcomeMessage) {
        println(welcomeMessage);
    }

    @Override
    public void displayEmptyLine() {
        // The terminal is expected to add a new line after any text.
        println("");
    }

    @Override
    public void displayText(String text) {
        println(text);
    }

    @Override
    public void displayCommandInfo(CommandInfo commandInfo) {
        final String assistInfoStr = serializer.serializeCommandInfo(commandInfo);
        println(assistInfoStr);
    }

    @Override
    public void displaySuggestions(Suggestions suggestions) {
        final String suggestionsStr = serializer.serializeSuggestions(suggestions);
        println(suggestionsStr);
    }

    @Override
    public void displayDirectory(ShellDirectory directory) {
        final String shellEntryViewStr = serializer.serializeDirectory(directory);
        println(shellEntryViewStr);
    }

    @Override
    public void displayCommand(Command command) {
        final String shellCommandViewStr = serializer.serializeCommand(command);
        println(shellCommandViewStr);
    }

    @Override
    public void displayParseError(ParseError error, String errorMessage) {
        errorPrintln(errorMessage);
    }

    @Override
    public void displayExecuteError(ExecuteException e) {
        printException(e);
    }

    @Override
    public void displayUnhandledException(Exception e) {
        printException(e);
    }

    private void printException(Exception e) {
        final String exceptionStr = serializer.serializeException(e);
        errorPrintln(exceptionStr);
    }

    private void println(String message) {
        numInteractions++;
        terminal.println(message);
    }

    private void errorPrintln(String message) {
        numInteractions++;
        terminal.errorPrintln(message);
    }
}
