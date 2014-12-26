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
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.display.InteractionCountingDisplayDriver;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.assist.AssistReturnValue;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteType;
import com.github.ykrasik.jerminal.internal.assist.SuggestionsBuilder;
import com.github.ykrasik.jerminal.internal.command.ControlCommandFactory;
import com.github.ykrasik.jerminal.internal.command.OutputPrinterImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.InternalShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Built on top of a {@link ShellFileSystem}, processes command lines and displays results
 * as a side effect through a {@link com.github.ykrasik.jerminal.api.display.DisplayDriver}.<br>
 * Changes to the underlying {@link ShellFileSystem} will <b>not</b> be picked up after this object is constructed
 * (and why would you want to keep changing the {@link ShellFileSystem} afterwards, anyway?)<br>
 * <br>
 * The Shell does not alter the command line in any way. It is assumed that an external system
 * has ownership of the command line and is the one in charge of manipulating it. The Shell, in turn, simply returns
 * what the new command line should be on each of it's calls.<br>
 * <br>
 *
 * @author Yevgeny Krasik
 */
// TODO: Create AssistReturnValue and ExecuteReturnValue instead of having a DisplayDriver with side effects?
public class Shell {
    private final InternalShellFileSystem fileSystem;
    private final InteractionCountingDisplayDriver displayDriver;
    private final OutputPrinter outputPrinter;

    public Shell(ShellFileSystem fileSystem, DisplayDriver displayDriver) {
        this(fileSystem, displayDriver, "Welcome to Jerminal!\n");
    }

    public Shell(ShellFileSystem fileSystem, DisplayDriver displayDriver, String welcomeMessage) {
        this.displayDriver = new InteractionCountingDisplayDriver(displayDriver);
        this.fileSystem = createFileSystem(Objects.requireNonNull(fileSystem), this.displayDriver);
        this.outputPrinter = new OutputPrinterImpl(this.displayDriver);

        // Initial displayDriver stuff.
        displayDriver.begin();
        displayDriver.setWorkingDirectory(Collections.singletonList(fileSystem.getRoot().getName()));
        displayDriver.displayWelcomeMessage(welcomeMessage);
        displayDriver.end();
    }

    private InternalShellFileSystem createFileSystem(ShellFileSystem fileSystem, DisplayDriver displayDriver) {
        final InternalShellFileSystem internalShellFileSystem = new InternalShellFileSystem(fileSystem);
        new ControlCommandFactory(internalShellFileSystem, displayDriver).installControlCommands();
        return internalShellFileSystem;
    }

    /**
     * Provide assistance for the command line.
     *
     * @param commandLine Command line to provide assistance for.
     * @return The new command line if assistance was possible, or {@link com.google.common.base.Optional#absent()} otherwise.
     */
    public Optional<String> assist(String commandLine) {
        displayDriver.begin();
        try {
            return parseAndAssist(commandLine);
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            displayDriver.displayException(e);
        } finally {
            if (displayDriver.getInteractions() > 0) {
                displayDriver.displayEmptyLine();
            }
            displayDriver.end();
        }
        return Optional.absent();
    }

    private Optional<String> parseAndAssist(String rawCommandLine) throws ParseException {
        // Split the commandLine for autoComplete.
        // If the commandLine ends with a space (or is empty), we manually insert an empty arg.
        // This implies that the user wanted assistance about the NEXT argument and not the last one he typed.
        final boolean endsWithSpace = rawCommandLine.isEmpty() || rawCommandLine.endsWith(" ");
        final List<String> splitCommandLine = splitCommandLine(rawCommandLine.trim());
        if (endsWithSpace) {
            splitCommandLine.add("");
        }

        // Do the actual autoCompletion.
        final AssistReturnValue returnValue = doAssist(splitCommandLine);
        return processAssistReturnValue(returnValue, rawCommandLine);
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
        final InternalCommand file = fileSystem.parsePathToCommand(rawPath);

        // Provide assistance with the command parameters.
        // The command args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        return file.assistArgs(args);
    }

    private Optional<String> processAssistReturnValue(AssistReturnValue returnValue, String rawCommandLine) {
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

    /**
     * Execute the command line.
     *
     * @param commandLine Command line to execute.
     * @return True if the command line was executed successfully.
     */
    public boolean execute(String commandLine) {
        displayDriver.begin();
        try {
            parseAndExecute(commandLine);
            return true;
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            displayDriver.displayException(e);
        } finally {
            displayDriver.displayEmptyLine();
            displayDriver.end();
        }
        return false;
    }

    private void parseAndExecute(String rawCommandLine) throws Exception {
        // Split the commandLine.
        final List<String> commandLine = splitCommandLine(rawCommandLine.trim());
        if (commandLine.isEmpty()) {
            // Received a commandLine that is either empty or full of spaces.
            return;
        }

        // Parse commandLine.
        // The first arg of the commandLine must be a path to a command.
        final String pathToCommand = commandLine.get(0);
        final InternalCommand internalCommand = fileSystem.parsePathToCommand(pathToCommand);

        // Parse the command args.
        // The command args start from the 2nd commandLine element (the first was the command).
        final List<String> rawArgs = commandLine.subList(1, commandLine.size());
        final CommandArgs args = internalCommand.parseCommandArgs(rawArgs);

        // Execute the command.
        final Command command = internalCommand.getCommand();
        command.execute(args, outputPrinter);

        if (displayDriver.getInteractions() == 0) {
            final String message = String.format("Command '%s' executed successfully.", command.getName());
            displayDriver.displayText(message);
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
                // Add double-quoted string without the quotes.
                matchList.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Add single-quoted string without the quotes.
                matchList.add(matcher.group(2));
            } else {
                // Add unquoted word.
                matchList.add(matcher.group());
            }
        }
        return matchList;
    }

    // A pattern that matches spaces that aren't surrounded by single or double quotes.
    private static final Pattern ARGS_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
}
