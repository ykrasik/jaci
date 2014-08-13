package com.rawcod.jerminal;

import com.google.common.base.Optional;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.output.OutputProcessor;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.suggestion.Suggestions;
import com.rawcod.jerminal.util.CommandLineUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
* User: ykrasik
* Date: 05/01/14
*/
public class Shell {
    private final OutputProcessor outputProcessor;
    private final ShellFileSystem fileSystem;
    private final ShellCommandHistory commandHistory;

    Shell(OutputProcessor outputProcessor, ShellFileSystem fileSystem, ShellCommandHistory commandHistory) {
        this.outputProcessor = outputProcessor;
        this.fileSystem = fileSystem;
        this.commandHistory = commandHistory;
    }

    public void clearCommandLine() {
        outputProcessor.clearCommandLine();
    }

    public void showPrevCommand() {
        final Optional<String> prevCommand = commandHistory.getPrevCommand();
        doShowCommand(prevCommand);
    }

    public void showNextCommand() {
        final Optional<String> nextCommand = commandHistory.getNextCommand();
        doShowCommand(nextCommand);
    }

    private void doShowCommand(Optional<String> commandOptional) {
        if (commandOptional.isPresent()) {
            final String command = commandOptional.get();
            outputProcessor.setCommandLine(command);
        }
    }

    public void autoComplete(String rawCommandLine) {
        // Split the commandLine for autoComplete.
        final List<String> commandLine = CommandLineUtils.splitCommandLineForAutoComplete(rawCommandLine);

        // Do the actual autoCompletion.
        try {
            final AutoCompleteReturnValue returnValue = doAutoComplete(commandLine);
            handleAutoComplete(returnValue, rawCommandLine);
        } catch (ParseException e) {
            final ParseError error = e.getError();
            final String message = e.getMessage();
            final Optional<Suggestions> suggestions = e.getSuggestions();
            outputProcessor.parseError(error, message, suggestions);
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

        // The first arg is not the only arg on the commandLine,  it is expected to be a valid path to a command.
        final ShellCommand command = fileSystem.parsePathToCommand(rawPath);

        // AutoComplete the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final ParseParamContext context = new ParseParamContext(fileSystem);
        final List<String> args = commandLine.subList(1, commandLine.size());
        return command.getParamManager().autoCompleteArgs(args, context);
    }

    private void handleAutoComplete(AutoCompleteReturnValue returnValue, String rawCommandLine) {
        final String prefix = returnValue.getPrefix();
        final Trie<AutoCompleteType> possibilities = returnValue.getPossibilities();
        final Map<String, AutoCompleteType> possibilitiesMap = possibilities.toMap();
        final int numPossibilities = possibilitiesMap.size();
        if (numPossibilities == 0) {
            final String message = String.format("AutoComplete Error: Not possible for prefix '%s'", prefix);
            outputProcessor.autoCompleteNotPossible(message);
            return;
        }

        final String autoCompleteAddition;
        final Optional<Suggestions> suggestions;
        if (numPossibilities == 1) {
            // Only a single word is possible.
            // Let's be helpful - depending on the autoCompleteType, add a suffix.
            final String singlePossibility = possibilitiesMap.keySet().iterator().next();
            final AutoCompleteType type = possibilitiesMap.get(singlePossibility);
            final char suffix = getSinglePossibilitySuffix(type);
            autoCompleteAddition = getAutoCompleteAddition(prefix, singlePossibility) + suffix;
            suggestions = Optional.absent();
        } else {
            // Multiple words are possible.
            // AutoComplete as much as is possible - until the longest common prefix.
            final String longestPrefix = possibilities.getLongestPrefix();
            autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);

            // Catalogue the suggestions according to their type.
            suggestions = Optional.of(createAutoCompleteSuggestions(possibilitiesMap));
        }

        final String newCommandLine = rawCommandLine + autoCompleteAddition;
        outputProcessor.autoCompleteSuccess(newCommandLine, suggestions);
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

    public void execute(String rawCommandLine) {
        // Split the commandLine.
        final List<String> commandLine = CommandLineUtils.splitCommandLineForExecute(rawCommandLine);
        if (commandLine.size() == 1 && commandLine.get(0).isEmpty()) {
            // Received a commandLine that is either empty or full of spaces.
            outputProcessor.blankCommandLine();
            return;
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
            final ParseParamContext context = new ParseParamContext(fileSystem);
            args = command.getParamManager().parseCommandArgs(rawArgs, context);
        } catch (ParseException e) {
            outputProcessor.parseError(e.getError(), e.getMessage(), e.getSuggestions());
            return;
        }

        // Successfully parsed commandLine.
        // Save command in history.
        commandHistory.pushCommand(rawCommandLine);

        // Execute the command.
        final ExecuteReturnValue executeReturnValue = command.execute(args);
        if (executeReturnValue.isSuccess()) {
            final ExecuteReturnValueSuccess success = executeReturnValue.getSuccess();
            outputProcessor.executeSuccess(success);
        } else {
            final ExecuteReturnValueFailure failure = executeReturnValue.getFailure();
            outputProcessor.executeFailure(failure);
        }
    }
}
