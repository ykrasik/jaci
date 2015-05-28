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

package com.github.ykrasik.jemi.core.directory;

import com.github.ykrasik.jemi.core.Identifiable;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.core.command.CommandDef;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Delegate;

import java.util.*;

/**
 * A container for child {@link CommandDirectoryDef}s and {@link CommandDef}s.
 *
 * @author Yevgeny Krasik
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandDirectoryDef implements Identifiable {
    /**
     * This directory's {@link Identifier}.
     */
    @Delegate
    @NonNull private final Identifier identifier;

    /**
     * Child {@link CommandDirectoryDef}s.
     */
    @NonNull private final List<CommandDirectoryDef> directoryDefs;

    /**
     * Child {@link CommandDef}s.
     */
    @NonNull private final List<CommandDef> commandDefs;

    @Override
    public String toString() {
        return identifier.toString();
    }

    /**
     * @author Yevgeny Krasik
     */
    // TODO: JavaDoc
    public static class Builder {
        private final Map<String, Builder> childDirectories = new HashMap<>();
        private final Map<String, CommandDef> childCommands = new HashMap<>();

        private final Identifier identifier;

        public Builder() {
            this("root", "root");
        }

        private Builder(String name, String description) {
            this.identifier = new Identifier(name, description);
        }

        /**
         * If a directory with the requested name is already a child directory of this directory, will return
         * the existing child. Otherwise, will create a new child directory with that name and return it.
         *
         * @param name Directory name.
         * @return An existing or newly created child {@link Builder}.
         */
        public Builder getOrCreateDirectory(@NonNull String name) {
            final String trimmedName = name.trim();
            if (trimmedName.isEmpty()) {
                throw new IllegalArgumentException("Empty or all-whitespace name is invalid!");
            }

            final Builder existingDirectory = childDirectories.get(trimmedName);
            if (existingDirectory != null) {
                return existingDirectory;
            }

            assertLegalName(trimmedName);
            final Builder newDirectory = new Builder(trimmedName, "directory");
            childDirectories.put(trimmedName, newDirectory);
            return newDirectory;
        }

        /**
         * Add the commandDefs to this directory.
         *
         * @param commandDefs CommandDefs to add.
         * @throws IllegalArgumentException If any of the commandDefs' names clash with existing commandDefs under this directory.
         */
        public Builder addCommandDefs(CommandDef... commandDefs) {
            return addCommandDefs(Arrays.asList(commandDefs));
        }

        /**
         * Add the commandDefs to this directory.
         *
         * @param commandDefs CommandDefs to add.
         * @throws IllegalArgumentException If any of the commandDefs' names clash with existing commandDefs under this directory.
         */
        public Builder addCommandDefs(List<CommandDef> commandDefs) {
            for (CommandDef commandDef : commandDefs) {
                final String name = commandDef.getName();
                assertLegalName(name);
                childCommands.put(name, commandDef);
            }
            return this;
        }

        private void assertLegalName(String name) {
            if (childDirectories.containsKey(name) || childCommands.containsKey(name)) {
                throw new IllegalArgumentException(String.format("Directory '%s' already contains child entry: '%s'", identifier.getName(), name));
            }
        }

        // TODO: JavaDoc
        public CommandDirectoryDef build() {
            final List<CommandDirectoryDef> directories = buildDirectories();
            final List<CommandDef> commandDefs = buildCommandDefs();
            return new CommandDirectoryDef(identifier, directories, commandDefs);
        }

        private List<CommandDirectoryDef> buildDirectories() {
            final List<CommandDirectoryDef> directories = new ArrayList<>(childDirectories.size());
            for (Builder builder : childDirectories.values()) {
                directories.add(builder.build());
            }
            return Collections.unmodifiableList(directories);
        }

        private List<CommandDef> buildCommandDefs() {
            return Collections.unmodifiableList(new ArrayList<>(childCommands.values()));
        }
    }
}
