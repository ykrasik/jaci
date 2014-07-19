package com.rawcod.jerminal;

import com.google.common.base.Optional;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.ShellAutoComplete;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.manager.AutoCompleteManager;
import com.rawcod.jerminal.manager.ExecuteManager;
import com.rawcod.jerminal.manager.FileSystemManager;
import com.rawcod.jerminal.output.OutputProcessor;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellCommandHistory;

import java.util.*;

/**
* User: ykrasik
* Date: 05/01/14
*/
public class Shell {

    private final AutoCompleteManager autoCompleteManager;
    private final ExecuteManager executeManager;
    private final ShellCommandHistory commandHistory;

    private final List<OutputProcessor> outputProcessors;

    private ShellDirectory currentDirectory;

    public Shell(ShellFileSystem fileSystem, int maxCommandHistory) {
        final FileSystemManager fileSystemManager = new FileSystemManager(fileSystem);
        this.autoCompleteManager = new AutoCompleteManager(fileSystemManager, commandManager);
        this.executeManager = new ExecuteManager(fileSystemManager, parseManager);
        this.commandHistory = new ShellCommandHistory(maxCommandHistory);

        this.outputProcessors = new ArrayList<>();
    }

    public void addOutputProcessor(OutputProcessor outputProcessor) {
        outputProcessors.add(outputProcessor);
    }

    public void clearCommandLine() {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.clearCommandLine();
            }
        });
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
            forEachOutputProcessor(new OutputProcessorTask() {
                @Override
                public void process(OutputProcessor outputProcessor) {
                    outputProcessor.setCommandLine(command);
                }
            });
            // TODO: Setting the cursor position should be done by the terminal implementation, not by the shell
//            terminal.setCommandLineCursor(command.length());
        }
    }

    public void autoComplete(String commandLine) {
        final List<String> splitCommandLine = CommandLineSplitter.splitCommandLineForAutoComplete(commandLine);

        final AutoCompleteReturnValue returnValue = autoCompleteManager.autoComplete(splitCommandLine, currentDirectory);
        if (returnValue.isSuccess()) {
            final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
            handleAutoCompleteSuccess(success);
        } else {
            final AutoCompleteReturnValueFailure failure = returnValue.getFailure();
            handleAutoCompleteFailure(failure);
        }

        final AutoCompleteReturnValue returnValue = manager.autoComplete(args);
        final ShellAutoComplete autoComplete = returnValue.getAutoComplete();
        if (returnValue.isSuccess()) {
            handleAutoComplete(commandLine, splitCommandLine, returnValue);
        } else {
            displayError(returnValue.getErrorMessage());
            displayUsage(returnValue.getUsage());
            displaySuggestions(autoComplete);
        }
    }

    public void execute(String commandLine) {
        if (commandLine.isEmpty()) {
            print("");
            return;
        }

        // Save command in history
        commandHistory.pushCommand(commandLine);

        final List<String> splitCommandLine = CommandLineSplitter.splitCommandLineForExecute(commandLine);

        final ExecuteReturnValue returnValue = executeManager.execute(splitCommandLine, currentDirectory);
        if (returnValue.isSuccess()) {
            final ExecuteReturnValueSuccess success = returnValue.getSuccess();
            handleExecuteSuccess(success);
        } else {
            final ExecuteReturnValueFailure failure = returnValue.getFailure();
            handleExecuteFailure(failure);
        }
    }

    private ParseReturnValue<?> parseCommandLine(String commandLine,
                                                      Queue<Object> parsedArgs,
                                                      Set<String> flags) {
        final String trimmedCommandLine = commandLine.trim();
        final Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1)));
        return manager.parse(args, parsedArgs, flags);
    }



    private void handleAutoComplete(String commandLine,
                                    Deque<String> splitCommandLine,
                                    AutoCompleteReturnValue returnValue) {
        final ShellAutoComplete autoComplete = returnValue.getAutoComplete();
        final List<String> suggestions = autoComplete.getSuggestions();
        if (!suggestions.isEmpty()) {
            if (suggestions.size() == 1) {
                // Only 1 possible autoComplete for the last arg.
                // Replace the last arg with the autoCompleted arg.
                // The commandLine should be args + space.
                splitCommandLine.pollLast();
                splitCommandLine.addLast(suggestions.get(0));
                final boolean trailingSpace = returnValue.isTrailingSpace();
                setAutoCompletedCommandLine(commandLine, splitCommandLine, trailingSpace);
            } else {
                // Multiple auto-complete suggestions.
                // The new commandLine in this case should be the parsedArgs + longestPrefix.
                // No trailing space for the commandLine.
                splitCommandLine.pollLast();
                splitCommandLine.addLast(autoComplete.getLongestPrefix());
                setAutoCompletedCommandLine(commandLine, splitCommandLine, false);
                displaySuggestions(autoComplete);
            }
        }
    }

    private void setAutoCompletedCommandLine(String oldCommandLine,
                                             Queue<String> args,
                                             boolean trailingSpace) {
        final StringBuilder sb = new StringBuilder();

        // Append all args except the last one, which is
        for (String arg : args) {
            sb.append(arg);
            sb.append(' ');
        }
        if (!trailingSpace) {
            sb.delete(sb.length() - 1, sb.length());
        }

        final String newCommandLine = sb.toString();
        if (!newCommandLine.equals(oldCommandLine)) {
            // Only set a new commandLine if something actually changed
            terminal.setCommandLine(newCommandLine);
            terminal.setCommandLineCursor(newCommandLine.length());
        }
    }

    private void print(String message) {
        terminal.displayMessage(message);
    }

    private void displayError(String errorMessage) {
        if (errorMessage != null) {
            terminal.displayError(errorMessage);
        }
    }

    private void displayUsage(String usage) {
        if (usage != null) {
            terminal.displayUsage(usage);
        }
    }

    private void displaySuggestions(ShellAutoComplete autoComplete) {
        final List<String> suggestions = autoComplete.getSuggestions();
        if (!suggestions.isEmpty()) {
            terminal.displaySuggestions(suggestions);
        }
    }

    private interface OutputProcessorTask {
        void process(OutputProcessor outputProcessor);
    }

    private void handleAutoCompleteSuccess(final AutoCompleteReturnValueSuccess success) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processAutoCompleteSuccess(success);
            }
        });
    }

    private void handleAutoCompleteFailure(final AutoCompleteReturnValueFailure failure) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processAutoCompleteFailure(failure);
            }
        });
    }

    private void handleExecuteSuccess(final ExecuteReturnValueSuccess success) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processExecuteOutputSuccess(success);
            }
        });
    }

    private void handleExecuteFailure(final ExecuteReturnValueFailure failure) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processExecuteOutputFailure(failure);
            }
        });
    }

    private void forEachOutputProcessor(OutputProcessorTask task) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            task.process(outputProcessor);
        }
    }
}
