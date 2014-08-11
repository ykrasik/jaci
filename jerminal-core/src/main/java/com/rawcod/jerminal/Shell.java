package com.rawcod.jerminal;

import com.google.common.base.Optional;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.output.OutputProcessor;
import com.rawcod.jerminal.returnvalue.autocomplete.*;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue.ParseReturnValueSuccess;
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
        final AutoCompleteReturnValue returnValue = doAutoComplete(commandLine);
        if (returnValue.isSuccess()) {
            final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
            handleAutoCompleteSuccess(success, rawCommandLine);
        } else {
            final AutoCompleteReturnValueFailure failure = returnValue.getFailure();
            outputProcessor.autoCompleteFailure(failure);
        }
    }

    private AutoCompleteReturnValue doAutoComplete(List<String> commandLine) {
        // The first arg of the commandLine must be a path to a command.
        final String rawPath = commandLine.get(0);

        // If we only have 1 arg, we are trying to autoComplete a path to a command.
        // Otherwise, the first arg is expected to be a valid command and we are autoCompleting its' args.
        if (commandLine.size() == 1) {
            // The first arg is the only arg on the commandLine, autoComplete path.
            return fileSystem.autoCompletePath(rawPath);
        }

        // The first arg is not the only arg on the commandLine,
        // it is expected to be a valid path to a command.
        final ParseEntryReturnValue returnValue = fileSystem.parsePathToCommand(rawPath);
        if (returnValue.isFailure()) {
            // Couldn't parse the command successfully.
            return AutoCompleteErrors.parseError(returnValue.getFailure());
        }

        // AutoComplete the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final ParseParamContext context = new ParseParamContext(fileSystem);
        final ShellCommand command = returnValue.getSuccess().getEntry().getAsCommand();
        final List<String> args = commandLine.subList(1, commandLine.size());
        return command.getParamManager().autoCompleteArgs(args, context);
    }

    private void handleAutoCompleteSuccess(AutoCompleteReturnValueSuccess success, String rawCommandLine) {
        final String prefix = success.getPrefix();
        final Trie<AutoCompleteType> possibilities = success.getPossibilities();
        final Map<String, AutoCompleteType> possibilitiesMap = possibilities.toMap();
        final int numPossibilities = possibilitiesMap.size();
        if (numPossibilities == 0) {
            final AutoCompleteReturnValueFailure failure = AutoCompleteErrors.noPossibleValuesForPrefix(prefix).getFailure();
            outputProcessor.autoCompleteFailure(failure);
            return;
        }

        final String autoCompleteAddition;
        final Optional<Suggestions> suggestions;
        if (numPossibilities == 1) {
            // Only a single word is possible.
            final String singlePossibility = possibilitiesMap.keySet().iterator().next();
            autoCompleteAddition = getAutoCompleteAddition(prefix, singlePossibility);
            suggestions = Optional.absent();
        } else {
            // Multiple words are possible.
            // AutoComplete as much as is possible - until the longest common prefix.
            final String longestPrefix = possibilities.getLongestPrefix();
            autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);

            // Catalogue the suggestions according to their type.
            final Suggestions autoCompleteSuggestions = new Suggestions();
            for (Entry<String, AutoCompleteType> entry : possibilitiesMap.entrySet()) {
                autoCompleteSuggestions.addSuggestion(entry.getValue(), entry.getKey());
            }
            suggestions = Optional.of(autoCompleteSuggestions);
        }

        final String newCommandLine = rawCommandLine + autoCompleteAddition;
        outputProcessor.autoCompleteSuccess(newCommandLine, suggestions);
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

        // Save command in history
        commandHistory.pushCommand(rawCommandLine);

        // Parse commandLine.
        final ParseReturnValue parseReturnValue = parseCommandLine(commandLine);
        if (parseReturnValue.isFailure()) {
            outputProcessor.parseFailure(parseReturnValue.getFailure());
            return;
        }

        // Execute the command.
        final ExecuteReturnValue executeReturnValue = doExecute(parseReturnValue.getSuccess());
        if (executeReturnValue.isSuccess()) {
            final ExecuteReturnValueSuccess success = executeReturnValue.getSuccess();
            outputProcessor.executeSuccess(success);
        } else {
            final ExecuteReturnValueFailure failure = executeReturnValue.getFailure();
            outputProcessor.executeFailure(failure);
        }
    }

    private ExecuteReturnValue doExecute(ParseReturnValueSuccess success) {
        final ShellCommand command = success.getCommand();
        final CommandArgs args = success.getArgs();
        return command.execute(args);
    }

    private ParseReturnValue parseCommandLine(List<String> commandLine) {
        // The first arg of the commandLine must be a path to a command.
        final String rawPath = commandLine.get(0);

        // Parse the path to the command.
        final ParseEntryReturnValue parseCommandReturnValue = fileSystem.parsePathToCommand(rawPath);
        if (parseCommandReturnValue.isFailure()) {
            // Failed to parse the command.
            return ParseReturnValue.failure(parseCommandReturnValue.getFailure());
        }
        final ShellCommand command = parseCommandReturnValue.getSuccess().getEntry().getAsCommand();

        // Parse the command args.
        // The command args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        final ParseParamContext context = new ParseParamContext(fileSystem);
        final ParseCommandArgsReturnValue parseArgsReturnValue = command.getParamManager().parseCommandArgs(args, context);
        if (parseArgsReturnValue.isFailure()) {
            return ParseReturnValue.failure(parseArgsReturnValue.getFailure());
        }

        final CommandArgs parsedArgs = parseArgsReturnValue.getSuccess().getArgs();
        return ParseReturnValue.success(command, parsedArgs);
    }
}
