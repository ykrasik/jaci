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

package com.github.ykrasik.jerminal.api.output.terminal;

import com.github.ykrasik.jerminal.api.assist.AssistInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;
import com.github.ykrasik.jerminal.api.output.OutputProcessor;
import com.google.common.base.Optional;

/**
 * An {@link OutputProcessor} that translates all given events into text through a {@link TerminalSerializer}
 * and send them to a {@link Terminal} to be printed.
 *
 * @author Yevgeny Krasik
 */
public class TerminalOutputProcessor implements OutputProcessor {
    private final Terminal terminal;
    private final TerminalSerializer serializer;

    private int numInteractions;

    public TerminalOutputProcessor(Terminal terminal) {
        this(terminal, new DefaultTerminalSerializer());
    }

    public TerminalOutputProcessor(Terminal terminal, TerminalSerializer serializer) {
        this.terminal = terminal;
        this.serializer = serializer;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public TerminalSerializer getSerializer() {
        return serializer;
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
        print(welcomeMessage);
    }

    @Override
    public void displayEmptyLine() {
        print(serializer.getEmptyLine());
    }

    @Override
    public void displayText(String text) {
        print(text);
    }

    @Override
    public void displayAssistance(Optional<AssistInfo> assistInfo, Optional<Suggestions> suggestions) {
        if (assistInfo.isPresent()) {
            final String assistInfoStr = serializer.serializeAssistInfo(assistInfo.get());
            print(assistInfoStr);
        }

        if (suggestions.isPresent()) {
            final String suggestionsStr = serializer.serializeSuggestions(suggestions.get());
            print(suggestionsStr);
        } else {
            // TODO: Find a way to print this when no suggestions are available, but not to print when only a single one was possible.
//            printError("No auto complete suggestions available.");
        }
    }

    @Override
    public void displayShellEntryView(ShellEntryView shellEntryView) {
        final String shellEntryViewStr = serializer.serializeShelEntryView(shellEntryView);
        print(shellEntryViewStr);
    }

    @Override
    public void displayShellCommandView(ShellCommandView shellCommandView) {
        final String shellCommandViewStr = serializer.serializeShellCommandView(shellCommandView);
        print(shellCommandViewStr);
    }

    @Override
    public void parseError(ParseError error, String errorMessage, Optional<Suggestions> suggestions) {
        printError(errorMessage);
        if (suggestions.isPresent()) {
            final String suggestionsStr = serializer.serializeSuggestions(suggestions.get());
            print(suggestionsStr);
        }
    }

    @Override
    public void executeError(ExecuteException e) {
        printException(e);
    }

    @Override
    public void executeUnhandledException(Exception e) {
        printException(e);
    }

    @Override
    public void internalError(Exception e) {
        printException(e);
    }

    private void printException(Exception e) {
        final String exceptionStr = serializer.serializeException(e);
        printError(exceptionStr);
    }

    private void print(String message) {
        numInteractions++;
        terminal.print(message);
    }

    private void printError(String message) {
        numInteractions++;
        terminal.printError(message);
    }
}
