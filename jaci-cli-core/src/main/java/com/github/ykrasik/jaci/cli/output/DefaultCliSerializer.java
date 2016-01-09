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

package com.github.ykrasik.jaci.cli.output;

import com.github.ykrasik.jaci.Identifiable;
import com.github.ykrasik.jaci.IdentifiableComparators;
import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.cli.assist.BoundParams;
import com.github.ykrasik.jaci.cli.assist.CommandInfo;
import com.github.ykrasik.jaci.cli.assist.Suggestions;
import com.github.ykrasik.jaci.cli.command.CliCommand;
import com.github.ykrasik.jaci.cli.directory.CliDirectory;
import com.github.ykrasik.jaci.cli.param.CliParam;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.string.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A default implementation of a {@link CliSerializer}.
 *
 * @author Yevgeny Krasik
 */
public class DefaultCliSerializer implements CliSerializer {
    private final String tab;

    /**
     * Create a serializer with the default '\t' tab string.
     */
    public DefaultCliSerializer() {
        this("\t");
    }

    /**
     * Create a serializer with the given 'tab' string.
     *
     * @param tab String to use as a 'tab' string.
     */
    public DefaultCliSerializer(String tab) {
        this.tab = Objects.requireNonNull(tab, "tab");
    }

    @Override
    public String serializePathToDirectory(CliDirectory directory) {
        return directory.toPath();
    }

    @Override
    public String serializeCommandLine(CliDirectory workingDirectory, String commandLine) {
        Objects.requireNonNull(commandLine, "commandLine");
        final String path = serializePathToDirectory(workingDirectory);
        return '[' + path + "] " + commandLine;
    }

    @Override
    public Serialization serializeDirectory(CliDirectory directory, boolean recursive) {
        final Serialization serialization = createSerialization();
        serializeDirectory(serialization, directory, recursive);
        return serialization;
    }

    private void serializeDirectory(Serialization serialization, CliDirectory directory, boolean recursive) {
        // Serialize root directory name.
        serialization
            .append('[')
            .append(directory.getName())
            .append(']')
            .newLine();

        // Serialize child commands.
        final List<CliCommand> commands = new ArrayList<>(directory.getChildCommands());
        Collections.sort(commands, IdentifiableComparators.nameComparator());

        serializeIdentifiables(serialization, commands);

        // Serialize child directories.
        final List<CliDirectory> directories = new ArrayList<>(directory.getChildDirectories());
        Collections.sort(directories, IdentifiableComparators.nameComparator());

        if (!recursive) {
            serializeIdentifiables(serialization, directories);
        } else {
            // Recursively serialize child directories.
            for (CliDirectory childDirectory : directories) {
                serialization.incIndent();
                serializeDirectory(serialization, childDirectory, true);
                serialization.decIndent();
            }
        }
    }

    @Override
    public Serialization serializeCommand(CliCommand command) {
        final Serialization serialization = createSerialization();

        // Serialize command name : description
        serializeIdentifiable(serialization, command);

        // Serialize each param name : description
        serializeIdentifiables(serialization, command.getParams());

        return serialization;
    }

    private void serializeIdentifiables(Serialization serialization, List<? extends Identifiable> identifiables) {
        serialization.incIndent();
        for (Identifiable identifiable : identifiables) {
            serializeIdentifiable(serialization, identifiable);
        }
        serialization.decIndent();
    }

    private void serializeIdentifiable(Serialization serialization, Identifiable identifiable) {
        final Identifier identifier = identifiable.getIdentifier();
        serialization
            .append(identifier.getName())
            .append(" : ")
            .append(identifier.getDescription())
            .newLine();
    }

    @Override
    public Serialization serializeException(Exception e) {
        final Serialization serialization = createSerialization();

        serialization.append(e.toString())
            .newLine();

        serialization.incIndent();
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            serialization.append(stackTraceElement.toString())
                .newLine();
        }
        return serialization;
    }

    @Override
    public Serialization serializeCommandInfo(CommandInfo info) {
        final Serialization serialization = createSerialization();

        // Serialize command name : description
        final CliCommand command = info.getCommand();
        serializeIdentifiable(serialization, command);

        // Serialize bound params.
        final BoundParams boundParams = info.getBoundParams();
        serializeBoundParams(serialization, command, boundParams);

        return serialization;
    }

    private void serializeBoundParams(Serialization serialization, CliCommand command, BoundParams boundParams) {
        final Opt<CliParam> nextParam = boundParams.getNextParam();
        for (CliParam param : command.getParams()) {
            final Opt<Object> value = boundParams.getBoundValue(param);
            final boolean isCurrent = nextParam.isPresent() && nextParam.get() == param;

            // Surround the current param being parsed with -> <-
            if (isCurrent) {
                serialization.append("-> ").append(tab);
            } else {
                serialization.append(tab);
            }

            serialization.append(param.toExternalForm());
            if (value.isPresent()) {
                serialization.append(" = ").append(value.get().toString());
            }

            // Actually, value.isPresent and isCurrent cannot both be true at the same time.
            if (isCurrent) {
                serialization.append(tab).append(" <-");
            }

            serialization.newLine();
        }
    }

    @Override
    public Serialization serializeSuggestions(Suggestions suggestions) {
        final Serialization serialization = createSerialization();

        serialization.append("Suggestions:")
            .newLine();

        serialization.incIndent();
        printSuggestions(serialization, suggestions.getDirectorySuggestions(), "Directories");
        printSuggestions(serialization, suggestions.getCommandSuggestions(), "Commands");
        printSuggestions(serialization, suggestions.getParamNameSuggestions(), "Parameter names");
        printSuggestions(serialization, suggestions.getParamValueSuggestions(), "Parameter values");
        serialization.decIndent();

        return serialization;
    }

    private void printSuggestions(Serialization serialization, List<String> suggestions, String suggestionsTitle) {
        if (!suggestions.isEmpty()) {
            serialization
                .append(suggestionsTitle)
                .append(": [")
                .append(StringUtils.join(suggestions, ", "))
                .append(']')
                .newLine();
        }
    }

    private Serialization createSerialization() {
        return new Serialization(tab);
    }
}
