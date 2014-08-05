package com.rawcod.jerminal.output.terminal;

import com.rawcod.jerminal.command.view.ShellCommandParamView;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;

/**
 * User: ykrasik
 * Date: 05/08/2014
 * Time: 09:30
 */
public final class DefaultViewSerializer {
    private DefaultViewSerializer() {
    }

    public static String serializeShellEntryView(ShellEntryView entry) {
        final StringBuilder sb = new StringBuilder();
        serializeShellEntryView(sb, entry, 0);
        return sb.toString();
    }

    private static void serializeShellEntryView(StringBuilder sb, ShellEntryView entry, int depth) {
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

    public static String serializeShellCommandView(ShellCommandView command) {
        final StringBuilder sb = new StringBuilder();
        serializeShellCommandView(sb, command);
        return sb.toString();
    }

    private static void serializeShellCommandView(StringBuilder sb, ShellCommandView command) {
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

    private static void serializedShellCommandParamView(StringBuilder sb, ShellCommandParamView param) {
        sb.append(param.getExternalForm());
        sb.append(" - ");
        sb.append(param.getDescription());
    }

    private static void appendDepthSpaces(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
    }
}
