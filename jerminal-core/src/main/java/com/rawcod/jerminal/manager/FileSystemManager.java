package com.rawcod.jerminal.manager;

import com.google.common.base.Splitter;
import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.TrieFilters;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValue.AutoCompleteEntryReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.path.AutoCompletePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValue.ParsePathReturnValueSuccess;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:37
 */
public class FileSystemManager {
    private static final char DELIMITER = '/';
    private static final Splitter SPLITTER = Splitter.on(DELIMITER);

    private final ShellFileSystem fileSystem;

    public FileSystemManager(ShellFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public ParsePathReturnValue parsePathToCommand(String path, ShellDirectory currentDirectory) {
        return parsePath(path, currentDirectory, TrieFilters.FILE_FILTER);
    }

    public ParsePathReturnValue parsePathToDirectory(String path, ShellDirectory currentDirectory) {
        return parsePath(path, currentDirectory, TrieFilters.DIRECTORY_FILTER);
    }

    public ParsePathReturnValue parsePath(String path, ShellDirectory currentDirectory) {
        return parsePath(path, currentDirectory, TrieFilters.NO_FILTER);
    }

    public ParsePathReturnValue parsePath(String path,
                                          ShellDirectory currentDirectory,
                                          TrieFilter<ShellEntry> filter) {
        final String pathToSplit;
        final ShellDirectory startDir;
        final boolean startsFromRoot = !path.isEmpty() && path.charAt(0) == DELIMITER;
        if (startsFromRoot) {
            pathToSplit = path.substring(1);
            startDir = fileSystem.getRoot();
        } else {
            pathToSplit = path;
            startDir = currentDirectory;
        }

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // Keep a record of the parsed path.
        final List<ShellDirectory> parsedPath = new ArrayList<>(splitPath.size());
        if (startsFromRoot) {
            parsedPath.add(fileSystem.getRoot());
        }

        // If the path consists of N entries, parse N-1 entries as directories
        // and parse the last entry according to the filter.
        ShellDirectory dir = startDir;
        for (int i = 0; i < splitPath.size() - 1; i++) {
            final String entry = splitPath.get(i);
            final ParseEntryReturnValue returnValue = dir.getEntryManager().parseDirectory(entry);
            if (returnValue.isFailure()) {
                // Invalid directory along the path.
                return ParsePathReturnValue.failure(returnValue.getFailure());
            }

            dir = (ShellDirectory) returnValue.getSuccess().getEntry();
            parsedPath.add(dir);
        }

        // Parse the last entry in the path according to the filter.
        final String lastEntry = splitPath.get(splitPath.size() - 1);
        final ParseEntryReturnValue returnValue = dir.getEntryManager().parseEntry(lastEntry, filter);
        if (returnValue.isFailure()) {
            return ParsePathReturnValue.failure(returnValue.getFailure());
        }

        final ShellEntry entry = returnValue.getSuccess().getEntry();
        return ParsePathReturnValue.success(parsedPath, entry);
    }

    public AutoCompletePathReturnValue autoCompletePathToCommand(String path, ShellDirectory currentDirectory) {
        return autoCompletePath(path, currentDirectory, TrieFilters.FILE_FILTER);
    }

    public AutoCompletePathReturnValue autoCompletePathToDirectory(String path, ShellDirectory currentDirectory) {
        return autoCompletePath(path, currentDirectory, TrieFilters.DIRECTORY_FILTER);
    }

    public AutoCompletePathReturnValue autoCompletePath(String path, ShellDirectory currentDirectory) {
        return autoCompletePath(path, currentDirectory, TrieFilters.NO_FILTER);
    }

    public AutoCompletePathReturnValue autoCompletePath(String path,
                                                        ShellDirectory currentDirectory,
                                                        TrieFilter<ShellEntry> filter) {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        int lastIndexOfDelimiter = path.lastIndexOf(DELIMITER);
        if (lastIndexOfDelimiter == -1) {
            // Use the whole string for autoCompletion if no delimiter present.
            lastIndexOfDelimiter = 0;
        }

        final String pathToParse = path.substring(0, lastIndexOfDelimiter);
        final String autoCompleteArg = path.substring(lastIndexOfDelimiter);

        final ParsePathReturnValue parseReturnValue = parsePathToDirectory(pathToParse, currentDirectory);
        if (parseReturnValue.isFailure()) {
            return AutoCompletePathReturnValue.parseFailure(parseReturnValue.getFailure());
        }

        final ParsePathReturnValueSuccess parseSuccess = parseReturnValue.getSuccess();
        final List<ShellDirectory> parsedPath = parseSuccess.getPath();
        final ShellDirectory currentDir = (ShellDirectory) parseSuccess.getEntry();

        // Let the last directory along the path autoComplete the arg.
        final AutoCompleteEntryReturnValue returnValue = currentDir.getEntryManager().autoCompleteEntry(autoCompleteArg, filter);
        if (returnValue.isFailure()) {
            // It was impossible to autoComplete the entry, no suggestions available.
            return AutoCompletePathReturnValue.failure(returnValue.getFailure());
        }

        final AutoCompleteEntryReturnValueSuccess success = returnValue.getSuccess();
        return AutoCompletePathReturnValue.success(parsedPath, success.getSuggestion());
    }

    public String serializePath(List<ShellEntry> path, boolean endWithDelimiter) {
        final ShellDirectory root = fileSystem.getRoot();
        if (path.size() == 1 && path.get(0) == root) {
            // If root is the only directory on the path.
            if (endWithDelimiter) {
                return String.valueOf(DELIMITER);
            } else {
                return "";
            }
        }

        final StringBuilder sb = new StringBuilder();
        for (ShellEntry entry : path) {
            if (entry != root) {
                sb.append(entry.getName());
                sb.append(DELIMITER);
            }
        }
        if (!endWithDelimiter && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
