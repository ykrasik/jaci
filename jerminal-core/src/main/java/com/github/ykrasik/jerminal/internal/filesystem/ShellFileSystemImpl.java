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
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFile;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementaion for a {@link ShellFileSystem}. <b>Immutable</b>.
 *
 * @author Yevgeny Krasik
 */
public class ShellFileSystemImpl implements ShellFileSystem {
    private static final Splitter SPLITTER = Splitter.on(ShellConstants.FILE_SYSTEM_DELIMITER);
    private static final Function<ShellFile, AutoCompleteType> AUTO_COMPLETE_TYPE_MAPPER = new Function<ShellFile, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(ShellFile input) {
            return AutoCompleteType.COMMAND;
        }
    };

    private final ShellDirectory root;
    private final Trie<ShellFile> globalFiles;

    private ShellDirectory currentDirectory;

    ShellFileSystemImpl(ShellDirectory root,
                        Trie<ShellFile> globalFiles) {
        this.root = root;
        this.globalFiles = globalFiles;
        this.currentDirectory = root;
    }

    @Override
    public ShellDirectory getRoot() {
        return root;
    }

    @Override
    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public void setCurrentDirectory(ShellDirectory directory) {
        this.currentDirectory = checkNotNull(directory, "directory");
    }

    @Override
    public ShellDirectory parsePathToDirectory(String rawPath) throws ParseException {
        if (rawPath.isEmpty()) {
            throw emptyDirectoryNameAlongPath(currentDirectory);
        }

        final boolean startsFromRoot = rawPath.charAt(0) == ShellConstants.FILE_SYSTEM_DELIMITER;
        if (startsFromRoot && rawPath.length() == 1) {
            return root;
        }

        // Remove leading and trailing '/'.
        final String pathToSplit = removeLeadingAndTrailingDelimiter(rawPath, startsFromRoot);

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // Parse all pathElements as directories.
        ShellDirectory dir = startsFromRoot ? root : currentDirectory;
        for (String pathElement : splitPath) {
            if (pathElement.isEmpty()) {
                throw emptyDirectoryNameAlongPath(dir);
            }
            if (ShellConstants.FILE_SYSTEM_THIS.equals(pathElement)) {
                continue;
            }
            if (ShellConstants.FILE_SYSTEM_PARENT.equals(pathElement)) {
                final Optional<ShellDirectory> parent = dir.getParent();
                if (parent.isPresent()) {
                    dir = parent.get();
                } else {
                    throw directoryDoesNotHaveParent(dir.getName());
                }
            }

            dir = dir.parseDirectory(pathElement);
        }

        return dir;
    }

    // TODO: Make sure this doesn't mask '//' or '///' as an error.
    private String removeLeadingAndTrailingDelimiter(String path, boolean leadingDelimiter) {
        final int length = path.length();
        final boolean trailingDelimiter = length > 1 && path.charAt(length - 1) == ShellConstants.FILE_SYSTEM_DELIMITER;
        if (!leadingDelimiter && !trailingDelimiter) {
            return path;
        } else {
            final int startingDelimiterIndex = leadingDelimiter ? 1 : 0;
            final int endingDelimiterIndex = trailingDelimiter ? Math.max(length - 1, startingDelimiterIndex) : length;
            return path.substring(startingDelimiterIndex, endingDelimiterIndex);
        }
    }

    @Override
    public ShellFile parsePathToFile(String rawPath) throws ParseException {
        // If rawPath does not contain a single delimiter, we can try use it as the command name.
        final int delimiterIndex = rawPath.lastIndexOf(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath does not contain a delimiter.
            // It could either be a global file(command), or a file(command) from the currentDirectory.
            final Optional<ShellFile> globalFile = globalFiles.get(rawPath);
            if (globalFile.isPresent()) {
                return globalFile.get();
            } else {
                return currentDirectory.parseFile(rawPath);
            }
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory parse the last entry as a command.
        // So in "path/to/command", parse "path/to" as path to directory "to", and let "to" parse "command".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final ShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        // If rawPath ends with the delimiter, it cannot possibly point to a command.
        final String rawCommand = rawPath.substring(delimiterIndex + 1);
        if (rawCommand.isEmpty()) {
            throw pathDoesntPointToCommand(rawPath);
        }
        return lastDirectory.parseFile(rawCommand);
    }

    @Override
    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter, just autoComplete it from the current directory.
            final Trie<AutoCompleteType> possibilities = currentDirectory.autoCompleteDirectory(rawPath);
            return new AutoCompleteReturnValue(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry as a directory.
        // So in "path/to/directory", parse "path/to" as path to directory "to", and let "to" autoComplete "directory".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final ShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        final String rawEntry = rawPath.substring(delimiterIndex + 1);
        final Trie<AutoCompleteType> possibilities = lastDirectory.autoCompleteDirectory(rawEntry);
        return new AutoCompleteReturnValue(rawEntry, possibilities);
    }

    @Override
    public AutoCompleteReturnValue autoCompletePath(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(ShellConstants.FILE_SYSTEM_DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter.
            // It could be an entry from the current directory or a global command.
            final Trie<AutoCompleteType> entryPossibilities = currentDirectory.autoCompleteEntry(rawPath);
            final Trie<AutoCompleteType> globalFilePossibilities = globalFiles.subTrie(rawPath).map(AUTO_COMPLETE_TYPE_MAPPER);
            final Trie<AutoCompleteType> possibilities = entryPossibilities.union(globalFilePossibilities);
            return new AutoCompleteReturnValue(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry.
        // So in "path/to/entry", parse "path/to" as path to directory "to", and let "to" autoComplete "entry".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final ShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        final String rawEntry = rawPath.substring(delimiterIndex + 1);
        final Trie<AutoCompleteType> possibilities = lastDirectory.autoCompleteEntry(rawEntry);
        return new AutoCompleteReturnValue(rawEntry, possibilities);
    }

    private ParseException directoryDoesNotHaveParent(String directoryName) {
        return new ParseException(
            ParseError.INVALID_ENTRY,
            "Directory '%s' doesn't have a parent.", directoryName
        );
    }

    private ParseException pathDoesntPointToCommand(String path) {
        return new ParseException(
            ParseError.INVALID_ENTRY,
            "Path doesn't point to a command: %s", path
        );
    }

    private ParseException emptyDirectoryNameAlongPath(ShellDirectory parentDirectory) {
        return new ParseException(
            ParseError.INVALID_ENTRY,
            "Empty directory name detected along path! Under: '%s'", parentDirectory.getName()
        );
    }
}
