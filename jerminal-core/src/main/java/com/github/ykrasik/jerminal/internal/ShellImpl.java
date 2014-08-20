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
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.output.OutputProcessor;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.command.OutputPrinterImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.internal.returnvalue.*;
import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * An implementation for a {@link Shell}.
 *
 * @author Yevgeny Krasik
 */
public class ShellImpl implements Shell {
    // A pattern that regards sequential spaces as a single space
    private static final Pattern ARGS_PATTERN = Pattern.compile("[ ]+");

    private final OutputProcessor outputProcessor;
    private final ShellFileSystem fileSystem;
    private final CommandLineHistory commandLineHistory;
    private final OutputPrinter outputPrinter;

    public ShellImpl(OutputProcessor outputProcessor,
                     ShellFileSystem fileSystem,
                     CommandLineHistory commandLineHistory,
                     String welcomeMessage) {
        this.outputProcessor = outputProcessor;
        this.fileSystem = fileSystem;
        this.commandLineHistory = commandLineHistory;
        this.outputPrinter = new OutputPrinterImpl(outputProcessor);

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
        outputProcessor.begin();
        try {
            // Split the commandLine for autoComplete.
            // Only remove leading spaces, trailing spaces have a significant meaning.
            final String trimmedCommandLine = rawCommandLine.startsWith(" ") ?
                ARGS_PATTERN.matcher(rawCommandLine).replaceFirst("") :
                rawCommandLine;
            final List<String> commandLine = Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1));

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

    // FIXME: Assist info should be printed no matter what.
    // FIXME: I do still want the old errors - no more params, etc.
    private AssistReturnValue doAssist(List<String> commandLine) throws ParseException {
        // The first arg of the commandLine must be a path to a command.
        final String rawPath = commandLine.get(0);

        // If we only have 1 arg, we are trying to autoComplete a path to a command.
        // Otherwise, the first arg is expected to be a valid command and we are autoCompleting its' args.
        if (commandLine.size() == 1) {
            // The first arg is the only arg on the commandLine, autoComplete path.
            final AutoCompleteReturnValue autoCompleteReturnValue = fileSystem.autoCompletePath(rawPath);
            return new AssistReturnValue(Optional.<CommandInfo>absent(), autoCompleteReturnValue);
        }

        // The first arg is not the only arg on the commandLine, it is expected to be a valid path to a command.
        final ShellCommand command = fileSystem.parsePathToCommand(rawPath);

        // Provide assistance with the command parameters.
        // The command args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        return command.assistArgs(args);
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
                final char suffix = getSinglePossibilitySuffix(type);
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

    private char getSinglePossibilitySuffix(AutoCompleteType type) {
        // FIXME: Where to place these constants?
        switch (type) {
            case DIRECTORY: return '/';
            case COMMAND: return ' ';
            case COMMAND_PARAM_NAME: return '=';
            case COMMAND_PARAM_FLAG: // Fallthrough
            case COMMAND_PARAM_VALUE: return ' ';
            default: throw new ShellException("Invalid AutoCompleteType: %s", type);
        }
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
        final List<String> commandLine = Arrays.asList(ARGS_PATTERN.split(rawCommandLine.trim(), -1));
        if (commandLine.size() == 1 && commandLine.get(0).isEmpty()) {
            // Received a commandLine that is either empty or full of spaces.
            outputProcessor.displayEmptyLine();
            return "";
        }

        // Parse commandLine.
        final ShellCommand command;
        final CommandArgs args;
        try {
            // The first arg of the commandLine must be a path to a command.
            final String pathToCommand = commandLine.get(0);
            command = fileSystem.parsePathToCommand(pathToCommand);

            // Parse the command args.
            // The command args start from the 2nd commandLine element (the first was the command).
            final List<String> rawArgs = commandLine.subList(1, commandLine.size());
            args = command.parseCommandArgs(rawArgs);
        } catch (ParseException e) {
            handleParseException(e);

            // There was an error parsing the command line, return the old one.
            return rawCommandLine;
        }

        // Successfully parsed commandLine.
        // Save command in history.
        commandLineHistory.pushCommandLine(rawCommandLine);

        // Execute the command.
        executeParsedCommand(command, args);

        // Command line was successfully parsed, the new command line should be blank.
        return "";
    }

    private void executeParsedCommand(ShellCommand command, CommandArgs args) {
        try {
            command.execute(args, outputPrinter);
            // Print a generic success message
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
}
