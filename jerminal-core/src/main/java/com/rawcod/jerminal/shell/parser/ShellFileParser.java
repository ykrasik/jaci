package com.rawcod.jerminal.shell.parser;

import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.collections.trie.TrieFilter.NoTrieFilter;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.ShellAutoComplete;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

import java.util.List;
import java.util.regex.Pattern;

/**
 * User: ykrasik
 * Date: 17/01/14
 */
public class ShellFileParser {
    private static final String DELIMITER = "/";
    private static final Pattern PATH_PATTERN = Pattern.compile(DELIMITER);

    private static final TrieFilter<ShellEntry> NO_FILTER = new NoTrieFilter<>();
    private static final TrieFilter<ShellEntry> FILE_FILTER = new TrieFilter<ShellEntry>() {
        @Override
        public boolean shouldFilter(ShellEntry value) {
            return value.isDirectory();
        }
    };
    private static final TrieFilter<ShellEntry> DIRECTORY_FILTER = new TrieFilter<ShellEntry>() {
        @Override
        public boolean shouldFilter(ShellEntry value) {
            return !value.isDirectory();
        }
    };

    private final ShellManager manager;

    public ShellFileParser(ShellManager manager) {
        this.manager = manager;
    }

    public AutoCompleteReturnValue autoCompleteCommand(String path) {
        return doAutoComplete(path, FILE_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteDirectory(String path) {
        return doAutoComplete(path, DIRECTORY_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String path) {
        return doAutoComplete(path, NO_FILTER);
    }

    private AutoCompleteReturnValue doAutoComplete(String path, TrieFilter<ShellEntry> filter) {
        final boolean startsFromRoot = path.startsWith("/");
        final String pathToSplit;
        final ShellDirectory startDir;
        if (startsFromRoot) {
            pathToSplit = path.substring(1);
            startDir = manager.getRoot();
        } else {
            pathToSplit = path;
            startDir = manager.getCurrentDirectory();
        }

        ShellDirectory currentDir = startDir;
        final String[] entries = PATH_PATTERN.split(pathToSplit, -1);
        for (int i = 0; i < entries.length - 1; i++) {
            final ParseReturnValue<ShellEntry> returnValue = currentDir.parseEntry(entries[i], DIRECTORY_FILTER);
            if (!returnValue.isSuccess()) {
                return AutoCompleteReturnValue.from(returnValue);
            }

            currentDir = (ShellDirectory) returnValue.getParsedValue();
        }

        // Append the path prefix to the param, except the last path section which is autoCompleted.
        final String lastArg = entries[entries.length - 1];
        final String pathPrefix = calcPathPrefix(entries, startsFromRoot);
        final AutoCompleteReturnValue returnValue = currentDir.autoCompleteEntry(lastArg, filter);
        final ShellAutoComplete autoComplete = returnValue.getAutoComplete();
        final List<String> suggestions = autoComplete.getSuggestions();
        if (suggestions.isEmpty()) {
            return returnValue;
        }
        if (suggestions.size() == 1) {
            final String suggestion = suggestions.get(0);
            final boolean directory = currentDir.parseEntry(suggestion, NO_FILTER).getParsedValue().isDirectory();
            if (directory) {
                return AutoCompleteReturnValue.successNoTrailingSpace(ShellAutoComplete.single(pathPrefix + suggestion + DELIMITER));
            } else {
                return AutoCompleteReturnValue.success(ShellAutoComplete.single(pathPrefix + suggestion));
            }
        }

        final String longestPrefix = autoComplete.getLongestPrefix();
        final String pathLongestPrefix = pathPrefix + longestPrefix;
        return AutoCompleteReturnValue.success(ShellAutoComplete.multiple(pathLongestPrefix, suggestions));
    }

    public ParseReturnValue<ShellCommand> parseCommand(String path) {
        final ParseReturnValue<ShellEntry> returnValue = doParse(path, FILE_FILTER);
        if (returnValue.isSuccess() && !returnValue.getParsedValue().isDirectory()) {
            return ParseReturnValue.success((ShellCommand) returnValue.getParsedValue());
        } else {
            return ParseReturnValue.failureFrom(returnValue);
        }
    }

    public ParseReturnValue<ShellDirectory> parseDirectory(String path) {
        final ParseReturnValue<ShellEntry> returnValue = doParse(path, DIRECTORY_FILTER);
        if (returnValue.isSuccess()) {
            return ParseReturnValue.success((ShellDirectory) returnValue.getParsedValue());
        } else {
            return ParseReturnValue.failureFrom(returnValue);
        }
    }

    public ParseReturnValue<ShellEntry> parseEntry(String path) {
        return doParse(path, NO_FILTER);
    }

    private ParseReturnValue<ShellEntry> doParse(String path, TrieFilter<ShellEntry> filter) {
        final boolean startsFromRoot = path.startsWith("/");
        final String pathToSplit;
        final ShellDirectory startDir;
        if (startsFromRoot) {
            pathToSplit = path.substring(1);
            startDir = manager.getRoot();
        } else {
            pathToSplit = path;
            startDir = manager.getCurrentDirectory();
        }

        ShellDirectory currentDir = startDir;
        final String[] entries = PATH_PATTERN.split(pathToSplit, -1);
        for (int i = 0; i < entries.length - 1; i++) {
            final ParseReturnValue<ShellEntry> returnValue = currentDir.parseEntry(entries[i], DIRECTORY_FILTER);
            if (!returnValue.isSuccess()) {
                return ParseReturnValue.failureFrom(returnValue);
            }

            currentDir = (ShellDirectory) returnValue.getParsedValue();
        }

        final String lastArg = entries[entries.length - 1];
        if (lastArg.isEmpty()) {
            // Extra '/' at the end of the path
            if (!filter.shouldFilter(currentDir)) {
                return ParseReturnValue.<ShellEntry>success(currentDir);
            } else {
                return ParseReturnValue.failureInvalidArgument("Invalid argument: " + lastArg, null);
            }
        } else {
            return currentDir.parseEntry(lastArg, filter);
        }
    }

    private String calcPathPrefix(String[] entries, boolean startsFromRoot) {
        final StringBuilder sb = new StringBuilder();
        if (startsFromRoot) {
            sb.append(DELIMITER);
        }
        for (int i = 0; i < entries.length - 1; i++) {
            sb.append(entries[i]);
            sb.append(DELIMITER);
        }
        return sb.toString();
    }
}
