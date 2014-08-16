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

package com.github.ykrasik.jerminal.internal.filesystem;

import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectoryBuilder;
import com.google.common.base.Splitter;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A builder for a {@link ShellFileSystem}. {@link ShellFileSystem}s are <b>immutable</b> once built.<br>
 * This builder expects to receive a path to a command and a set of commands.
 * The path is separated by the delimiter '/', for example: "path/to/element". Any directories along that path that don't exist
 * will be automatically created.<br>
 * Directories may also provide an optional description. The description delimiter is ':'. A description may
 * only be assigned to a directory when it is first created. Any subsequent calls may omit the description.<br>
 * <p>For example: "this/is/a/path : This is a path element/to/some/directory : Everything up till now is a directory".<br>
 * This will create the following directory structure: this/is/a/path/to/some/directory and also assign the given descriptions
 * to "path" and "directory".</p>
 *
 * @author Yevgeny Krasik
 */
public class ShellFileSystemBuilder {
    private static final char PATH_DELIMITER = '/';
    private static final String PATH_DELIMITER_STR = String.valueOf(PATH_DELIMITER);
    private static final char DESCRIPTION_DELIMITER = ':';

    private static final Splitter PATH_SPLITTER = Splitter.on(PATH_DELIMITER).trimResults();
    private static final Splitter DESCRIPTION_SPLITTER = Splitter.on(DESCRIPTION_DELIMITER).trimResults();

    private final ShellDirectoryBuilder rootBuilder;
    private final TrieBuilder<ShellCommand> globalCommandsBuilder;

    public ShellFileSystemBuilder() {
        this.rootBuilder = new ShellDirectoryBuilder("root", "root");
        this.globalCommandsBuilder = new TrieBuilder<>();
    }

    public ShellFileSystem build() {
        final ShellDirectory root = rootBuilder.build();
        final Trie<ShellCommand> globalCommands = globalCommandsBuilder.build();
        return new ShellFileSystemImpl(root, globalCommands);
    }

    public ShellFileSystemBuilder add(ShellCommand... commands) {
        return add("", commands);
    }

    public ShellFileSystemBuilder add(String path, ShellCommand... commands) {
        return add(path, Arrays.asList(commands));
    }

    public ShellFileSystemBuilder add(String path, Collection<ShellCommand> commands) {
        final ShellDirectoryBuilder directory = getOrCreatePath(path);
        directory.addCommands(commands);
        return this;
    }

    public ShellFileSystemBuilder addGlobalCommands(ShellCommand... globalCommands) {
        return addGlobalCommands(Arrays.asList(globalCommands));
    }

    public ShellFileSystemBuilder addGlobalCommands(Collection<ShellCommand> globalCommands) {
        for (ShellCommand globalCommand : globalCommands) {
            final String name = globalCommand.getName();
            this.globalCommandsBuilder.add(name, globalCommand);
        }
        return this;
    }

    private ShellDirectoryBuilder getOrCreatePath(String path) {
        // Ignore any leading '/', paths always start from root.
        final String trimmedPath = path.trim();
        final String pathToSplit = trimmedPath.startsWith(PATH_DELIMITER_STR) ? trimmedPath.substring(1) : trimmedPath;
        final List<String> splitPath = PATH_SPLITTER.splitToList(pathToSplit);

        // Advance along the path, creating missing child directories as required.
        ShellDirectoryBuilder currentDir = rootBuilder;
        for (int i = 0; i < splitPath.size(); i++) {
            final String pathElement = splitPath.get(i);
            if (pathElement.isEmpty()) {
                if (i == splitPath.size() - 1) {
                    // The last element of the path is allowed to be empty.
                    // This allows any path to end with a '/', and it will simply ignore the last '/'.
                    // For example: "path/to/element/" is the same as "path/to/element".
                    continue;
                } else {
                    throw new ShellException("Path elements aren't allowed to be empty!");
                }
            }

            currentDir = parsePathElement(currentDir, pathElement);
        }
        return currentDir;
    }

    private ShellDirectoryBuilder parsePathElement(ShellDirectoryBuilder currentDir, String pathElement) {
        // A pathElement is allowed to contain an optional description for the directory.
        // The format is "{name}[:{description}]
        final List<String> splitPathElement = DESCRIPTION_SPLITTER.splitToList(pathElement);

        final String name = splitPathElement.get(0);
        final String description = splitPathElement.size() == 2 ? splitPathElement.get(1) : "directory";
        return currentDir.getOrCreateDirectory(name, description);
    }
}
