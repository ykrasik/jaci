package com.rawcod.jerminal.manager;

import com.rawcod.jerminal.collections.trie.ReadOnlyTrie;
import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.filesystem.entry.TrieFilters;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.path.AutoCompletePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValueSuccess;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:37
 */
public class FileSystemManager {
    private static final String DELIMITER = "/";
    private static final String THIS = ".";
    private static final String PARENT = "..";

    private static final Pattern PATH_PATTERN = Pattern.compile(DELIMITER);

    private final ShellFileSystem fileSystem;

    public FileSystemManager(ShellFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public AutoCompleteEntryReturnValue autoCompleteCommand(ShellDirectory dir, String arg) {
        return autoCompleteEntry(dir, arg, TrieFilters.FILE_FILTER);
    }

    public AutoCompleteEntryReturnValue autoCompleteDirectory(ShellDirectory dir, String arg) {
        return autoCompleteEntry(dir, arg, TrieFilters.DIRECTORY_FILTER);
    }

    public AutoCompleteEntryReturnValue autoCompleteEntry(ShellDirectory dir, String arg) {
        return autoCompleteEntry(dir, arg, TrieFilters.NO_FILTER);
    }

    public AutoCompleteEntryReturnValue autoCompleteEntry(ShellDirectory dir, String arg, TrieFilter<ShellEntry> filter) {
        // Special characters are matched, unless they are not accepted by the filter.
        if (isParentAccepted(arg, dir, filter) || isThisAccepted(arg, dir, filter)) {
            final ShellSuggestion suggestion = ShellSuggestion.single(arg);
            return AutoCompleteEntryReturnValue.success(suggestion);
        }

        final ReadOnlyTrie<ShellEntry> children = dir.getChildren();
        if (children.isEmpty()) {
            return AutoCompleteEntryReturnValue.failureBuilder(AutoCompleteError.EMPTY_DIRECTORY)
                .setMessageFormat("Directory '%s' is empty.", dir.getName())
                .build();
        }

        final List<String> possibleValues = children.getWordsByFilter(arg, filter);

        // Couldn't match any child entry.
        if (possibleValues.isEmpty()) {
            return AutoCompleteEntryReturnValue.failureBuilder(AutoCompleteError.NO_POSSIBLE_VALUES)
                .setMessageFormat("AutoCompletion not possible: directory='%s', prefix='%s'", dir.getName(), arg)
                .build();
        }

        final ShellSuggestion suggestion;
        if (possibleValues.size() == 1) {
            // Matched a single child entry.
            final String possibility = possibleValues.get(0);
            suggestion = ShellSuggestion.single(possibility);
        } else {
            // Matched multiple potential child entries.
            final String longestPrefix = children.getLongestPrefix(arg);
            suggestion = ShellSuggestion.multiple(longestPrefix, possibleValues);
        }

        return AutoCompleteEntryReturnValue.success(suggestion);
    }

    public AutoCompletePathReturnValue autoCompleteCommandFromPath(String path, ShellDirectory dir) {
        return autoCompleteEntryFromPath(path, dir, TrieFilters.FILE_FILTER);
    }

    public AutoCompletePathReturnValue autoCompleteDirectoryFromPath(String path, ShellDirectory dir) {
        return autoCompleteEntryFromPath(path, dir, TrieFilters.DIRECTORY_FILTER);
    }

    public AutoCompletePathReturnValue autoCompleteEntryFromPath(String path, ShellDirectory dir) {
        return autoCompleteEntryFromPath(path, dir, TrieFilters.NO_FILTER);
    }

    public AutoCompletePathReturnValue autoCompleteEntryFromPath(String path,
                                                                 ShellDirectory dir,
                                                                 TrieFilter<ShellEntry> filter) {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        int lastIndexOfDelimiter = path.lastIndexOf(DELIMITER);
        if (lastIndexOfDelimiter == -1) {
            // Use the whole string for autoCompletion if no delimiter present.
            lastIndexOfDelimiter = 0;
        }

        final String pathToParse = path.substring(0, lastIndexOfDelimiter);
        final String autoCompleteArg = path.substring(lastIndexOfDelimiter);

        final ParsePathReturnValue parseReturnValue = parseDirectoryFromPath(pathToParse, dir);
        if (parseReturnValue.isFailure()) {
            return AutoCompletePathReturnValue.failureFrom(parseReturnValue.getFailure());
        }

        final ParsePathReturnValueSuccess parseSuccess = parseReturnValue.getSuccess();
        final List<ShellDirectory> parsedPath = parseSuccess.getPath();
        final ShellDirectory currentDir = (ShellDirectory) parseSuccess.getEntry();

        // Let the last directory along the path autoComplete the arg.
        final AutoCompleteEntryReturnValue returnValue = autoCompleteEntry(currentDir, autoCompleteArg, filter);
        if (returnValue.isFailure()) {
            // It was impossible to autoComplete the arg, no suggestions available.
            return AutoCompletePathReturnValue.failureFrom(returnValue.getFailure());
        }

        final AutoCompleteEntryReturnValueSuccess success = returnValue.getSuccess();
        return AutoCompletePathReturnValue.success(parsedPath, success.getSuggestion());
    }

    public ParsePathReturnValue parseCommandFromPath(String path, ShellDirectory dir) {
        return parseEntryFromPath(path, dir, TrieFilters.FILE_FILTER);
    }

    public ParsePathReturnValue parseDirectoryFromPath(String path, ShellDirectory dir) {
        return parseEntryFromPath(path, dir, TrieFilters.DIRECTORY_FILTER);
    }

    public ParsePathReturnValue parseEntryFromPath(String path, ShellDirectory dir) {
        return parseEntryFromPath(path, dir, TrieFilters.NO_FILTER);
    }

    public ParsePathReturnValue parseEntryFromPath(String path, ShellDirectory dir, TrieFilter<ShellEntry> filter) {
        final ShellDirectory startDir;
        final String pathToSplit;
        final boolean startsFromRoot = path.startsWith(DELIMITER);
        if (startsFromRoot) {
            pathToSplit = path.substring(1);
            startDir = fileSystem.getRoot();
        } else {
            pathToSplit = path;
            startDir = dir;
        }

        // Split the given path according to delimiter.
        final String[] entries = PATH_PATTERN.split(pathToSplit);

        // Keep a record of the parsed path.
        final List<ShellDirectory> parsedPath = new ArrayList<>(entries.length);
        if (startsFromRoot) {
            parsedPath.add(fileSystem.getRoot());
        }

        // If the path consists of N entries, parse N-1 entries as directories
        // and parse the last entry according to the filter.
        ShellDirectory currentDir = startDir;
        for (int i = 0; i < entries.length - 1; i++) {
            final String entry = entries[i];
            final ParseEntryReturnValue returnValue = parseDirectory(currentDir, entry);
            if (returnValue.isFailure()) {
                // Invalid directory along the path.
                return ParsePathReturnValue.failureFrom(returnValue.getFailure());
            }

            currentDir = (ShellDirectory) returnValue.getSuccess().getEntry();
            parsedPath.add(currentDir);
        }

        // Parse the last entry in the path according to the filter.
        final ParseEntryReturnValue returnValue = parseEntry(currentDir, entries[entries.length - 1], filter);
        if (returnValue.isFailure()) {
            return ParsePathReturnValue.failureFrom(returnValue.getFailure());
        }

        final ShellEntry entry = returnValue.getSuccess().getEntry();
        return ParsePathReturnValue.success(parsedPath, entry);
    }

    public ParseEntryReturnValue parseCommand(ShellDirectory dir, String arg) {
        return parseEntry(dir, arg, TrieFilters.FILE_FILTER);
    }

    public ParseEntryReturnValue parseDirectory(ShellDirectory dir, String arg) {
        return parseEntry(dir, arg, TrieFilters.DIRECTORY_FILTER);
    }

    public ParseEntryReturnValue parseEntry(ShellDirectory dir, String arg) {
        return parseEntry(dir, arg, TrieFilters.NO_FILTER);
    }

    public ParseEntryReturnValue parseEntry(ShellDirectory dir, String arg, TrieFilter<ShellEntry> filter) {
        if (isParentAccepted(arg, dir, filter)) {
            return ParseEntryReturnValue.success(dir.getParent());
        }
        if (isThisAccepted(arg, dir, filter)) {
            return ParseEntryReturnValue.success(dir);
        }

        final ReadOnlyTrie<ShellEntry> children = dir.getChildren();
        if (children.isEmpty()) {
            return ParseEntryReturnValue.failureBuilder(ParseError.EMPTY_DIRECTORY)
                .setMessageFormat("Directory '%s' is empty.", dir.getName())
                .build();
        }

        final ShellEntry entry = children.get(arg);
        if (entry == null) {
            return ParseEntryReturnValue.failureBuilder(ParseError.INVALID_ENTRY)
                .setMessageFormat("Directory '%s' doesn't contain child '%s'.", dir.getName(), arg)
                .build();
        }

        if (filter.shouldFilter(entry)) {
            return ParseEntryReturnValue.failureBuilder(ParseError.INVALID_ENTRY)
                .setMessageFormat("Invalid access to directory '%s' child '%s'.", dir.getName(), arg)
                .build();
        }

        return ParseEntryReturnValue.success(entry);
    }

    public String serializePath(List<ShellEntry> path, boolean endWithDelimiter) {
        final ShellDirectory root = fileSystem.getRoot();
        if (path.size() == 1 && path.get(0) == root) {
            // If root is the only directory on the path.
            if (endWithDelimiter) {
                return DELIMITER;
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

    private boolean isParentAccepted(String arg, ShellDirectory dir, TrieFilter<ShellEntry> filter) {
        return PARENT.equals(arg) && !filter.shouldFilter(dir.getParent());
    }

    private boolean isThisAccepted(String arg, ShellDirectory dir, TrieFilter<ShellEntry> filter) {
        return THIS.equals(arg) && !filter.shouldFilter(dir);
    }
}
