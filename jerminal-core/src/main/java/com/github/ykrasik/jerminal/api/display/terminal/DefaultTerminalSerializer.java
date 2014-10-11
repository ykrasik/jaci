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

package com.github.ykrasik.jerminal.api.display.terminal;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.ParamAndValue;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
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
    public String serializeDirectory(ShellDirectory directory) {
        final StringBuilder sb = new StringBuilder();
        doSerializeDirectory(sb, directory, 0);
        return sb.toString();
    }

    private void doSerializeDirectory(StringBuilder sb, ShellDirectory directory, int depth) {
        appendDepthSpaces(sb, depth);

        // Append root.
        sb.append('[');
        sb.append(directory.getName());
        sb.append(']');
        sb.append('\n');

        for (ShellDirectory childDirectory : directory.getDirectories()) {
            doSerializeDirectory(sb, childDirectory, depth + 1);
        }

        for (Command command : directory.getCommands()) {
            doSerializeCommand(sb, command, depth + 1, false);
        }
    }

    @Override
    public String serializeCommand(Command command) {
        final StringBuilder sb = new StringBuilder();
        doSerializeCommand(sb, command, 0, true);
        return sb.toString();
    }

    private void doSerializeCommand(StringBuilder sb, Command command, int depth, boolean parameters) {
        appendDepthSpaces(sb, depth);

        sb.append(command.getName());
        sb.append(" : ");
        sb.append(command.getDescription());
        sb.append('\n');

        if (parameters) {
            for (CommandParam param : command.getParams()) {
                doSerializeCommandParam(sb, param, depth + 1);
            }
        }
    }

    private void doSerializeCommandParam(StringBuilder sb, CommandParam param, int depth) {
        appendDepthSpaces(sb, depth);
        sb.append(param.getExternalForm());
        sb.append(" - ");
        sb.append(param.getDescription());
        sb.append('\n');
    }

    @Override
    public String serializeException(Exception e) {
        final StringBuilder sb = new StringBuilder();
        sb.append(e.toString());
        sb.append('\n');
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            appendDepthSpaces(sb, 1);
            sb.append(stackTraceElement.toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    private void appendDepthSpaces(StringBuilder sb, int depth) {
        sb.append('|');
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
    }
}
