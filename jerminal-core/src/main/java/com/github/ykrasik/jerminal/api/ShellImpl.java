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

package com.github.ykrasik.jerminal.api;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.Command;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.CommandLineHistory;
import com.github.ykrasik.jerminal.internal.command.ControlCommandFactory;
import com.github.ykrasik.jerminal.internal.command.OutputPrinterImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFile;
import com.github.ykrasik.jerminal.internal.returnvalue.AssistReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.github.ykrasik.jerminal.internal.returnvalue.SuggestionsBuilder;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A builder for a {@link Shell}.<br>
 * This builder expects to receive a path to a command and a set of commands.
 * The path is separated by the delimiter '/', for example: "path/to/element". Any directories along that path that don't exist
 * will be automatically created.<br>
 * Directories may also provide an optional description. The description delimiter is ':'. A description may
 * only be assigned to a directory when it is first created. Any subsequent calls may omit the description.<br>
 * <p>For example: "this/is/a/path : This is a path element/to/some/directory : Everything up till now is a directory".<br>
 * This will create the following directory structure: this/is/a/path/to/some/directory and also assign the given descriptions
 * to "path" and "directory".</p>
 *
 * @author Yevgeny Krasik
 */
// FIXME: Update JavaDoc.
// FIXME: Add JavaFX frontend gui
public class ShellImpl implements Shell {
    private final ShellFileSystem fileSystem;
    private final DisplayDriver displayDriver;
    private final CommandLineHistory history;

    public ShellImpl(ShellFileSystem fileSystem, DisplayDriver displayDriver) {
        this(fileSystem, displayDriver, 30, "Welcome to Jerminal!\n");
    }

    public ShellImpl(ShellFileSystem fileSystem,
                     DisplayDriver displayDriver,
                     int maxHistory,
                     String welcomeMessage) {
        this.displayDriver = Objects.requireNonNull(displayDriver);
        this.fileSystem = new ControlCommandFactory(Objects.requireNonNull(fileSystem), displayDriver).installControlCommands();
        this.history = new CommandLineHistory(maxHistory);

        // Display welcome message.
        displayDriver.begin();
        displayDriver.displayWelcomeMessage(welcomeMessage);
        displayDriver.end();
    }

    @Override
    public Optional<String> getPrevCommandLineFromHistory() {
        return history.getPrevCommandLine();
    }

    @Override
    public Optional<String> getNextCommandLineFromHistory() {
        return history.getNextCommandLine();
    }

    // FIXME: Javadoc
    @Override
    public Optional<String> assist(String rawCommandLine) {
        displayDriver.begin();
        try {
            // Split the commandLine for autoComplete.
            final boolean endsWithSpace = rawCommandLine.isEmpty() || rawCommandLine.endsWith(" ");

            // If the commandLine ends with a space (or is empty), we manually insert an empty arg.
            // This implies that the user wanted assistance about the NEXT argument and not the last one he typed.
            final List<String> commandLine = splitCommandLine(rawCommandLine.trim());
            if (endsWithSpace) {
                commandLine.add("");
            }

            // Do the actual autoCompletion.
            final AssistReturnValue returnValue = doAssist(commandLine);
            return handleAssist(returnValue, rawCommandLine);
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            displayDriver.displayInternalError(e);
        } finally {
            displayDriver.end();
        }

        // There was an error parsing the command line.
        return Optional.absent();
    }

    // FIXME: I do still want the old errors - no more params, etc.
    private AssistReturnValue doAssist(List<String> commandLine) throws ParseException {
        // The first arg of the commandLine must be a path to a command(file).
        final String rawPath = commandLine.get(0);

        // If we only have 1 arg, we are trying to autoComplete a path to a command.
        // Otherwise, the first arg is expected to be a valid command and we are autoCompleting its' args.
        if (commandLine.size() == 1) {
            // The first arg is the only arg on the commandLine, autoComplete path.
            final AutoCompleteReturnValue autoCompleteReturnValue = fileSystem.autoCompletePath(rawPath);
            return new AssistReturnValue(Optional.<CommandInfo>absent(), autoCompleteReturnValue);
        }

        // The first arg is not the only arg on the commandLine, it is expected to be a valid path to a file(command).
        final ShellFile file = fileSystem.parsePathToFile(rawPath);

        // Provide assistance with the command parameters.
        // The command args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        return file.assistArgs(args);
    }

    private Optional<String> handleAssist(AssistReturnValue returnValue, String rawCommandLine) {
        // This method does 3 things:
        // 1. Display command info, if there is any.
        // 2. Determine the suggestions for auto complete.
        // 3. Determine what the new command line should be.
        final AutoCompleteReturnValue autoCompleteReturnValue = returnValue.getAutoCompleteReturnValue();

        displayCommandInfoIfPresent(returnValue.getCommandInfo());

        final Trie<AutoCompleteType> possibilities = autoCompleteReturnValue.getPossibilities();
        if (possibilities.isEmpty()) {
            // There are no auto complete possibilities, so no suggestions either.
            // TODO: Print an error that no suggestions are available?
            return Optional.absent();
        }

        final String prefix = autoCompleteReturnValue.getPrefix();
        final String autoCompleteAddition;
        if (possibilities.size() == 1) {
            // TODO: Only 1 possibility, assistInfo should be updated to show it...
            // Only a single auto complete result is possible, append it to the command line.
            // This is also why there are no suggestions - only 1 is possible.
            // Let's be helpful - depending on the autoCompleteType, add a suffix.
            final Entry<String, AutoCompleteType> entry = possibilities.entrySet().iterator().next();
            final String singlePossibility = entry.getKey();
            final AutoCompleteType type = entry.getValue();
            final char suffix = type.getSuffix();
            autoCompleteAddition = getAutoCompleteAddition(prefix, singlePossibility) + suffix;
        } else {
            // Multiple auto complete results are possible.
            // AutoComplete as much as is possible - until the longest common prefix.
            final String longestPrefix = possibilities.getLongestPrefix();
            autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);

            // There are at least 2 possibilities, so suggestions are available.
            final Suggestions suggestions = getSuggestions(possibilities);
            displayDriver.displaySuggestions(suggestions);
        }

        return Optional.of(rawCommandLine + autoCompleteAddition);
    }

    private Suggestions getSuggestions(Trie<AutoCompleteType> possibilities) {
        final SuggestionsBuilder builder = new SuggestionsBuilder();
        for (Entry<String, AutoCompleteType> entry : possibilities.entrySet()) {
            builder.addSuggestion(entry.getValue(), entry.getKey());
        }
        return builder.build();
    }

    private String getAutoCompleteAddition(String prefix, String autoCompletedPrefix) {
        return autoCompletedPrefix.substring(prefix.length());
    }

    // FIXME: JavaDoc
    @Override
    public boolean execute(String rawCommandLine) {
        displayDriver.begin();
        try {
            return parseAndExecute(rawCommandLine);
        } catch (Exception e) {
            displayDriver.displayInternalError(e);
            return false;
        } finally {
            displayDriver.end();
        }
    }

    private boolean parseAndExecute(String rawCommandLine) {
        // Split the commandLine.
        final List<String> commandLine = splitCommandLine(rawCommandLine.trim());
        if (commandLine.isEmpty()) {
            // Received a commandLine that is either empty or full of spaces.
            displayDriver.displayEmptyLine();
            return true;
        }

        // Parse commandLine.
        final ShellFile file;
        final CommandArgs args;
        try {
            // The first arg of the commandLine must be a path to a file(command).
            final String pathToCommand = commandLine.get(0);
            file = fileSystem.parsePathToFile(pathToCommand);

            // Parse the command args.
            // The command args start from the 2nd commandLine element (the first was the command).
            final List<String> rawArgs = commandLine.subList(1, commandLine.size());
            args = file.parseCommandArgs(rawArgs);
        } catch (ParseException e) {
            handleParseException(e);
            return false;
        }

        // Successfully parsed commandLine.
        // Save command in history.
        history.pushCommandLine(rawCommandLine);

        // Execute the command.
        executeFile(file, args);

        // Command line was successfully parsed and executed.
        return true;
    }

    private void executeFile(ShellFile file, CommandArgs args) {
        final Command command = file.getCommand();
        final OutputPrinterImpl outputPrinter = new OutputPrinterImpl(displayDriver);
        try {
            command.execute(args, outputPrinter);
            // Print a generic success message.
            outputPrinter.println("Command '%s' executed successfully.", command.getName());
        } catch (ExecuteException e) {
            displayDriver.displayExecuteError(e);
        } catch (Exception e) {
            outputPrinter.println("Command '%s' was terminated due to an unhandled exception!", command.getName());
            displayDriver.displayExecuteUnhandledException(e);
        }
    }

    private void handleParseException(ParseException e) {
        displayCommandInfoIfPresent(e.getCommandInfo());
        final String errorMessage = String.format("Parse Error: %s", e.getMessage());
        displayDriver.displayParseError(e.getError(), errorMessage);
    }

    private void displayCommandInfoIfPresent(Optional<CommandInfo> commandInfo) {
        if (commandInfo.isPresent()) {
            displayDriver.displayCommandInfo(commandInfo.get());
        }
    }

    private List<String> splitCommandLine(String commandLine) {
        final List<String> matchList = new ArrayList<>();
        final Matcher matcher = ARGS_PATTERN.matcher(commandLine);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(matcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(matcher.group());
            }
        }
        return matchList;
    }

    // A pattern that matches spaces that aren't surrounded by single or double quotes.
    private static final Pattern ARGS_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
}
