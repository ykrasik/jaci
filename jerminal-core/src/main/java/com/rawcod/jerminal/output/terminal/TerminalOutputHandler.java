package com.rawcod.jerminal.output.terminal;

import com.google.common.base.Joiner;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.output.OutputHandler;
import com.rawcod.jerminal.returnvalue.execute.ExecuteError;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 05/08/2014
 * Time: 00:28
 */
public class TerminalOutputHandler implements OutputHandler {
    private static final Joiner JOINER = Joiner.on(',').skipNulls();

    protected final Terminal terminal;

    public TerminalOutputHandler(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void clearCommandLine() {
        terminal.clearCommandLine();
    }

    @Override
    public void setCommandLine(String commandLine) {
        terminal.setCommandLine(commandLine);
    }

    @Override
    public void handleBlankCommandLine() {
        print("");
    }

    @Override
    public void parseError(ParseError error, String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void autoCompleteNotPossible(String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void executeError(ExecuteError error, String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void executeUnhandledException(Exception e) {
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            printError("|    " + stackTraceElement.toString());
        }
    }

    @Override
    public void displaySuggestions(List<String> directorySuggestions,
                                   List<String> commandSuggestions,
                                   List<String> paramNameSuggestions,
                                   List<String> paramValueSuggestions) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Suggestions: \n");
        appendSuggestions(sb, directorySuggestions, "Directories");
        appendSuggestions(sb, commandSuggestions, "Commands");
        appendSuggestions(sb, paramNameSuggestions, "Parameter names");
        appendSuggestions(sb, paramValueSuggestions, "Parameter values");
        print(sb.toString());
    }

    private void appendSuggestions(StringBuilder sb, List<String> suggestions, String suggestionTitle) {
        if (!suggestions.isEmpty()) {
            sb.append("|   ");
            sb.append(suggestionTitle);
            sb.append(": [");
            sb.append(JOINER.join(suggestions));
            sb.append("]\n");
        }
    }

    @Override
    public void displayCommandOutput(List<String> output) {
        final StringBuilder sb = new StringBuilder();
        for (String str : output) {
            sb.append(str);
            sb.append('\n');
        }
        print(sb.toString());
    }

    @Override
    public void displayShellEntryView(ShellEntryView shellEntryView) {
        print(DefaultViewSerializer.serializeShellEntryView(shellEntryView));
    }

    @Override
    public void displayShellCommandView(ShellCommandView shellCommandView) {
        print(DefaultViewSerializer.serializeShellCommandView(shellCommandView));
    }

    private void print(String message) {
        terminal.print(message);
    }

    private void printError(String message) {
        terminal.printError(message);
    }
}
