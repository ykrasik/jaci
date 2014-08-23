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

package com.github.ykrasik.jerminal.internal.filesystem.directory;

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.command.Command;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFile;
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFileImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A builder for a {@link ShellDirectory}.
 * {@link ShellDirectory ShellDirectories} are <b>immutable</b> once built.
 *
 * @author Yevgeny Krasik
 */
public class ShellDirectoryBuilder {
    private static final String DELIMITER = String.valueOf(ShellConstants.FILE_SYSTEM_DELIMITER);

    private final String name;
    private final String description;
    private final Map<String, ShellDirectoryBuilder> childDirectoryBuilders;
    private final Map<String, ShellFile> childFiles;

    public ShellDirectoryBuilder(String name, String description) {
        this.name = name;
        this.description = description;
        this.childDirectoryBuilders = new HashMap<>();
        this.childFiles = new HashMap<>();
    }

    public ShellDirectory build() {
        final Map<String, ShellDirectory> childDirectories = buildChildren();
        final ShellDirectoryImpl directory = new ShellDirectoryImpl(name, description, childDirectories, childFiles);
        for (ShellDirectory childDirectory : childDirectories.values()) {
            // This downcast isn't great, but it's guaranteed to always succeed.
            // FIXME: Figure out how to get rid of the need for a parent.
            ((ShellDirectoryImpl) childDirectory).setParent(directory);
        }
        return directory;
    }

    private Map<String, ShellDirectory> buildChildren() {
        final Map<String, ShellDirectory> childDirectories = new HashMap<>(childDirectoryBuilders.size());
        for (Entry<String, ShellDirectoryBuilder> entry : childDirectoryBuilders.entrySet()) {
            final ShellDirectory childDirectory = entry.getValue().build();
            childDirectories.put(entry.getKey(), childDirectory);
        }
        return childDirectories;
    }

    public ShellDirectoryBuilder getOrCreateDirectory(String name, String description) {
        // If such a child directory already exists, return it.
        final ShellDirectoryBuilder childDirectory = childDirectoryBuilders.get(name);
        if (childDirectory != null) {
            return childDirectory;
        }

        // Assert 'name' is legal and isn't already taken by a child command.
        assertLegalName(name);
        assertNoFile(name);

        // Create a new child directory, link it and return.
        final ShellDirectoryBuilder builder = new ShellDirectoryBuilder(name, description);
        childDirectoryBuilders.put(name, builder);
        return builder;
    }

    public void addCommands(Command... commands) {
        addCommands(Arrays.asList(commands));
    }

    public void addCommands(Collection<Command> commands) {
        for (Command command : commands) {
            addCommand(command);
        }
    }

    public void addCommand(Command command) {
        final String commandName = command.getName();

        // Assert 'commandName' is legal and isn't already taken by a child directory or command.
        assertLegalName(commandName);
        assertNoDirectory(commandName);
        assertNoFile(commandName);

        // Link the command.
        final ShellFile file = new ShellFileImpl(command);
        childFiles.put(commandName, file);
    }

    private void assertLegalName(String name) {
        if (name.contains(DELIMITER) ||
            name.equals(ShellConstants.FILE_SYSTEM_THIS) ||
            name.equals(ShellConstants.FILE_SYSTEM_PARENT)) {
            throw new ShellException("Illegal name for entry: '%s'", name);
        }
    }

    private void assertNoDirectory(String name) {
        if (childDirectoryBuilders.containsKey(name)) {
            throw new ShellException("Directory already contains a child directory with name '%s'!", name);
        }
    }

    private void assertNoFile(String name) {
        if (childFiles.containsKey(name)) {
            throw new ShellException("Directory already contains a child command with name '%s'!", name);
        }
    }
}
