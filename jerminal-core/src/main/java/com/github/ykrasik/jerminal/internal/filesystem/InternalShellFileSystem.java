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

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteType;
import com.github.ykrasik.jerminal.internal.util.StringUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import java.util.*;

/**
 * An internal representation of a {@link ShellFileSystem}.<br>
 * Processes the file system upon which it is based and constructs it's own nodes. After being built, changes
 * to the underlying {@link ShellFileSystem} will not be reflected.<br>
 * Is slightly mutable for convenience - allows to add global commands after being built, but doesn't allow
 * changing the directory hierarchy.
 *
 * @author Yevgeny Krasik
 */
public class InternalShellFileSystem {
    private static final Splitter PATH_SPLITTER = Splitter.on(ShellConstants.FILE_SYSTEM_DELIMITER.charAt(0)).trimResults();
    private static final Function<InternalCommand, AutoCompleteType> AUTO_COMPLETE_TYPE_MAPPER = new Function<InternalCommand, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(InternalCommand input) {
            return AutoCompleteType.COMMAND;
        }
    };

    private final InternalShellDirectory root;
    private Trie<InternalCommand> globalCommands;

    private InternalShellDirectory workingDirectory;

    public InternalShellFileSystem(ShellFileSystem fileSystem) {
        this.root = new InternalShellDirectory(Objects.requireNonNull(fileSystem.getRoot()));
        this.globalCommands = TrieImpl.emptyTrie();
        this.workingDirectory = root;

        addGlobalCommands(fileSystem.getGlobalCommands());
    }

    /**
     * Add the commands as global commands.
     *
     * @param commands Commands to add as global commands.
     * @throws com.github.ykrasik.jerminal.internal.exception.ShellException If one of the command names is invalid or a global command with that name already exists.
     */
    public void addGlobalCommands(Command... commands) {
        addGlobalCommands(Arrays.asList(commands));
    }

    /**
     * Add the commands as global commands.
     *
     * @param commands Commands to add as global commands.
     * @throws com.github.ykrasik.jerminal.internal.exception.ShellException If one of the command names is invalid or a global command with that name already exists.
     */
    public void addGlobalCommands(Collection<Command> commands) {
        for (Command command : commands) {
            final InternalCommand internalCommand = new InternalCommand(command);
            globalCommands = globalCommands.add(command.getName(), internalCommand);
        }
    }

    /**
     * @return The working directory.
     */
    public InternalShellDirectory getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Set the working directory.
     *
     * @param directory New working directory to set.
     */
    public void setWorkingDirectory(InternalShellDirectory directory) {
        this.workingDirectory = Objects.requireNonNull(directory);
    }

    /**
     * Parse the given path as a path to an {@link InternalShellDirectory}.<br>
     * Parsing a path always starts from the working directory, unless the path explicitly starts from root.
     *
     * @param rawPath Path to parse.
     * @return The {@link InternalShellDirectory} pointed to by the path.
     * @throws ParseException If the path is invalid or doesn't point to an {@link InternalShellDirectory}.
     */
    public InternalShellDirectory parsePathToDirectory(String rawPath) throws ParseException {
        if (rawPath.isEmpty()) {
            throw emptyPath();
        }

        final boolean startsFromRoot = rawPath.startsWith(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (startsFromRoot && rawPath.length() == 1) {
            return root;
        }

        // Remove leading and trailing '/'.
        // TODO: Make sure this doesn't mask '//' or '///' as an error.
        final String pathToSplit = StringUtils.removeLeadingAndTrailingDelimiter(rawPath, ShellConstants.FILE_SYSTEM_DELIMITER);

        // Split the given path according to delimiter.
        final List<String> splitPath = PATH_SPLITTER.splitToList(pathToSplit);

        // Parse all pathElements as directories.
        InternalShellDirectory currentDirectory = startsFromRoot ? root : workingDirectory;
        for (String directoryName : splitPath) {
            if (directoryName.isEmpty()) {
                throw emptyDirectoryNameAlongPath(currentDirectory.getName());
            }
            if (ShellConstants.FILE_SYSTEM_THIS.equals(directoryName)) {
                continue;
            }
            if (ShellConstants.FILE_SYSTEM_PARENT.equals(directoryName)) {
                final Optional<InternalShellDirectory> parent = currentDirectory.getParent();
                if (parent.isPresent()) {
                    currentDirectory = parent.get();
                    continue;
                } else {
                    throw directoryDoesNotHaveParent(currentDirectory.getName());
                }
            }

            final Optional<InternalShellDirectory> childDirectory = currentDirectory.getDirectory(directoryName);
            if (childDirectory.isPresent()) {
                currentDirectory = childDirectory.get();
            } else {
                throw invalidEntry(currentDirectory.getName(), directoryName, true);
            }
        }

        return currentDirectory;
    }

    /**
     * Parse the given path as a path to an {@link InternalCommand}.<br>
     * Parsing a path always starts from the working directory, unless the path explicitly starts from root.
     *
     * @param rawPath Path to parse.
     * @return The {@link InternalCommand} pointed to by the path.
     * @throws ParseException If the path is invalid or doesn't point to an {@link InternalCommand}.
     */
    public InternalCommand parsePathToCommand(String rawPath) throws ParseException {
        // If rawPath does not contain a single delimiter, we can try use it as the command name.
        final int delimiterIndex = rawPath.lastIndexOf(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath does not contain a delimiter.
            // It could either be a global command, or a command under the workingDirectory.
            return getGlobalOrLocalCommand(rawPath);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory parse the last entry as a command.
        // So in "path/to/command", parse "path/to" as path to directory "to", and let "to" parse "command".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final InternalShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        // If rawPath ends with the delimiter, it cannot possibly point to a command.
        final String fileName = rawPath.substring(delimiterIndex + 1);
        if (fileName.isEmpty()) {
            throw pathDoesNotPointToCommand(rawPath);
        }

        final Optional<InternalCommand> file = lastDirectory.getCommand(fileName);
        if (file.isPresent()) {
            return file.get();
        } else {
            throw invalidEntry(lastDirectory.getName(), fileName, false);
        }
    }

    private InternalCommand getGlobalOrLocalCommand(String name) throws ParseException {
        // If 'name' is a global file, return it.
        final Optional<InternalCommand> globalFile = globalCommands.get(name);
        if (globalFile.isPresent()) {
            return globalFile.get();
        }

        // 'name' is not a global file, check if it is a child of the working directory.
        final Optional<InternalCommand> file = workingDirectory.getCommand(name);
        if (file.isPresent()) {
            return file.get();
        } else {
            throw invalidCommandName(name);
        }
    }

    /**
     * Provide auto complete suggestions for the path to an {@link InternalShellDirectory}.<br>
     * The path is expected to be valid all the way except the last element, which will be auto completed.
     *
     * @param rawPath Path to auto complete.
     * @return Auto complete suggestions for the next {@link InternalShellDirectory} in this path.
     * @throws ParseException If the path is invalid.
     */
    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter, just autoComplete it from the workingDirectory.
            final Trie<AutoCompleteType> possibilities = workingDirectory.autoCompleteDirectory(rawPath);
            return new AutoCompleteReturnValue(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry as a directory.
        // So in "path/to/directory", parse "path/to" as path to directory "to", and let "to" autoComplete "directory".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final InternalShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        final String directoryPrefix = rawPath.substring(delimiterIndex + 1);
        final Trie<AutoCompleteType> possibilities = lastDirectory.autoCompleteDirectory(directoryPrefix);
        return new AutoCompleteReturnValue(directoryPrefix, possibilities);
    }

    /**
     * Provide auto complete suggestions for the path either to an {@link InternalShellDirectory} or to an {@link InternalCommand}.<br>
     * The path is expected to be valid all the way except the last element, which will be auto completed.
     *
     * @return Auto complete suggestions for the next {@link InternalShellDirectory} or {@link InternalCommand} in this path.
     * @throws ParseException If the path is invalid.
     */
    public AutoCompleteReturnValue autoCompletePath(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter.
            // It could be an entry from the workingDirectory or a global command.
            final Trie<AutoCompleteType> entryPossibilities = workingDirectory.autoCompleteEntry(rawPath);
            final Trie<AutoCompleteType> globalFilePossibilities = globalCommands.subTrie(rawPath).map(AUTO_COMPLETE_TYPE_MAPPER);
            final Trie<AutoCompleteType> possibilities = entryPossibilities.union(globalFilePossibilities);
            return new AutoCompleteReturnValue(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry.
        // So in "path/to/entry", parse "path/to" as path to directory "to", and let "to" autoComplete "entry".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final InternalShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        final String entryPrefix = rawPath.substring(delimiterIndex + 1);
        final Trie<AutoCompleteType> possibilities = lastDirectory.autoCompleteEntry(entryPrefix);
        return new AutoCompleteReturnValue(entryPrefix, possibilities);
    }

    /**
     * @param directory Directory to calculate the path from root to.
     * @return The full path from the root directory to the given directory.
     */
    public List<InternalShellDirectory> getPath(InternalShellDirectory directory) {
        final List<InternalShellDirectory> path = new LinkedList<>();
        doGetPath(path, directory);
        return path;
    }

    private void doGetPath(List<InternalShellDirectory> path, InternalShellDirectory directory) {
        path.add(0, directory);
        final Optional<InternalShellDirectory> parent = directory.getParent();
        if (parent.isPresent()) {
            // Only the root dir doesn't have a parent.
            doGetPath(path, parent.get());
        }
    }

    private ParseException invalidEntry(String dirName, String entryName, boolean directory) {
        return new ParseException(
            directory ? ParseError.INVALID_DIRECTORY : ParseError.INVALID_COMMAND,
            "Directory '%s' doesn't contain %s: '%s'", dirName, directory ? "directory" : "command", entryName
        );
    }

    private ParseException invalidCommandName(String name) {
        return new ParseException(ParseError.INVALID_COMMAND, "'%s' is not a recognized command!", name);
    }

    private ParseException emptyPath() {
        return new ParseException(ParseError.INVALID_DIRECTORY, "Empty path!");
    }

    private ParseException directoryDoesNotHaveParent(String directoryName) {
        return new ParseException(ParseError.INVALID_DIRECTORY, "Directory '%s' doesn't have a parent.", directoryName);
    }

    private ParseException pathDoesNotPointToCommand(String path) {
        return new ParseException(ParseError.INVALID_COMMAND, "Path doesn't point to a command: %s", path);
    }

    private ParseException emptyDirectoryNameAlongPath(String parentName) {
        return new ParseException(ParseError.INVALID_DIRECTORY, "Empty directory name! Under: '%s'", parentName);
    }
}
