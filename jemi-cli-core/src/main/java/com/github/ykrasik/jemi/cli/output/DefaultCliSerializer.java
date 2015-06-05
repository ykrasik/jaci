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
import com.github.ykrasik.jemi.Identifiable;
import com.github.ykrasik.jemi.IdentifiableComparators;
import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.string.StringUtils;
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
        serializer
            .append('[')
            .append(directory.getName())
            .append(']')
            .newLine();

        // Serialize child commands.
        final List<CliCommand> commands = new ArrayList<>(directory.getChildCommands());
        Collections.sort(commands, IdentifiableComparators.nameComparator());

        serializeIdentifiables(serializer, commands);

        // Serialize child directories.
        final List<CliDirectory> directories = new ArrayList<>(directory.getChildDirectories());
        Collections.sort(directories, IdentifiableComparators.nameComparator());

        if (!recursive) {
            serializeIdentifiables(serializer, directories);
        } else {
            // Recursively serialize child directories.
            for (CliDirectory childDirectory : directories) {
                serializer.incIndent();
                serializeDirectory(serializer, childDirectory, true);
                serializer.decIndent();
            }
        }
    }

    @Override
    public List<String> serializeCommand(CliCommand command) {
        final Serializer serializer = new Serializer();

        // Serialize command name : description
        serializeIdentifiable(serializer, command);

        // Serialize each param name : description
        serializeIdentifiables(serializer, command.getParams());

        return serializer.serialization;
    }

    private void serializeIdentifiables(Serializer serializer, List<? extends Identifiable> identifiables) {
        serializer.incIndent();
        for (Identifiable identifiable : identifiables) {
            serializeIdentifiable(serializer, identifiable);
        }
        serializer.decIndent();
    }

    private void serializeIdentifiable(Serializer serializer, Identifiable identifiable) {
        final Identifier identifier = identifiable.getIdentifier();
        serializer
            .append(identifier.getName())
            .append(" : ")
            .append(identifier.getDescription())
            .newLine();
    }

    @Override
    public List<String> serializeException(Exception e) {
        final Serializer serializer = new Serializer();

        serializer.append(e.toString())
            .newLine();

        serializer.incIndent();
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            serializer.append(stackTraceElement.toString())
                .newLine();
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
            final Opt<Object> value = boundParams.getBoundValue(param);
            final boolean isCurrent = nextParam.isPresent() && nextParam.get() == param;

            // Surround the current param being parsed with -> <-
            if (isCurrent) {
                serializer.append("-> ").append(tab);
            } else {
                serializer.append(tab);
            }

            serializer.append(param.toExternalForm());
            if (value.isPresent()) {
                serializer.append(" = ").append(value.get().toString());
            }

            // Actually, value.isPresent and isCurrent cannot both be true at the same time.
            if (isCurrent) {
                serializer.append(tab).append(" <-");
            }

            serializer.newLine();
        }
    }

    @Override
    public List<String> serializeSuggestions(Suggestions suggestions) {
        final Serializer serializer = new Serializer();

        serializer.append("Suggestions:")
            .newLine();

        serializer.incIndent();
        printSuggestions(serializer, suggestions.getDirectorySuggestions(), "Directories");
        printSuggestions(serializer, suggestions.getCommandSuggestions(), "Commands");
        printSuggestions(serializer, suggestions.getParamNameSuggestions(), "Parameter names");
        printSuggestions(serializer, suggestions.getParamValueSuggestions(), "Parameter values");
        serializer.decIndent();

        return serializer.serialization;
    }

    private void printSuggestions(Serializer serializer, List<String> suggestions, String suggestionsTitle) {
        if (!suggestions.isEmpty()) {
            serializer
                .append(suggestionsTitle)
                .append(": [")
                .append(StringUtils.join(suggestions, ", "))
                .append(']')
                .newLine();
        }
    }

    private class Serializer {
        private final List<String> serialization = new ArrayList<>();

        private StringBuilder sb = new StringBuilder();
        private int indent;

        /**
         * Whether indent was appended to this line. Reset every time a new line is opened.
         */
        private boolean indentAppended;

        public void incIndent() {
            indent++;
        }

        public void decIndent() {
            indent--;
            if (indent < 0) {
                throw new IllegalArgumentException("Invalid indent: " + indent);
            }
        }

        public Serializer append(String str) {
            indentIfNecessary();
            sb.append(str);
            return this;
        }

        public Serializer append(char ch) {
            indentIfNecessary();
            sb.append(ch);
            return this;
        }

        public Serializer newLine() {
            serialization.add(sb.toString());
            sb = new StringBuilder();
            indentAppended = false;
            return this;
        }

        private void indentIfNecessary() {
            if (!indentAppended) {
                appendIndent();
                indentAppended = true;
            }
        }

        private void appendIndent() {
            for (int i = 0; i < indent; i++) {
                sb.append(tab);
            }
        }

        @Override
        public String toString() {
            return serialization.toString();
        }
    }
}
