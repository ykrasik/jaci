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
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.output.OutputProcessor;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.command.OutputBufferImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.google.common.base.Optional;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.github.ykrasik.jerminal.internal.returnvalue.Suggestions;

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

    public ShellImpl(OutputProcessor outputProcessor,
                     ShellFileSystem fileSystem,
                     CommandLineHistory commandLineHistory,
                     String welcomeMessage) {
        this.outputProcessor = outputProcessor;
        this.fileSystem = fileSystem;
        this.commandLineHistory = commandLineHistory;

        // Init.
        outputProcessor.displayWelcomeMessage(welcomeMessage);
    }

    @Override
    public Optional<String> getPrevCommandLineFromHistory() {
        return commandLineHistory.getPrevCommandLine();
    }

    @Override
    public Optional<String> getNextCommandLineFromHistory() {
        return commandLineHistory.getNextCommandLine();
    }

    // TODO: AutoCompleting should give more assistance: Command info etc.
    // TODO: Rename this to assist.
    @Override
    public String autoComplete(String rawCommandLine) {
        // Split the commandLine for autoComplete.
        final List<String> commandLine = splitCommandLineForAutoComplete(rawCommandLine);

        // Do the actual autoCompletion.
        try {
            final AutoCompleteReturnValue returnValue = doAutoComplete(commandLine);
            return getNewCommandLine(returnValue, rawCommandLine);
        } catch (ParseException e) {
            handleParseException(e);

            // There was an error parsing the command line, return the old one.
            return rawCommandLine;
        }
    }

    private AutoCompleteReturnValue doAutoComplete(List<String> commandLine) throws ParseException {
        // The first arg of the commandLine must be a path to a command.
        final String rawPath = commandLine.get(0);

        // If we only have 1 arg, we are trying to autoComplete a path to a command.
        // Otherwise, the first arg is expected to be a valid command and we are autoCompleting its' args.
        if (commandLine.size() == 1) {
            // The first arg is the only arg on the commandLine, autoComplete path.
            return fileSystem.autoCompletePath(rawPath);
        }

        // The first arg is not the only arg on the commandLine, it is expected to be a valid path to a command.
        final ShellCommand command = fileSystem.parsePathToCommand(rawPath);

        // AutoComplete the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        return command.autoCompleteArgs(args);
    }

    private String getNewCommandLine(AutoCompleteReturnValue returnValue, String rawCommandLine) {
        final String prefix = returnValue.getPrefix();
        final Trie<AutoCompleteType> possibilities = returnValue.getPossibilities();
        final Map<String, AutoCompleteType> possibilitiesMap = possibilities.toMap();
        final int numPossibilities = possibilitiesMap.size();
        if (numPossibilities == 0) {
            final String message = String.format("AutoComplete Error: Not possible for prefix '%s'", prefix);
            outputProcessor.autoCompleteNotPossible(message);

            // AutoComplete didn't give any results, return the old command line.
            return rawCommandLine;
        }

        final String autoCompleteAddition;
        if (numPossibilities == 1) {
            // Only a single word is possible.
            // Let's be helpful - depending on the autoCompleteType, add a suffix.
            final String singlePossibility = possibilitiesMap.keySet().iterator().next();
            final AutoCompleteType type = possibilitiesMap.get(singlePossibility);
            final char suffix = getSinglePossibilitySuffix(type);
            autoCompleteAddition = getAutoCompleteAddition(prefix, singlePossibility) + suffix;
        } else {
            // Multiple words are possible.
            // AutoComplete as much as is possible - until the longest common prefix.
            final String longestPrefix = possibilities.getLongestPrefix();
            autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);

            // Catalogue the suggestions according to their type and display them.
            final Suggestions suggestions = createAutoCompleteSuggestions(possibilitiesMap);
            displaySuggestions(suggestions);
        }

        // The new command line is the old command line + autoCompleteAddition.
        return rawCommandLine + autoCompleteAddition;
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
        final Suggestions autoCompleteSuggestions = new Suggestions();
        for (Entry<String, AutoCompleteType> entry : possibilitiesMap.entrySet()) {
            autoCompleteSuggestions.addSuggestion(entry.getValue(), entry.getKey());
        }
        return autoCompleteSuggestions;
    }

    private String getAutoCompleteAddition(String prefix, String autoCompletedPrefix) {
        return autoCompletedPrefix.substring(prefix.length());
    }

    @Override
    public String execute(String rawCommandLine) {
        // Split the commandLine.
        final List<String> commandLine = splitCommandLineForExecute(rawCommandLine);
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
        final OutputBufferImpl output = new OutputBufferImpl();
        try {
            command.execute(args, output);
            if (output.isEmpty()) {
                // Add a default message if the command didn't print anything.
                output.println("Command '%s' executed successfully.", command.getName());
            }
            displayCommandOutput(output);
        } catch (ExecuteException e) {
            displayCommandOutput(output);
            outputProcessor.executeError(e.getMessage());
        } catch (Exception e) {
            output.println("Command '%s' terminated with an unhandled exception!", command.getName());
            displayCommandOutput(output);
            outputProcessor.executeUnhandledException(e);
        }
        // TODO: Display command output in a finally block?
        // Command line was successfully parsed, the new command line should be blank.
        return "";
    }

    private void handleParseException(ParseException e) {
        final String errorMessage = String.format("Parse Error: %s", e.getMessage());
        outputProcessor.parseError(e.getError(), errorMessage);
        displaySuggestionsIfApplicable(e.getSuggestions());
    }

    private void displaySuggestionsIfApplicable(Optional<Suggestions> suggestions) {
        if (suggestions.isPresent()) {
            displaySuggestions(suggestions.get());
        }
    }

    private void displaySuggestions(Suggestions suggestions) {
        final List<String> directorySuggestions = suggestions.getDirectorySuggestions();
        final List<String> commandSuggestions = suggestions.getCommandSuggestions();
        final List<String> paramNameSuggestions = suggestions.getParamNameSuggestions();
        final List<String> paramValueSuggestions = suggestions.getParamValueSuggestions();

        outputProcessor.displaySuggestions(
            directorySuggestions,
            commandSuggestions,
            paramNameSuggestions,
            paramValueSuggestions
        );
    }

    private void displayCommandOutput(OutputBufferImpl outputBuffer) {
        if (!outputBuffer.isEmpty()) {
            final List<String> output = outputBuffer.getOutputBuffer();
            outputProcessor.displayCommandOutput(output);
        }
    }

    private List<String> splitCommandLineForAutoComplete(String commandLine) {
        // Only remove leading spaces, trailing spaces have a significant meaning.
        final String trimmedCommandLine = commandLine.startsWith(" ") ?
            ARGS_PATTERN.matcher(commandLine).replaceFirst("") :
            commandLine;
        return Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1));
    }

    private List<String> splitCommandLineForExecute(String commandLine) {
        final String trimmedCommandLine = commandLine.trim();
        return Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1));
    }
}
