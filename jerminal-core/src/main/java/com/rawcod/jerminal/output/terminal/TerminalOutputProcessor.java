package com.rawcod.jerminal.output.terminal;

import com.google.common.base.Joiner;
import com.rawcod.jerminal.command.view.ShellCommandParamView;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.output.OutputProcessor;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 05/08/2014
 * Time: 18:28
 */
public class TerminalOutputProcessor implements OutputProcessor {
    private static final Joiner JOINER = Joiner.on(',').skipNulls();

    protected final Terminal terminal;

    public TerminalOutputProcessor(Terminal terminal) {
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
    public void executeError(String errorMessage) {
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
        print(serializeShellEntryView(shellEntryView));
    }

    @Override
    public void displayShellCommandView(ShellCommandView shellCommandView) {
        print(serializeShellCommandView(shellCommandView));
    }

    protected String serializeShellEntryView(ShellEntryView entry) {
        final StringBuilder sb = new StringBuilder();
        serializeShellEntryView(sb, entry, 0);
        return sb.toString();
    }

    private void serializeShellEntryView(StringBuilder sb, ShellEntryView entry, int depth) {
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

    protected String serializeShellCommandView(ShellCommandView command) {
        final StringBuilder sb = new StringBuilder();
        serializeShellCommandView(sb, command);
        return sb.toString();
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

    private void print(String message) {
        terminal.print(message);
    }

    private void printError(String message) {
        terminal.printError(message);
    }
}
