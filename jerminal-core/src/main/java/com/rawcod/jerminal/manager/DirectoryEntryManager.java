package com.rawcod.jerminal.manager;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.filesystem.entry.TrieFilters;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: ykrasik
 * Date: 20/07/2014
 * Time: 21:05
 */
public class DirectoryEntryManager {
    private static final List<String> SPECIAL_CHARACTERS = Arrays.asList(ShellDirectory.THIS, ShellDirectory.PARENT);

    private final ShellDirectory directory;
    private final Trie<ShellEntry> children;

    public DirectoryEntryManager(ShellDirectory directory, Trie<ShellEntry> children) {
        this.directory = directory;
        this.children = children;
    }

    public ParseEntryReturnValue parseCommand(String command) {
        return parseEntry(command, TrieFilters.FILE_FILTER);
    }

    public ParseEntryReturnValue parseDirectory(String directory) {
        return parseEntry(directory, TrieFilters.DIRECTORY_FILTER);
    }

    public ParseEntryReturnValue parseEntry(String entry) {
        return parseEntry(entry, TrieFilters.NO_FILTER);
    }

    public ParseEntryReturnValue parseEntry(String entry, TrieFilter<ShellEntry> filter) {
        if (isParentAccepted(entry, filter)) {
            return ParseEntryReturnValue.success(directory.getParent());
        }
        if (isThisAccepted(entry, filter)) {
            return ParseEntryReturnValue.success(directory);
        }

        // Check if directory is empty.
        if (children.isEmpty()) {
            return ParseEntryReturnValue.failure(ParseReturnValueFailure.emptyDirectory(directory.getName()));
        }

        // Directory isn't empty, check that the child entry exists.
        final ShellEntry parsedEntry = children.get(entry);
        if (parsedEntry == null) {
            return ParseEntryReturnValue.failure(
                ParseReturnValueFailure.from(
                    ParseError.INVALID_ENTRY,
                    "Directory '%s' doesn't contain entry '%s'.", directory.getName(), entry
                )
            );
        }

        // Child entry exists, check that it is allowed by the filter.
        if (!filter.shouldKeep(parsedEntry)) {
            return ParseEntryReturnValue.failure(
                ParseReturnValueFailure.from(
                    ParseError.INVALID_ENTRY,
                    "Invalid access from directory '%s' to entry '%s'.", directory.getName(), parsedEntry
                )
            );
        }

        return ParseEntryReturnValue.success(parsedEntry);
    }

    public AutoCompleteEntryReturnValue autoCompleteCommand(String command) {
        return autoCompleteEntry(command, TrieFilters.FILE_FILTER);
    }

    public AutoCompleteEntryReturnValue autoCompleteDirectory(String directory) {
        return autoCompleteEntry(directory, TrieFilters.DIRECTORY_FILTER);
    }

    public AutoCompleteEntryReturnValue autoCompleteEntry(String entry) {
        return autoCompleteEntry(entry, TrieFilters.NO_FILTER);
    }

    public AutoCompleteEntryReturnValue autoCompleteEntry(String entry, TrieFilter<ShellEntry> filter) {
        final List<String> possibleValues = children.getWordsByFilter(entry, filter);

        // Couldn't match any child entry.
        if (possibleValues.isEmpty()) {
            // The special characters should only be shown if the entry doesn't clash with their prefix.
            if (canAddAllSpecialCharacters(entry)) {
                return AutoCompleteEntryReturnValue.success(ShellSuggestion.multiple(entry, SPECIAL_CHARACTERS));
            }
            if (canAddParentSpecialCharacter(entry)) {
                return AutoCompleteEntryReturnValue.success(ShellSuggestion.single(ShellDirectory.PARENT));
            }

            // The entry clashes with the special characters prefix.
            if (children.isEmpty()) {
                // The directory is empty.
                return AutoCompleteEntryReturnValue.failure(
                    AutoCompleteReturnValueFailure.parseFailure(
                        ParseReturnValueFailure.emptyDirectory(directory.getName())
                    )
                );
            } else {
                // The directory is not empty.
                return AutoCompleteEntryReturnValue.failure(
                    AutoCompleteReturnValueFailure.from(
                        AutoCompleteError.NO_POSSIBLE_VALUES,
                        "AutoComplete error: Not possible for directory='%s', prefix='%s'", directory.getName(), entry
                    )
                );
            }
        }

        final ShellSuggestion suggestion;
        if (possibleValues.size() == 1) {
            // Matched a single child entry.
            final String possibility = possibleValues.get(0);
            suggestion = ShellSuggestion.single(possibility);
        } else {
            // Matched multiple potential child entries.
            final String longestPrefix = children.getLongestPrefix(entry);

            // The special characters should only be shown if the entry doesn't clash with their prefix.
            final List<String> possibilitiesWithSpecialCharacters = new ArrayList<>(possibleValues.size() + 2);
            if (canAddAllSpecialCharacters(longestPrefix)) {
                possibilitiesWithSpecialCharacters.addAll(SPECIAL_CHARACTERS);
            } else if (canAddParentSpecialCharacter(entry)) {
                possibilitiesWithSpecialCharacters.add(ShellDirectory.PARENT);
            }
            possibilitiesWithSpecialCharacters.addAll(possibleValues);

            suggestion = ShellSuggestion.multiple(longestPrefix, possibilitiesWithSpecialCharacters);
        }

        return AutoCompleteEntryReturnValue.success(suggestion);
    }

    private boolean isParentAccepted(String entry, TrieFilter<ShellEntry> filter) {
        return ShellDirectory.PARENT.equals(entry) && directory.getParent() != null && filter.shouldKeep(directory.getParent());
    }

    private boolean isThisAccepted(String entry, TrieFilter<ShellEntry> filter) {
        return ShellDirectory.THIS.equals(entry) && filter.shouldKeep(directory);
    }

    private boolean canAddAllSpecialCharacters(String input) {
        return input.isEmpty() || ShellDirectory.THIS.equals(input);
    }

    private boolean canAddParentSpecialCharacter(String input) {
        return ShellDirectory.PARENT.equals(input);
    }
}
