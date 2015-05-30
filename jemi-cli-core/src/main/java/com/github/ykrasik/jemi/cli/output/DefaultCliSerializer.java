/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.output;

import com.github.ykrasik.jemi.cli.assist.BoundParams;
import com.github.ykrasik.jemi.cli.assist.CommandInfo;
import com.github.ykrasik.jemi.cli.assist.Suggestions;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.core.Identifiable;
import com.github.ykrasik.jemi.core.IdentifiableComparators;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor
public class DefaultCliSerializer implements CliSerializer {
    @NonNull private final String tab;

    public DefaultCliSerializer() {
        this("\t");
    }

    @Override
    public String serializePathToDirectory(@NonNull CliDirectory directory) {
        return directory.toPath();
    }

    @Override
    public String serializeCommandLine(@NonNull String commandLine) {
        return "> " + commandLine;
    }

    @Override
    public List<String> serializeDirectory(@NonNull CliDirectory directory, boolean recursive) {
        final Serializer serializer = new Serializer();
        serializeDirectory(serializer, directory, recursive);
        return serializer.serialization;
    }

    private void serializeDirectory(Serializer serializer, CliDirectory directory, boolean recursive) {
        // Serialize root directory name.
        serializer.appendDepth();
        serializer.append('[');
        serializer.append(directory.getName());
        serializer.append(']');
        serializer.newLine();

        // Serialize child commands.
        final List<CliCommand> commands = new ArrayList<>(directory.getChildCommands());
        Collections.sort(commands, IdentifiableComparators.nameComparator());

        for (CliCommand command : commands) {
            serializer.incDepth();
            serializeIdentifiable(serializer, command);
            serializer.decDepth();
        }

        if (recursive) {
            // Serialize child directories.
            final List<CliDirectory> directories = new ArrayList<>(directory.getChildDirectories());
            Collections.sort(directories, IdentifiableComparators.nameComparator());

            for (CliDirectory childDirectory : directories) {
                serializer.incDepth();
                serializeDirectory(serializer, childDirectory, true);
                serializer.decDepth();
            }
        }
    }

    @Override
    public List<String> serializeCommand(CliCommand command) {
        final Serializer serializer = new Serializer();

        // Serialize command name : description
        serializeIdentifiable(serializer, command);

        // Serialize each param name : description
        for (CliParam param : command.getParams()) {
            serializeIdentifiable(serializer, param);
        }
        return serializer.serialization;
    }

    private void serializeIdentifiable(Serializer serializer, Identifiable identifiable) {
        final Identifier identifier = identifiable.getIdentifier();
        serializer.appendDepth();
        serializer.append(identifier.getName());
        serializer.append(" : ");
        serializer.append(identifier.getDescription());
        serializer.newLine();
    }

    @Override
    public List<String> serializeException(Exception e) {
        final Serializer serializer = new Serializer();

        serializer.append(e.toString());
        serializer.newLine();

        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            serializer.incDepth();
            serializer.append(stackTraceElement.toString());
            serializer.newLine();
        }
        return serializer.serialization;
    }

    @Override
    public List<String> serializeCommandInfo(CommandInfo info) {
        final Serializer serializer = new Serializer();

        // Serialize command name : description
        final CliCommand command = info.getCommand();
        serializeIdentifiable(serializer, command);

        // Serialize bound params.
        final BoundParams boundParams = info.getBoundParams();
        serializeBoundParams(serializer, command, boundParams);

        return serializer.serialization;
    }

    private void serializeBoundParams(Serializer serializer, CliCommand command, BoundParams boundParams) {
        final Opt<CliParam> nextParam = boundParams.getNextParam();
        for (CliParam param : command.getParams()) {
            serializer.appendDepth();

            final Opt<Object> value = boundParams.getBoundValue(param);
            final boolean isCurrent = nextParam.isPresent() && nextParam.get() == param;

            // Surround the current param being parsed with -> <-
            if (isCurrent) {
                serializer.append("-> ");
                serializer.append(tab);
            } else {
                serializer.append(tab);
            }

            serializer.append(param.toExternalForm());
            if (value.isPresent()) {
                serializer.append(" = ");
                serializer.append(value.get().toString());
            }

            // Actually, value.isPresent and isCurrent cannot both be true at the same time.
            if (isCurrent) {
                serializer.append(tab);
                serializer.append(" <-");
            }

            serializer.newLine();
        }
    }

    @Override
    public List<String> serializeSuggestions(Suggestions suggestions) {
        final Serializer serializer = new Serializer();

        serializer.append("Suggestions:");
        serializer.newLine();

        printSuggestions(serializer, suggestions.getDirectorySuggestions(), "Directories");
        printSuggestions(serializer, suggestions.getCommandSuggestions(), "Commands");
        printSuggestions(serializer, suggestions.getParamNameSuggestions(), "Parameter names");
        printSuggestions(serializer, suggestions.getParamValueSuggestions(), "Parameter values");

        return serializer.serialization;
    }

    private void printSuggestions(Serializer serializer, List<String> suggestions, String suggestionsTitle) {
        if (!suggestions.isEmpty()) {
            serializer.append(suggestionsTitle);
            serializer.append(": [");
            serializer.append(join(suggestions));
            serializer.append(']');
            serializer.newLine();
        }
    }

    private String join(List<String> list) {
        final StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str);
            sb.append(", ");
        }
        if (sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    private class Serializer {
        private final List<String> serialization = new ArrayList<>();

        private StringBuilder sb = new StringBuilder();
        private int depth;

        public Serializer incDepth() {
            depth++;
            return this;
        }

        public Serializer decDepth() {
            depth--;
            if (depth < 0) {
                throw new IllegalArgumentException("Invalid depth: " + depth);
            }
            return this;
        }

        public Serializer appendDepth() {
            for (int i = 0; i < depth; i++) {
                sb.append(tab);
            }
            return this;
        }

        public Serializer append(String str) {
            sb.append(str);
            return this;
        }

        public Serializer append(char ch) {
            sb.append(ch);
            return this;
        }

        public Serializer newLine() {
            serialization.add(sb.toString());
            sb = new StringBuilder();
            return this;
        }
    }
}
