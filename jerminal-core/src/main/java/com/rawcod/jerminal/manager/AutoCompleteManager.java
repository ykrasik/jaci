package com.rawcod.jerminal.manager;

import com.rawcod.jerminal.command.param.ParamParseContext;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValue;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.path.AutoCompletePathReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.path.AutoCompletePathReturnValueSuccess;

import java.util.*;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:23
 */
public class AutoCompleteManager {
    private final FileSystemManager fileSystemManager;

    public AutoCompleteManager(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }

    public AutoCompleteReturnValue autoComplete(List<String> commandLine, ShellDirectory currentDirectory) {
        // The first arg of the commandLine must be the command.
        final String commandArg = commandLine.get(0);

        // If we only have 1 arg, we are trying to autoComplete the command.
        // Otherwise, the first arg is expected to be a valid command and we are autoCompleting its' args.
        if (commandLine.size() == 1) {
            // The first arg is the only arg on the commandLine, autoComplete command.
            return autoCompleteCommand(commandArg, currentDirectory);
        }

        // The first arg is not the only arg on the commandLine, it is expected to be a valid command.
        final ParsePathReturnValue returnValue = fileSystemManager.parseCommandFromPath(commandArg, currentDirectory);
        if (returnValue.isFailure()) {
            // Couldn't parse the command successfully.
            return AutoCompleteReturnValue.failureFrom(returnValue.getFailure());
        }

        // AutoComplete the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final ParamParseContext context = new ParamParseContext(fileSystemManager);
        final ShellCommand command = (ShellCommand) returnValue.getSuccess().getPath();
        final List<String> args = commandLine.subList(1, commandLine.size());
        return command.getParamManager().autoCompleteArgs(args, context);
    }

    @SuppressWarnings("unchecked")
    private AutoCompleteReturnValue autoCompleteCommand(String commandArg, ShellDirectory currentDirectory) {
        final AutoCompletePathReturnValue returnValue = fileSystemManager.autoCompleteCommandFromPath(commandArg, currentDirectory);
        if (returnValue.isFailure()) {
            return AutoCompleteReturnValue.failureFrom(returnValue.getFailure());
        }

        final AutoCompletePathReturnValueSuccess success = returnValue.getSuccess();
        final List<? extends ShellDirectory> path = success.getPath();
        final ShellSuggestion suggestion = success.getSuggestion();

        final List<String> possibilities = suggestion.getPossibilities();
        if (possibilities.isEmpty()) {
            // A successful autoComplete must have 1 or more possibilities.
            return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.INTERNAL_ERROR)
                .setMessage("Internal error: AutoCompletePath returned success, but possibilities list is empty!")
                .build();
        }

        if (possibilities.size() == 1) {
            final String possibility = possibilities.get(0);
            final ShellDirectory lastDirectory = path.get(path.size() - 1);
            final ParseEntryReturnValue parseReturnValue = fileSystemManager.parseCommand(lastDirectory, possibility);
            if (returnValue.isFailure()) {
                return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.INTERNAL_ERROR)
                    .setMessageFormat(
                        "Internal error: AutoCompletePath suggested only 1 possible possibility, " +
                            "but it is not accessible from the path! currentDir='%s', possibility='%s", lastDirectory, possibility)
                    .build();
            }

            // Only 1 possibility available, we can use it.
            final List<ShellEntry> newPath = new ArrayList<ShellEntry>(path);
            newPath.add(parseReturnValue.getSuccess().getEntry());

            // There is only 1 possibility for this command to be autoCompleted,
            // we use this single possibility and add a space.
            // Don't add a fileSystem delimiter to the end of the path.
            final String newPathStr = fileSystemManager.serializePath(newPath, false) + ' ';
            return AutoCompleteReturnValue.successSingle(newPathStr);
        } else {
            // There are a few possibilities for this command to be autoCompleted.
            // Serialize the path and add a fileSystem delimiter at the end.
            // Then append the longest common prefix, but don't add a space.
            final String longestPrefix = suggestion.getLongestPrefix();
            final String newPathStr = fileSystemManager.serializePath((List<ShellEntry>) path, true) + longestPrefix;
            return AutoCompleteReturnValue.successMultiple(newPathStr, possibilities);
        }
    }
}
