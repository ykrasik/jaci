/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.output.terminal;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.ParamAndValue;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.parameter.view.ShellCommandParamView;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import java.util.List;

/**
 * A default implementation for a {@link TerminalSerializer}.
 *
 * @author Yevgeny Krasik
 */
public class DefaultTerminalSerializer implements TerminalSerializer {
    private static final Joiner JOINER = Joiner.on(',').skipNulls();

    @Override
    public String getEmptyLine() {
        return "\n";
    }

    @Override
    public String serializeCommandInfo(CommandInfo commandInfo) {
        final String commandName = commandInfo.getCommandName();
        final List<ParamAndValue> paramAndValues = commandInfo.getParamAndValues();
        final int currentParamIndex = commandInfo.getCurrentParamIndex();

        final StringBuilder sb = new StringBuilder();
        sb.append(commandName);
        sb.append(' ');
        appendParams(sb, paramAndValues, currentParamIndex);
        return sb.toString();
    }

    private void appendParams(StringBuilder sb, List<ParamAndValue> paramAndValues, int currentParamIndex) {
        // Surround the current param being parsed with >>> <<<
        for (int i = 0; i < paramAndValues.size(); i++) {
            if (currentParamIndex == i) {
                sb.append(">>> ");
            }

            final ParamAndValue param = paramAndValues.get(i);
            sb.append(param.getParam().getExternalForm());

            final Optional<String> value = param.getValue();
            if (value.isPresent()) {
                sb.append('=');
                sb.append(value.get());
            } else {
                if (currentParamIndex == i) {
                    sb.append(" <<<");
                }
            }

            sb.append(' ');
        }
    }

    @Override
    public String serializeSuggestions(Suggestions suggestions) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Suggestions: \n");
        appendSuggestions(sb, suggestions.getDirectorySuggestions(), "Directories");
        appendSuggestions(sb, suggestions.getCommandSuggestions(), "Commands");
        appendSuggestions(sb, suggestions.getParamNameSuggestions(), "Parameter names");
        appendSuggestions(sb, suggestions.getParamValueSuggestions(), "Parameter values");
        return sb.toString();
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
    public String serializeShellEntryView(ShellEntryView shellEntryView) {
        final StringBuilder sb = new StringBuilder();
        doSerializeShellEntryView(sb, shellEntryView, 0);
        return sb.toString();
    }

    private void doSerializeShellEntryView(StringBuilder sb, ShellEntryView shellEntryView, int depth) {
        final boolean directory = shellEntryView.isDirectory();

        // Append root.
        if (directory) {
            sb.append('[');
        }
        sb.append(shellEntryView.getName());
        if (directory) {
            sb.append(']');
        }

        if (!directory) {
            sb.append(" : ");
            sb.append(shellEntryView.getDescription());
        }
        sb.append('\n');

        // Append children.
        if (directory) {
            for (ShellEntryView child : shellEntryView.getChildren()) {
                sb.append('|');
                appendDepthSpaces(sb, depth + 1);
                doSerializeShellEntryView(sb, child, depth + 1);
            }
        }
    }

    @Override
    public String serializeShellCommandView(ShellCommandView shellCommandView) {
        final StringBuilder sb = new StringBuilder();
        sb.append(shellCommandView.getName());
        sb.append(" : ");
        sb.append(shellCommandView.getDescription());
        sb.append('\n');

        for (ShellCommandParamView paramView : shellCommandView.getParams()) {
            appendDepthSpaces(sb, 1);
            serializedShellCommandParamView(sb, paramView);
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public String serializeException(Exception e) {
        final StringBuilder sb = new StringBuilder();
        sb.append(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            sb.append("|    ");
            sb.append(stackTraceElement.toString());
        }
        return sb.toString();
    }

    private void appendDepthSpaces(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
    }

    private void serializedShellCommandParamView(StringBuilder sb, ShellCommandParamView param) {
        sb.append(param.getExternalForm());
        sb.append(" - ");
        sb.append(param.getDescription());
    }
}
