/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jerminal.old.display.terminal;

import com.github.ykrasik.jemi.core.directory.CommandDirectoryDef;
import com.github.ykrasik.jemi.cli.assist.Suggestions;
import com.github.ykrasik.jerminal.old.parameter.CommandParam;
import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jerminal.old.command.Command;
import com.github.ykrasik.jemi.core.IdentifierNameComparator;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A {@link DisplayDriver} that translates all received events into text and sends them to a {@link Terminal}
 * to be printed.<br>
 * Text colors for different events can be customized via the {@link TerminalConfiguration}.<br>
 *
 * @author Yevgeny Krasik
 */
public class TerminalDisplayDriver implements DisplayDriver {
    private static final Joiner JOINER = Joiner.on(", ").skipNulls();
    private static final IdentifierNameComparator NAME_COMPARATOR = new IdentifierNameComparator();

    private final Terminal terminal;
    private final TerminalConfiguration configuration;

    public TerminalDisplayDriver(Terminal terminal, TerminalConfiguration configuration) {
        this.terminal = Objects.requireNonNull(terminal);
        this.configuration = Objects.requireNonNull(configuration);
    }

    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public void begin() {
        terminal.begin();
    }

    @Override
    public void end() {
        terminal.end();
    }

    @Override
    public void displayWelcomeMessage(String welcomeMessage) {
        println(welcomeMessage);
    }

    @Override
    public void displayCommandLine(String commandLine, boolean isExecute) {
        // Don't really care about isExecute.
        println("> " + commandLine);
    }

    @Override
    public void displayText(String text) {
        println(text);
    }

    @Override
    public void displayCommandInfo(CommandInfo commandInfo) {
        final Command command = commandInfo.getCommand();
        final List<Optional<String>> paramValues = commandInfo.getParamValues();
        final Optional<CommandParam> currentParam = commandInfo.getCurrentParam();
        printCommand(command, 0, false, true, paramValues, currentParam);
    }

    @Override
    public void displaySuggestions(Suggestions suggestions) {
        suggestionsPrintln("Suggestions:");
        printSuggestions(suggestions.getDirectorySuggestions(), "Directories");
        printSuggestions(suggestions.getCommandSuggestions(), "Commands");
        printSuggestions(suggestions.getParamNameSuggestions(), "Parameter names");
        printSuggestions(suggestions.getParamValueSuggestions(), "Parameter values");
    }

    private void printSuggestions(List<String> suggestions, String suggestionsTitle) {
        if (!suggestions.isEmpty()) {
            final StringBuilder sb = stringBuilderWithTabs(1);
            sb.append(suggestionsTitle);
            sb.append(": [");
            sb.append(JOINER.join(suggestions));
            sb.append(']');
            suggestionsPrintln(sb.toString());
        }
    }

    @Override
    public void displayDirectory(CommandDirectoryDef directory) {
        printDirectory(directory, 0);
    }

    private void printDirectory(CommandDirectoryDef directory, int depth) {
        final StringBuilder sb = stringBuilderWithTabs(depth);

        // Print root directory name.
        sb.append('[');
        sb.append(directory.getName());
        sb.append(']');
        directoryPrintln(sb.toString());

        // Print child commands.
        final List<Command> commands = new ArrayList<>(directory.getCommandDefs());
        Collections.sort(commands, NAME_COMPARATOR);
        for (Command command : commands) {
            printCommand(command, depth + 1, true, false, Collections.<Optional<String>>emptyList(), Optional.<CommandParam>absent());
        }

        // Print child directories.
        final List<CommandDirectoryDef> directories = new ArrayList<>(directory.getDirectoryDefs());
        Collections.sort(directories, NAME_COMPARATOR);
        for (CommandDirectoryDef childDirectory : directories) {
            printDirectory(childDirectory, depth + 1);
        }
    }

    private void printCommand(Command command,
                              int depth,
                              boolean withDescription,
                              boolean withParams,
                              List<Optional<String>> paramValues,
                              Optional<CommandParam> currentParam) {
        final StringBuilder sb = stringBuilderWithTabs(depth);

        // Print name : description
        sb.append(command.getName());
        if (withDescription) {
            sb.append(" : ");
            sb.append(command.getDescription());
        }
        commandPrintln(sb.toString());

        // Print params.
        if (withParams) {
            final List<CommandParam> params = command.getParams();
            for (int i = 0; i < params.size(); i++) {
                final CommandParam param = params.get(i);
                final Optional<String> value = !paramValues.isEmpty() ? paramValues.get(i) : Optional.<String>absent();
                final boolean isCurrent = currentParam.isPresent() && currentParam.get() == param;
                printParam(param, depth + 1, withDescription, value, isCurrent);
            }
        }
    }

    private void printParam(CommandParam param,
                            int depth,
                            boolean withDescription,
                            Optional<String> value,
                            boolean isCurrent) {
        final StringBuilder sb = stringBuilderWithTabs(depth);

        // Surround the current param being parsed with -> <-
        if (isCurrent) {
            sb.append("-> ");
            sb.append(getTab());
        } else {
            sb.append(getTab());
        }

        sb.append(param.getExternalForm());
        if (withDescription) {
            sb.append(" : ");
            sb.append(param.getDescription());
        }

        if (value.isPresent()) {
            sb.append(" = ");
            sb.append(value.get());
        }

        // Actually, value.isPresent and isCurrent cannot both be true at the same time.
        if (isCurrent) {
            sb.append(getTab());
            sb.append(" <-");
        }

        paramPrintln(sb.toString());
    }

    @Override
    public void displayCommand(Command command) {
        printCommand(command, 0, true, true, Collections.<Optional<String>>emptyList(), Optional.<CommandParam>absent());
    }

    @Override
    public void displayParseError(ParseError error, String errorMessage) {
        errorPrintln(errorMessage);
    }

    @Override
    public void displayException(Exception e) {
        errorPrintln(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            errorPrintln(getTab() + stackTraceElement.toString());
        }
    }

    private StringBuilder stringBuilderWithTabs(int tabs) {
        final String tab = getTab();
        final StringBuilder sb = new StringBuilder(tab.length() * tabs);
        for (int i = 0; i < tabs; i++) {
            sb.append(tab);
        }
        return sb;
    }

    private String getTab() {
        return terminal.getTab();
    }

    private void println(String text) {
        terminal.println(text, configuration.getTextColor());
    }

    private void errorPrintln(String text) {
        terminal.println(text, defaultColorIfNull(configuration.getErrorColor()));
    }

    private void suggestionsPrintln(String text) {
        terminal.println(text, defaultColorIfNull(configuration.getSuggestionsColor()));
    }

    private void directoryPrintln(String text) {
        terminal.println(text, defaultColorIfNull(configuration.getDirectoryColor()));
    }

    private void commandPrintln(String text) {
        terminal.println(text, defaultColorIfNull(configuration.getCommandColor()));
    }

    private void paramPrintln(String text) {
        terminal.println(text, defaultColorIfNull(configuration.getParamColor()));
    }

    private TerminalColor defaultColorIfNull(TerminalColor color) {
        return color != null ? color : configuration.getTextColor();
    }
}
