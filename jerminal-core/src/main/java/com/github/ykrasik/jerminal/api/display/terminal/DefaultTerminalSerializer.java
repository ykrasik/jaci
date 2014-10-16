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
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.Describable;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A default implementation for a {@link TerminalSerializer}.
 *
 * @author Yevgeny Krasik
 */
public class DefaultTerminalSerializer implements TerminalSerializer {
    private static final Joiner JOINER = Joiner.on(", ").skipNulls();
    private static final NameComparator NAME_COMPARATOR = new NameComparator();

    @Override
    public String serializeCommandInfo(CommandInfo commandInfo) {
        final Command command = commandInfo.getCommand();
        final List<Optional<String>> paramValues = commandInfo.getParamValues();
        final Optional<CommandParam> currentParam = commandInfo.getCurrentParam();

        final StringBuilder sb = new StringBuilder();
        doSerializeCommand(sb, 0, command, false, true, paramValues, currentParam);
        return sb.toString();
    }

    @Override
    public String serializeSuggestions(Suggestions suggestions) {
        final StringBuilder sb = new StringBuilder();
        appendDepthSpaces(sb, 0);
        sb.append("Suggestions: \n");
        appendSuggestions(sb, suggestions.getDirectorySuggestions(), "Directories");
        appendSuggestions(sb, suggestions.getCommandSuggestions(), "Commands");
        appendSuggestions(sb, suggestions.getParamNameSuggestions(), "Parameter names");
        appendSuggestions(sb, suggestions.getParamValueSuggestions(), "Parameter values");
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected void appendSuggestions(StringBuilder sb, List<String> suggestions, String suggestionTitle) {
        if (!suggestions.isEmpty()) {
            appendDepthSpaces(sb, 1);
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

    protected void doSerializeDirectory(StringBuilder sb, ShellDirectory directory, int depth) {
        appendDepthSpaces(sb, depth);

        // Append root.
        sb.append('[');
        sb.append(directory.getName());
        sb.append(']');
        sb.append('\n');

        final List<Command> commands = new ArrayList<>(directory.getCommands());
        Collections.sort(commands, NAME_COMPARATOR);
        for (Command command : commands) {
            doSerializeCommand(sb, depth + 1, command, true, false, Collections.<Optional<String>>emptyList(), Optional.<CommandParam>absent());
        }

        final List<ShellDirectory> directories = new ArrayList<>(directory.getDirectories());
        Collections.sort(directories, NAME_COMPARATOR);
        for (ShellDirectory childDirectory : directories) {
            doSerializeDirectory(sb, childDirectory, depth + 1);
        }
    }

    @Override
    public String serializeCommand(Command command) {
        final StringBuilder sb = new StringBuilder();
        doSerializeCommand(sb, 0, command, true, true, Collections.<Optional<String>>emptyList(), Optional.<CommandParam>absent());
        return sb.toString();
    }

    protected void doSerializeCommand(StringBuilder sb,
                                      int depth,
                                      Command command,
                                      boolean withDescription,
                                      boolean withParams,
                                      List<Optional<String>> paramValues,
                                      Optional<CommandParam> currentParam) {
        appendDepthSpaces(sb, depth);

        // Append name : description
        sb.append(command.getName());
        if (withDescription) {
            sb.append(" : ");
            sb.append(command.getDescription());
        }
        sb.append('\n');

        // Append params.
        if (withParams) {
            final List<CommandParam> params = command.getParams();
            for (int i = 0; i < params.size(); i++) {
                final CommandParam param = params.get(i);
                final Optional<String> value = !paramValues.isEmpty() ? paramValues.get(i) : Optional.<String>absent();
                final boolean isCurrent = currentParam.isPresent() && currentParam.get() == param;
                appendParam(sb, depth + 1, param, withDescription, value, isCurrent);
            }
            if (!params.isEmpty()) {
                // Remove last \n
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    protected void appendParam(StringBuilder sb,
                               int depth,
                               CommandParam param,
                               boolean withDescription,
                               Optional<String> value,
                               boolean isCurrent) {
        appendDepthSpaces(sb, depth);

        // Surround the current param being parsed with -> <-
        if (isCurrent) {
            sb.append("-> ");
            sb.append(getTab());
        } else {
            sb.append(getTab());
        }

        sb.append(param.getExternalForm());
        if (withDescription) {
            sb.append(" : ");
            sb.append(param.getDescription());
        }

        if (value.isPresent()) {
            sb.append(" = ");
            sb.append(value.get());
        }

        // Actually, value.isPresent and isCurrent cannot both be true at the same time.
        if (isCurrent) {
            sb.append(getTab());
            sb.append(" <-");
        }

        sb.append('\n');
    }

    @Override
    public String serializeException(Exception e) {
        final StringBuilder sb = new StringBuilder();
        appendDepthSpaces(sb, 0);
        sb.append(e.toString());
        sb.append('\n');
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            appendDepthSpaces(sb, 1);
            sb.append(stackTraceElement.toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    protected void appendDepthSpaces(StringBuilder sb, int depth) {
        final String tab = getTab();
        for (int i = 0; i < depth; i++) {
            sb.append(tab);
        }
    }

    protected String getTab() {
        return "\t";
    }

    private static final class NameComparator implements Comparator<Describable> {
        @Override
        public int compare(Describable o1, Describable o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
