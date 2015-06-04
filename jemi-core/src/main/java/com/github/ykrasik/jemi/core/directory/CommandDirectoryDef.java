/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.core.directory;

import com.github.ykrasik.jemi.core.Identifiable;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.core.command.CommandDef;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.string.StringUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * A definition for a command directory.
 * Contains the directory's name, description, child {@link CommandDirectoryDef}s and child {@link CommandDef}s.
 * Built through the {@link CommandDirectoryDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandDirectoryDef implements Identifiable {
    private final Identifier identifier;
    private final List<CommandDirectoryDef> directoryDefs;
    private final List<CommandDef> commandDefs;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * @return Child {@link CommandDirectoryDef}s.
     */
    public List<CommandDirectoryDef> getDirectoryDefs() {
        return directoryDefs;
    }

    /**
     * @return Child {@link CommandDef}s.
     */
    public List<CommandDef> getCommandDefs() {
        return commandDefs;
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    /**
     * A builder for a {@link CommandDirectoryDef}.
     */
    public static class Builder {
        private final Map<String, Builder> childDirectories = new HashMap<>();
        private final Map<String, CommandDef> childCommands = new HashMap<>();

        private final String name;

        private String description = "directory";

        public Builder(@NonNull String name) {
            this.name = name;
        }

        /**
         * Set the directory's description.
         *
         * @param description Description to set.
         * @return this, for chaining.
         */
        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        /**
         * If a directory with the given name is already a child directory of this directory, will return
         * the existing child. Otherwise, will create a new child directory with that name and return it.
         *
         * @param name Directory name.
         * @return An existing or newly created child {@link Builder}.
         */
        public Builder getOrCreateDirectory(@NonNull String name) {
            final Opt<String> nonEmptyName = StringUtils.getNonEmptyString(name);
            if (!nonEmptyName.isPresent()) {
                throw new IllegalArgumentException("Empty or all-whitespace name is invalid!");
            }
            final String dirName = nonEmptyName.get();

            final Builder existingDirectory = childDirectories.get(dirName);
            if (existingDirectory != null) {
                return existingDirectory;
            }

            assertLegalName(dirName);
            final Builder newDirectory = new Builder(dirName);
            childDirectories.put(dirName, newDirectory);
            return newDirectory;
        }

        /**
         * Add child {@link CommandDef}s to this directory.
         *
         * @param commandDefs CommandDefs to add.
         * @throws IllegalArgumentException If any of the commandDefs' names clash with existing commandDefs under this directory.
         */
        public Builder addCommandDefs(CommandDef... commandDefs) {
            return addCommandDefs(Arrays.asList(commandDefs));
        }

        /**
         * Add child {@link CommandDef}s to this directory.
         *
         * @param commandDefs CommandDefs to add.
         * @throws IllegalArgumentException If any of the commandDefs' names clash with existing commandDefs under this directory.
         */
        public Builder addCommandDefs(List<CommandDef> commandDefs) {
            for (CommandDef commandDef : commandDefs) {
                final String name = commandDef.getIdentifier().getName();
                assertLegalName(name);
                childCommands.put(name, commandDef);
            }
            return this;
        }

        private void assertLegalName(String name) {
            if (childDirectories.containsKey(name) || childCommands.containsKey(name)) {
                throw new IllegalArgumentException(String.format("Directory '%s' already contains child entry: '%s'", this.name, name));
            }
        }

        /**
         * @return A {@link CommandDirectoryDef} built out of this builder's parameters.
         */
        public CommandDirectoryDef build() {
            final Identifier identifier = new Identifier(name, description);
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
