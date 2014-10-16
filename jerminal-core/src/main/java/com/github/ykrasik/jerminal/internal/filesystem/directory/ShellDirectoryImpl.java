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
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.util.*;

/**
 * An implementation for a {@link MutableShellDirectory}.
 *
 * @author Yevgeny Krasik
 */
public class ShellDirectoryImpl extends AbstractDescribable implements MutableShellDirectory {
    private final Map<String, MutableShellDirectory> childDirectories;
    private final Map<String, Command> childCommands;

    public ShellDirectoryImpl(String name) {
        this(name, "directory");
    }

    public ShellDirectoryImpl(String name, String description) {
        super(name, description);
        this.childDirectories = new HashMap<>();
        this.childCommands = new HashMap<>();

        if (!ShellConstants.isValidName(name)) {
            throw new ShellException("Invalid name for directory: '%s'", name);
        }
    }

    @Override
    public Collection<ShellDirectory> getDirectories() {
        return Collections.<ShellDirectory>unmodifiableCollection(childDirectories.values());
    }

    @Override
    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(childCommands.values());
    }

    @Override
    public MutableShellDirectory getOrCreateDirectory(String name, String description) {
        final MutableShellDirectory existingDirectory = childDirectories.get(name);
        if (existingDirectory != null) {
            return existingDirectory;
        }

        assertLegalName(name, false);
        final MutableShellDirectory newDirectory = new ShellDirectoryImpl(name, description);
        childDirectories.put(name, newDirectory);
        return newDirectory;
    }

    @Override
    public void addCommands(Command... commands) {
        addCommands(Arrays.asList(commands));
    }

    @Override
    public void addCommands(List<Command> commands) {
        for (Command command : commands) {
            final String name = command.getName();
            assertLegalName(name, true);
            childCommands.put(name, command);
        }
    }

    private void assertLegalName(String name, boolean command) {
        if (!ShellConstants.isValidName(name)) {
            throw new ShellException("Invalid name for %s: '%s'", command ? "command" : "directory", name);
        }
        if (childDirectories.containsKey(name) || childCommands.containsKey(name)) {
            throw new ShellException("Directory '%s' already contains a child entry named: '%s'", getName(), name);
        }
    }

    @Override
    public String toString() {
        return "ShellDirectoryImpl{" + "name=" + getName() + '}';
    }
}
