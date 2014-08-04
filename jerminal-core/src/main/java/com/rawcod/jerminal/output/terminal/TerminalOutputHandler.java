package com.rawcod.jerminal.output.terminal;

import com.google.common.base.Joiner;
import com.rawcod.jerminal.command.view.ShellCommandParamView;
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
        println("");
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
            println(stackTraceElement.toString());
        }
    }

    @Override
    public void displaySuggestions(List<String> suggestions) {
        final String suggestionsStr = JOINER.join(suggestions);
        final String message = String.format("Suggestions: [%s]", suggestionsStr);
        println(message);
    }

    @Override
    public void displayCommandOutput(List<String> output) {
        for (String str : output) {
            println(str);
        }
    }

    @Override
    public void displayShellEntryView(ShellEntryView shellEntryView) {
        final StringBuilder sb = new StringBuilder();
        serializeShellEntryView(sb, shellEntryView, 0);
        println(sb.toString());
    }

    @Override
    public void displayShellCommandView(ShellCommandView shellCommandView) {
        final StringBuilder sb = new StringBuilder();
        serializeShellCommandView(sb, shellCommandView);
        println(sb.toString());
    }

    private void println(String message) {
        terminal.print(message);
    }

    private void printError(String message) {
        terminal.printError(message);
    }

    private void serializeShellEntryView(StringBuilder sb,
                                         ShellEntryView entry,
                                         int depth) {
        final boolean directory = entry.isDirectory();

        // Print root
        if (directory) {
            sb.append('[');
        }
        sb.append(entry.getName());
        if (directory) {
            sb.append(']');
        }

        if (!directory) {
            sb.append(" : ");
            sb.append(entry.getDescription());
        }
        sb.append('\n');

        // Print children
        if (directory) {
            for (ShellEntryView child : entry.getChildren()) {
                sb.append('|');
                appendDepthSpaces(sb, depth + 1);
                serializeShellEntryView(sb, child, depth + 1);
            }
        }
    }

    private void serializeShellCommandView(StringBuilder sb, ShellCommandView command) {
        sb.append(command.getName());
        sb.append(" : ");
        sb.append(command.getDescription());
        sb.append('\n');

        for (ShellCommandParamView paramView : command.getParams()) {
            appendDepthSpaces(sb, 1);
            serializedShellCommandParamView(sb, paramView);
            sb.append('\n');
        }
    }

    private void serializedShellCommandParamView(StringBuilder sb, ShellCommandParamView param) {
        sb.append(param.getExternalForm());
        sb.append(" - ");
        sb.append(param.getDescription());
    }

    private void appendDepthSpaces(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
    }
}
