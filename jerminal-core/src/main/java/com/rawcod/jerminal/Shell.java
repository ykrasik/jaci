package com.rawcod.jerminal;

import com.google.common.base.Optional;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.output.OutputHandler;
import com.rawcod.jerminal.output.OutputProcessor;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue.ParsePathReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue.ParseReturnValueSuccess;
import com.rawcod.jerminal.shell.ShellCommandHistory;
import com.rawcod.jerminal.util.CommandLineUtils;

import java.util.List;

/**
* User: ykrasik
* Date: 05/01/14
*/
public class Shell {
    private final FileSystemManager fileSystemManager;
    private final ShellCommandHistory commandHistory;

    private final OutputHandler outputHandler;

    public Shell(ShellFileSystem fileSystem, int maxCommandHistory) {
        this.fileSystemManager = new FileSystemManager(fileSystem);
        this.commandHistory = new ShellCommandHistory(maxCommandHistory);

        this.outputHandler = new OutputHandler();
    }

    public void addOutputProcessor(OutputProcessor outputProcessor) {
        outputHandler.add(outputProcessor);
    }

    public void autoComplete(String rawCommandLine) {
        // Split the commandLine for autoComplete.
        final List<String> commandLine = CommandLineUtils.splitCommandLineForAutoComplete(rawCommandLine);

        // Do the actual autoCompletion.
        final AutoCompleteReturnValue returnValue = doAutoComplete(commandLine);
        if (returnValue.isSuccess()) {
            final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
            final String autoCompleteAddition = success.getAutoCompleteAddition();
            if (!autoCompleteAddition.isEmpty()) {
                final String newCommandLine = rawCommandLine + autoCompleteAddition;
                outputHandler.setCommandLine(newCommandLine);
            }
            final List<String> possibilities = success.getSuggestions();
            if (possibilities.size() > 1) {
                outputHandler.displayAutoCompleteSuggestions(possibilities);
            }
        } else {
            final AutoCompleteReturnValueFailure failure = returnValue.getFailure();
            outputHandler.handleAutoCompleteFailure(failure);
        }
    }

    private AutoCompleteReturnValue doAutoComplete(List<String> commandLine) {
        // The first arg of the commandLine must be a path to a command.
        final String rawPath = commandLine.get(0);

        // If we only have 1 arg, we are trying to autoComplete a path to a command.
        // Otherwise, the first arg is expected to be a valid command and we are autoCompleting its' args.
        if (commandLine.size() == 1) {
            // The first arg is the only arg on the commandLine, autoComplete path to command.
            return fileSystemManager.autoCompletePath(rawPath);
        }

        // The first arg is not the only arg on the commandLine,
        // it is expected to be a valid path to a command.
        final ParsePathReturnValue returnValue = fileSystemManager.parsePathToCommand(rawPath);
        if (returnValue.isFailure()) {
            // Couldn't parse the command successfully.
            return AutoCompleteErrors.parseError(returnValue.getFailure());
        }

        // AutoComplete the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final ParamParseContext context = new ParamParseContext(fileSystemManager);
        final ShellCommand command = (ShellCommand) returnValue.getSuccess().getLastEntry();
        final List<String> args = commandLine.subList(1, commandLine.size());
        return command.getParamManager().autoCompleteArgs(args, context);
    }

    public void execute(String rawCommandLine) {
        if (rawCommandLine.isEmpty()) {
            outputHandler.println("");
            return;
        }

        // Save command in history
        commandHistory.pushCommand(rawCommandLine);

        // Split the commandLine for autoComplete.
        final List<String> commandLine = CommandLineUtils.splitCommandLineForExecute(rawCommandLine);

        // Execute the command.
        final ExecuteReturnValue executeReturnValue = doExecute(commandLine);
        if (executeReturnValue.isSuccess()) {
            final ExecuteReturnValueSuccess success = executeReturnValue.getSuccess();
            final String output = success.getOutput();
            final Optional<Object> returnValue = success.getReturnValue();
            outputHandler.handleExecuteSuccess(output, returnValue);
        } else {
            final ExecuteReturnValueFailure failure = executeReturnValue.getFailure();
            outputHandler.handleExecuteFailure(failure);
        }
    }

    private ExecuteReturnValue doExecute(List<String> commandLine) {
        // Parse commandLine.
        final ParseReturnValue parseReturnValue = parseCommandLine(commandLine);
        if (parseReturnValue.isFailure()) {
            return ExecuteReturnValue.parseFailure(parseReturnValue.getFailure());
        }

        final ParseReturnValueSuccess parseSuccess = parseReturnValue.getSuccess();
        final ShellCommand command = parseSuccess.getCommand();
        final CommandArgs args = parseSuccess.getArgs();

        // Execute command.
        return command.execute(args);
    }

    private ParseReturnValue parseCommandLine(List<String> commandLine) {
        // The first arg of the commandLine must be a path to a command.
        final String rawPath = commandLine.get(0);

        // Parse the path to the command.
        final ParsePathReturnValue parseCommandReturnValue = fileSystemManager.parsePathToCommand(rawPath);
        if (parseCommandReturnValue.isFailure()) {
            // Failed to parse the command.
            return ParseReturnValue.failure(parseCommandReturnValue.getFailure());
        }

        final ParsePathReturnValueSuccess parseCommandSuccess = parseCommandReturnValue.getSuccess();
        final List<ShellDirectory> path = parseCommandSuccess.getPath();
        final ShellCommand command = (ShellCommand) parseCommandSuccess.getLastEntry();

        // Parse the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        final ParamParseContext context = new ParamParseContext(fileSystemManager);
        final ParseCommandArgsReturnValue parseArgsReturnValue = command.getParamManager().parseCommandArgs(args, context);
        if (parseArgsReturnValue.isFailure()) {
            return ParseReturnValue.failure(parseArgsReturnValue.getFailure());
        }

        final CommandArgs parsedArgs = parseArgsReturnValue.getSuccess().getArgs();
        return ParseReturnValue.success(path, command, parsedArgs);
    }

    public void clearCommandLine() {
        outputHandler.clearCommandLine();
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
            outputHandler.setCommandLine(command);
        }
    }
}
