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
import com.github.ykrasik.jerminal.internal.annotation.AnnotationProcessor;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.directory.MutableShellDirectory;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectoryImpl;
import com.github.ykrasik.jerminal.internal.util.StringUtils;
import com.google.common.base.Splitter;

import java.util.*;

/**
 * A mutable container for a hierarchy of {@link ShellDirectory} and {@link Command}.<br>
 * Supports 2 types of commands - local commands which must belong to some {@link ShellDirectory}
 * and global commands, which don't belong to any {@link ShellDirectory} and are accessible from every
 * part of the file system.<br>
 * The {@link ShellFileSystem} builds a directory hierarchy and links commands to directories.
 * For methods that take a path parameter, the path is separated by the delimiter '/', like "path/to/element".
 * Any directories along that path that don't exist will be automatically created.<br>
 * Directories may also provide an optional description. The description delimiter is ':'. A description may
 * only be assigned to a directory when it is first created. Any subsequent calls may omit the description.<br>
 * <p>For example: "this/is/a/path : This is a path element/to/some/directory : Everything up till now is a directory".<br>
 * This will create the following directory structure: this/is/a/path/to/some/directory and also assign the given descriptions
 * to "path" and "directory".</p>
 *
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class ShellFileSystem {
    private static final Splitter PATH_SPLITTER = Splitter.on(ShellConstants.FILE_SYSTEM_DELIMITER.charAt(0)).trimResults();
    private static final Splitter DESCRIPTION_SPLITTER = Splitter.on(ShellConstants.FILE_SYSTEM_DESCRIPTION_DELIMITER.charAt(0)).trimResults();

    private final MutableShellDirectory root;
    private final Map<String, Command> globalCommands;
    private final AnnotationProcessor annotationProcessor;

    public ShellFileSystem() {
        this.root = new ShellDirectoryImpl("root", "root");
        this.globalCommands = new HashMap<>();
        this.annotationProcessor = new AnnotationProcessor();
    }

    // FIXME: JavaDoc. Mention that class must have a no-args ctor.
    public <T> ShellFileSystem processAnnotations(Class<T> clazz) {
        annotationProcessor.process(this, clazz);
        return this;
    }

    /**
     * Add the commands as global commands.
     *
     * @param commands Commands to add as global commands.
     * @return this, for chained execution.
     * @throws ShellException If one of the command names is invalid or a global command with that name already exists.
     */
    public ShellFileSystem addGlobalCommands(Command... commands) {
        return addGlobalCommands(Arrays.asList(commands));
    }

    /**
     * Add the commands as global commands.
     *
     * @param commands Commands to add as global commands.
     * @return this, for chained execution.
     * @throws ShellException If one of the command names is invalid or a global command with that name already exists.
     */
    public ShellFileSystem addGlobalCommands(List<Command> commands) {
        for (Command globalCommand : commands) {
            final String name = globalCommand.getName();
            if (!ShellConstants.isValidName(name)) {
                throw new ShellException("Invalid name for global command: '%s'", name);
            }
            if (globalCommands.containsKey(name)) {
                throw new ShellException("FileSystem already contains a global command named: '%s'", name);
            }

            globalCommands.put(name, globalCommand);
        }
        return this;
    }

    /**
     * Add the commands to the root directory.
     *
     * @param commands Commands to add.
     * @return this, for chained execution.
     * @throws ShellException If one of the command names is invalid or a command with that name already exists under root.
     */
    public ShellFileSystem addCommands(Command... commands) {
        return addCommands(Arrays.asList(commands));
    }

    /**
     * Add the commands to the root directory.
     *
     * @param commands Commands to add.
     * @return this, for chained execution.
     * @throws ShellException If one of the command names is invalid or a command with that name already exists under root.
     */
    public ShellFileSystem addCommands(List<Command> commands) {
        root.addCommands(commands);
        return this;
    }

    /**
     * Add the commands to the directory specified by the path.<br>
     * Any directory along the path that doesn't exist will be created.<br>
     *
     * @param path Path to add commands under.
     * @param commands Commands to add.
     * @return this, for chained execution.
     * @throws ShellException If the path is invalid, one of the command names is invalid
     *                        or a command with that name already exists under the path.
     */
    public ShellFileSystem addCommands(String path, Command... commands) {
        return addCommands(path, Arrays.asList(commands));
    }

    /**
     * Add the commands to the directory specified by the path.<br>
     * Any directory along the path that doesn't exist will be created.<br>
     *
     * @param path Path to add commands under.
     * @param commands Commands to add.
     * @return this, for chained execution.
     * @throws ShellException If the path is invalid, one of the command names is invalid
     *                        or a command with that name already exists under the path.
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
     * @return The global commands.
     */
    public Collection<Command> getGlobalCommands() {
        return Collections.unmodifiableCollection(globalCommands.values());
    }
}
