package com.rawcod.jerminal.shell;

import com.rawcod.jerminal.Terminal;
import com.rawcod.jerminal.shell.entry.ShellAutoComplete;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellExecuteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

import java.util.*;
import java.util.regex.Pattern;

/**
* User: ykrasik
* Date: 05/01/14
*/
public class Shell {
    // A pattern that regards sequential spaces as a single space
    private static final Pattern ARGS_PATTERN = Pattern.compile("[ ]+");

    private final ShellManager manager;
    private final Terminal terminal;
    private final ShellCommandHistory commandHistory;

    public Shell(ShellManager manager, Terminal terminal, int maxCommandHistory) {
        this.manager = manager;
        this.terminal = terminal;
        this.commandHistory = new ShellCommandHistory(maxCommandHistory);

        manager.setTerminal(terminal);
    }

    public void clearCommandLine() {
        terminal.setCommandLine("");
        terminal.setCommandLineCursor(0);
    }

    public void showPrevCommand() {
        final String historyCommand = commandHistory.getPrevCommand();
        if (historyCommand != null) {
            terminal.setCommandLine(historyCommand);
            terminal.setCommandLineCursor(historyCommand.length());
        }
    }

    public void showNextCommand() {
        final String historyCommand = commandHistory.getNextCommand();
        if (historyCommand != null) {
            terminal.setCommandLine(historyCommand);
            terminal.setCommandLineCursor(historyCommand.length());
        }
    }

    public void execute(String commandLine) {
        if (commandLine.isEmpty()) {
            print("");
            return;
        }

        // Save command in history
        commandHistory.pushCommand(commandLine);

        // Parse arguments
        final ArrayDeque<Object> parsedArgs = new ArrayDeque<>();
        final Set<String> flags = new HashSet<>(0);
        final ShellParseReturnValue<?> parseReturnValue = parseCommandLine(commandLine, parsedArgs, flags);
        if (parseReturnValue.isSuccess()) {
            // Execute command
            print(commandLine);
            clearCommandLine();

            // Display return message
            final ShellExecuteReturnValue executeReturnValue = manager.execute(parsedArgs, flags);
            final String returnMessage = executeReturnValue.getMessage();
            if (executeReturnValue.isSuccess()) {
                if (returnMessage != null) {
                    terminal.displayCommandReturnMessage(returnMessage);
                }
            } else {
                terminal.displayError(returnMessage);
            }
        } else {
            // Print error message and suggestions.
            displayError(parseReturnValue.getErrorMessage());
            displayUsage(parseReturnValue.getUsage());
            displaySuggestions(parseReturnValue.getAutoComplete());
        }
    }

    private ShellParseReturnValue<?> parseCommandLine(String commandLine,
                                                      Queue<Object> parsedArgs,
                                                      Set<String> flags) {
        final String trimmedCommandLine = commandLine.trim();
        final Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1)));
        return manager.parse(args, parsedArgs, flags);
    }

    public void autoComplete(String commandLine) {
        final String trimmedCommandLine = trimCommandLine(commandLine);
        final Deque<String> splitCommandLine = new ArrayDeque<>(Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1)));

        // 'args' will be altered during parsing for autoCompletion, so we keep a copy of it.
        final Queue<String> args = new ArrayDeque<>(splitCommandLine);
        final ShellAutoCompleteReturnValue returnValue = manager.autoComplete(args);
        final ShellAutoComplete autoComplete = returnValue.getAutoComplete();
        if (returnValue.isSuccess()) {
            handleAutoComplete(commandLine, splitCommandLine, returnValue);
        } else {
            displayError(returnValue.getErrorMessage());
            displayUsage(returnValue.getUsage());
            displaySuggestions(autoComplete);
        }
    }

    private String trimCommandLine(String commandLine) {
        // Ignore any prefixed spaces
        return commandLine.startsWith(" ") ?
            ARGS_PATTERN.matcher(commandLine).replaceFirst("") :
            commandLine;
    }

    private void handleAutoComplete(String commandLine,
                                    Deque<String> splitCommandLine,
                                    ShellAutoCompleteReturnValue returnValue) {
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
}
