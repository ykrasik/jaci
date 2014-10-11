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

package com.github.ykrasik.jerminal.api.filesystem;

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.directory.MutableShellDirectory;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectoryImpl;
import com.github.ykrasik.jerminal.internal.util.StringUtils;
import com.google.common.base.Splitter;

import java.util.*;

/**
 * A mutable container for a hierarchy of {@link ShellDirectory} and {@link Command}.<br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class ShellFileSystem {
    private static final Splitter PATH_SPLITTER = Splitter.on(ShellConstants.FILE_SYSTEM_DELIMITER.charAt(0)).trimResults();
    private static final Splitter DESCRIPTION_SPLITTER = Splitter.on(ShellConstants.FILE_SYSTEM_DESCRIPTION_DELIMITER.charAt(0)).trimResults();

    private final MutableShellDirectory root;
    private final Map<String, Command> globalCommands;

    public ShellFileSystem() {
        this.root = new ShellDirectoryImpl("/", "root");
        this.globalCommands = new HashMap<>();
    }

    /**
     * Add the commands as global commands.
     *
     * @throws ShellException If one of the command names is invalid.
     */
    public ShellFileSystem addGlobalCommands(Command... globalCommands) {
        return addGlobalCommands(Arrays.asList(globalCommands));
    }

    /**
     * Add the commands as global commands.
     *
     * @throws ShellException If one of the command names is invalid.
     */
    public ShellFileSystem addGlobalCommands(List<Command> globalCommands) {
        for (Command globalCommand : globalCommands) {
            final String name = globalCommand.getName();
            if (!ShellConstants.isValidName(name)) {
                throw new ShellException("Invalid name for global command: '%s'", name);
            }
            if (this.globalCommands.containsKey(name)) {
                throw new ShellException("FileSystem already contains a global command named: '%s'", name);
            }

            this.globalCommands.put(name, globalCommand);
        }
        return this;
    }

    /**
     * Add the commands to the root directory.
     *
     * @throws ShellException If one of the command names is invalid.
     */
    public ShellFileSystem addCommands(Command... commands) {
        return addCommands(Arrays.asList(commands));
    }

    /**
     * Add the commands to the root directory.
     *
     * @throws ShellException If one of the command names is invalid.
     */
    public ShellFileSystem addCommands(List<Command> commands) {
        root.addCommands(commands);
        return this;
    }

    /**
     * Add the commands to the directory specified by the path.<br>
     * Any directory along the path that doesn't exist will be created.<br>
     *
     * @throws ShellException If the path is invalid or one of the command names is invalid.
     */
    public ShellFileSystem addCommands(String path, Command... commands) {
        return addCommands(path, Arrays.asList(commands));
    }

    /**
     * Add the commands to the directory specified by the path.<br>
     * Any directory along the path that doesn't exist will be created.<br>
     *
     * @throws ShellException If the path is invalid or one of the command names is invalid.
     */
    public ShellFileSystem addCommands(String path, List<Command> commands) {
        final String trimmedPath = path.trim();
        // TODO: Use this as a const?
        if ("//".equals(trimmedPath)) {
            throw new ShellException("Invalid path: '%s'", path);
        }

        // Ignore any leading and trailing '/', paths always start from root.
        // TODO: Make sure this doesn't mask '//' or '///' as an error.
        final String pathWithoutDelimiters = StringUtils.removeLeadingAndTrailingDelimiter(path, ShellConstants.FILE_SYSTEM_DELIMITER);
        final List<String> splitPath = PATH_SPLITTER.splitToList(pathWithoutDelimiters);

        final MutableShellDirectory directory = getOrCreatePathToDirectory(splitPath);
        directory.addCommands(commands);
        return this;
    }

    private MutableShellDirectory getOrCreatePathToDirectory(List<String> path) throws ShellException {
        // If an empty path is the only pathElement in the path, this is ok.
        // Allows for paths like '/' and '' to be considered the same.
        if (path.size() == 1 && path.get(0).isEmpty()) {
            return root;
        }

        // Advance along the path, creating directories as necessary.
        MutableShellDirectory dir = root;
        for (String pathElement : path) {
            // A pathElement is allowed to contain an optional description for the directory.
            // The format is "{name}[:{description}]
            final List<String> splitPathElement = DESCRIPTION_SPLITTER.splitToList(pathElement);
            final String name = splitPathElement.get(0);
            final String description = splitPathElement.size() == 2 ? splitPathElement.get(1) : "directory";

            if (!ShellConstants.isValidName(name)) {
                throw new ShellException("Invalid name for directory: '%s'", name);
            }

            dir = dir.getOrCreateDirectory(name, description);
        }

        return dir;
    }

    /**
     * @return The root directory.
     */
    public ShellDirectory getRoot() {
        return root;
    }

    /**
     * @return The global files.
     */
    public Collection<Command> getGlobalCommands() {
        return Collections.unmodifiableCollection(globalCommands.values());
    }
}
