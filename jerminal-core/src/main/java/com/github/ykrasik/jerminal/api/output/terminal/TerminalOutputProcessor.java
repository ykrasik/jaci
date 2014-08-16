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

import com.github.ykrasik.jerminal.api.command.parameter.view.ShellCommandParamView;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;
import com.github.ykrasik.jerminal.api.output.OutputProcessor;
import com.github.ykrasik.jerminal.internal.exception.ParseError;
import com.google.common.base.Joiner;

import java.util.List;

/**
 * An {@link OutputProcessor} that translates all given events into text and send them to a
 * {@link Terminal} to be printed.
 *
 * @author Yevgeny Krasik
 */
public class TerminalOutputProcessor implements OutputProcessor {
    private static final Joiner JOINER = Joiner.on(',').skipNulls();

    /**
     * Accessible to sub-classes, to support overrides of the default implementations.
     */
    protected final Terminal terminal;

    public TerminalOutputProcessor(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void displayWelcomeMessage(String welcomeMessage) {
        print(welcomeMessage);
    }

    @Override
    public void displayEmptyLine() {
        print("");
    }

    @Override
    public void displayCommandOutput(List<String> output) {
        final StringBuilder sb = new StringBuilder();
        for (String str : output) {
            sb.append(str);
            sb.append('\n');
        }
        print(sb.toString());
    }

    @Override
    public void displaySuggestions(List<String> directorySuggestions,
                                   List<String> commandSuggestions,
                                   List<String> paramNameSuggestions,
                                   List<String> paramValueSuggestions) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Suggestions: \n");
        appendSuggestions(sb, directorySuggestions, "Directories");
        appendSuggestions(sb, commandSuggestions, "Commands");
        appendSuggestions(sb, paramNameSuggestions, "Parameter names");
        appendSuggestions(sb, paramValueSuggestions, "Parameter values");
        print(sb.toString());
    }

    private void appendSuggestions(StringBuilder sb, List<String> suggestions, String suggestionTitle) {
        if (!suggestions.isEmpty()) {
            sb.append("|   ");
            sb.append(suggestionTitle);
            sb.append(": [");
            sb.append(JOINER.join(suggestions));
            sb.append("]\n");
        }
    }

    @Override
    public void displayShellEntryView(ShellEntryView shellEntryView) {
        print(serializeShellEntryView(shellEntryView));
    }

    @Override
    public void displayShellCommandView(ShellCommandView shellCommandView) {
        print(serializeShellCommandView(shellCommandView));
    }

    @Override
    public void parseError(ParseError error, String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void autoCompleteNotPossible(String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void executeError(String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void executeUnhandledException(Exception e) {
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            printError("|    " + stackTraceElement.toString());
        }
    }

    private void print(String message) {
        terminal.print(message);
    }

    private void printError(String message) {
        terminal.printError(message);
    }

    protected String serializeShellCommandView(ShellCommandView command) {
        final StringBuilder sb = new StringBuilder();
        serializeShellCommandView(sb, command);
        return sb.toString();
    }

    private void serializeShellCommandView(StringBuilder sb, ShellCommandView command) {
        sb.append(command.getName());
        sb.append(" : ");
        sb.append(command.getDescription());
        sb.append('\n');

        for (ShellCommandParamView paramView : command.getParams()) {
            appendDepthSpaces(sb, 1);
            serializedShellCommandParamView(sb, paramView);
            sb.append('\n');
        }
    }

    private void appendDepthSpaces(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
    }

    private void serializedShellCommandParamView(StringBuilder sb, ShellCommandParamView param) {
        sb.append(param.getExternalForm());
        sb.append(" - ");
        sb.append(param.getDescription());
    }

    protected String serializeShellEntryView(ShellEntryView entry) {
        final StringBuilder sb = new StringBuilder();
        serializeShellEntryView(sb, entry, 0);
        return sb.toString();
    }

    private void serializeShellEntryView(StringBuilder sb, ShellEntryView entry, int depth) {
        final boolean directory = entry.isDirectory();

        // Print root
        if (directory) {
            sb.append('[');
        }
        sb.append(entry.getName());
        if (directory) {
            sb.append(']');
        }

        if (!directory) {
            sb.append(" : ");
            sb.append(entry.getDescription());
        }
        sb.append('\n');

        // Print children
        if (directory) {
            for (ShellEntryView child : entry.getChildren()) {
                sb.append('|');
                appendDepthSpaces(sb, depth + 1);
                serializeShellEntryView(sb, child, depth + 1);
            }
        }
    }
}
