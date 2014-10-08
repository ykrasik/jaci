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

package com.github.ykrasik.jerminal.internal;

import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.Command;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.output.OutputProcessor;
import com.github.ykrasik.jerminal.collections.trie.Trie;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation for a {@link Shell}.
 *
 * @author Yevgeny Krasik
 */
public class ShellImpl implements Shell {
    // A pattern that matches spaces that aren't surrounded by single or double quotes.
    private static final Pattern ARGS_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    // FIXME: Add JavaFX frontend gui

    private final OutputProcessor outputProcessor;
    private final ShellFileSystem fileSystem;
    private final CommandLineHistory commandLineHistory;

    public ShellImpl(OutputProcessor outputProcessor,
                     ShellFileSystem fileSystem,
                     CommandLineHistory commandLineHistory,
                     String welcomeMessage) {
        this.outputProcessor = outputProcessor;
        this.fileSystem = fileSystem;
        this.commandLineHistory = commandLineHistory;

        // Init.
        outputProcessor.begin();
        outputProcessor.displayWelcomeMessage(welcomeMessage);
        outputProcessor.end();
    }

    @Override
    public Optional<String> getPrevCommandLineFromHistory() {
        return commandLineHistory.getPrevCommandLine();
    }

    @Override
    public Optional<String> getNextCommandLineFromHistory() {
        return commandLineHistory.getNextCommandLine();
    }

    @Override
    public String assist(String rawCommandLine) {
        // TODO: Return Optional.absent on error?
        outputProcessor.begin();
        try {
            // Split the commandLine for autoComplete.
            // Only remove leading spaces, trailing spaces have a significant meaning.
            final boolean endsWithSpace = rawCommandLine.isEmpty() || rawCommandLine.endsWith(" ");

            // If the commandLine ends with a space (or is empty), we manually insert an empty arg.
            // This signifies that the user wanted assistance about the NEXT argument and not the last one he typed.
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
            outputProcessor.internalError(e);
        } finally {
            outputProcessor.end();
        }

        // There was an error parsing the command line, return the old one.
        return rawCommandLine;
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

    private String handleAssist(AssistReturnValue returnValue, String rawCommandLine) {
        // This method does 3 things:
        // 1. Display command information, if there is any.
        // 2. Determine the suggestions for auto complete.
        // 3. Determine what the new command line should be.
        final Optional<CommandInfo> commandInfo = returnValue.getCommandInfo();

        final AutoCompleteReturnValue autoCompleteReturnValue = returnValue.getAutoCompleteReturnValue();
        final Trie<AutoCompleteType> possibilities = autoCompleteReturnValue.getPossibilities();
        final Map<String, AutoCompleteType> possibilitiesMap = possibilities.toMap();

        final Optional<Suggestions> suggestions;
        final String newCommandLine;
        final int numPossibilities = possibilitiesMap.size();
        if (numPossibilities == 0) {
            // AutoComplete didn't give any results, return the old command line.
            suggestions = Optional.absent();
            newCommandLine = rawCommandLine;
        } else {
            final String prefix = autoCompleteReturnValue.getPrefix();
            final String autoCompleteAddition;
            if (numPossibilities == 1) {
                // TODO: Only 1 possibility, assistInfo should be updated to show it...
                // Only a single auto complete result is possible.
                // Just take the auto complete, the change will be reflected in the new command line. No suggestions.
                // Let's be helpful - depending on the autoCompleteType, add a suffix.
                final String singlePossibility = possibilitiesMap.keySet().iterator().next();
                final AutoCompleteType type = possibilitiesMap.get(singlePossibility);
                final char suffix = type.getSuffix();
                autoCompleteAddition = getAutoCompleteAddition(prefix, singlePossibility) + suffix;
                suggestions = Optional.absent();
            } else {
                // Multiple auto complete results are possible.
                // AutoComplete as much as is possible - until the longest common prefix.
                final String longestPrefix = possibilities.getLongestPrefix();
                autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);

                // Catalogue the suggestions according to their type and display them.
                suggestions = Optional.of(createAutoCompleteSuggestions(possibilitiesMap));
            }

            newCommandLine = rawCommandLine + autoCompleteAddition;
        }

        outputProcessor.displayAssistance(commandInfo, suggestions);
        return newCommandLine;
    }

    private Suggestions createAutoCompleteSuggestions(Map<String, AutoCompleteType> possibilitiesMap) {
        final SuggestionsBuilder builder = new SuggestionsBuilder();
        for (Entry<String, AutoCompleteType> entry : possibilitiesMap.entrySet()) {
            builder.addSuggestion(entry.getValue(), entry.getKey());
        }
        return builder.build();
    }

    private String getAutoCompleteAddition(String prefix, String autoCompletedPrefix) {
        return autoCompletedPrefix.substring(prefix.length());
    }

    @Override
    public String execute(String rawCommandLine) {
        // TODO: Return Optional.absent on error?
        outputProcessor.begin();
        try {
            return doExecute(rawCommandLine);
        } catch (Exception e) {
            outputProcessor.internalError(e);
        } finally {
            outputProcessor.end();
        }

        // There was an internal error, return the old command line.
        return rawCommandLine;
    }

    private String doExecute(String rawCommandLine) {
        // Split the commandLine.
        final List<String> commandLine = splitCommandLine(rawCommandLine.trim());
        if (commandLine.isEmpty()) {
            // Received a commandLine that is either empty or full of spaces.
            outputProcessor.displayEmptyLine();
            return "";
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

            // There was an error parsing the command line, return the old one.
            return rawCommandLine;
        }

        // Successfully parsed commandLine.
        // Save command in history.
        commandLineHistory.pushCommandLine(rawCommandLine);

        // Execute the command.
        executeFile(file, args);

        // Command line was successfully parsed, the new command line should be blank.
        return "";
    }

    private void executeFile(ShellFile file, CommandArgs args) {
        final Command command = file.getCommand();
        final OutputPrinterImpl outputPrinter = new OutputPrinterImpl(outputProcessor);
        try {
            command.execute(args, outputPrinter);
            // Print a generic success message.
            outputPrinter.println("Command '%s' executed successfully.", command.getName());
        } catch (ExecuteException e) {
            outputProcessor.executeError(e);
        } catch (Exception e) {
            outputPrinter.println("Command '%s' was terminated due to an unhandled exception!", command.getName());
            outputProcessor.executeUnhandledException(e);
        }
    }

    private void handleParseException(ParseException e) {
        final String errorMessage = String.format("Parse Error: %s", e.getMessage());
        outputProcessor.parseError(e.getError(), errorMessage, e.getCommandInfo());
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
}
