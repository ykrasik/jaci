package com.rawcod.jerminal.output.terminal;

import com.google.common.base.Joiner;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.output.OutputHandler;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
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

    private final Terminal terminal;

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
    public void autoCompleteError(AutoCompleteError error, String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void executeError(ExecuteError error, String errorMessage) {
        printError(errorMessage);
    }

    @Override
    public void executeUnhandledException(Exception e) {
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            printError("    " + stackTraceElement.toString());
        }
    }

    @Override
    public void displaySuggestions(List<String> suggestions) {
        final String suggestionsStr = JOINER.join(suggestions);
        final String message = String.format("Suggestions: [%s]", suggestionsStr);
        print(message);
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
