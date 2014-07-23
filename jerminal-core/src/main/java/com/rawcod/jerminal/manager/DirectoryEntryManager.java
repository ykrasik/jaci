package com.rawcod.jerminal.manager;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.util.AutoCompleteUtils;

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

    public ParseEntryReturnValue parseCommand(String rawCommand) {
        return parseEntry(rawCommand, EntryFilters.FILE_FILTER);
    }

    public ParseEntryReturnValue parseDirectory(String rawDirectory) {
        return parseEntry(rawDirectory, EntryFilters.DIRECTORY_FILTER);
    }

    public ParseEntryReturnValue parseEntry(String rawEntry) {
        return parseEntry(rawEntry, EntryFilters.NO_FILTER);
    }

    public ParseEntryReturnValue parseEntry(String rawEntry, Predicate<ShellEntry> filter) {
        if (isParentAccepted(rawEntry, filter)) {
            return ParseEntryReturnValue.success(directory.getParent());
        }
        if (isThisAccepted(rawEntry, filter)) {
            return ParseEntryReturnValue.success(directory);
        }

        // Check if directory is empty.
        if (children.isEmpty()) {
            return ParseEntryReturnValue.failure(ParseReturnValueFailure.emptyDirectory(directory.getName()));
        }

        // Directory isn't empty, check that the child entry exists.
        final ShellEntry parsedEntry = children.get(rawEntry);
        if (parsedEntry == null) {
            return ParseEntryReturnValue.failure(
                ParseReturnValueFailure.from(
                    ParseError.INVALID_ENTRY,
                    "Directory '%s' doesn't contain entry '%s'.", directory.getName(), rawEntry
                )
            );
        }

        // Child entry exists, check that it is allowed by the filter.
        if (!filter.apply(parsedEntry)) {
            return ParseEntryReturnValue.failure(
                ParseReturnValueFailure.from(
                    ParseError.INVALID_ENTRY,
                    "Invalid access from directory '%s' to entry '%s'.", directory.getName(), parsedEntry
                )
            );
        }

        return ParseEntryReturnValue.success(parsedEntry);
    }

    public AutoCompleteReturnValue autoCompleteCommand(String rawCommand) {
        return autoCompleteEntry(rawCommand, EntryFilters.FILE_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteDirectory(String rawDirectory) {
        return autoCompleteEntry(rawDirectory, EntryFilters.DIRECTORY_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String rawEntry) {
        return autoCompleteEntry(rawEntry, EntryFilters.NO_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String rawEntry, Predicate<ShellEntry> filter) {
        final List<String> possibleValues = children.getWordsFromPrefixWithFilter(rawEntry, filter);

        // Couldn't match any child entry.
        if (possibleValues.isEmpty()) {
            // The special characters should only be shown if the entry doesn't clash with their prefix.
            // The autoCompleteAddition should be nothing, as we don't want to autoComplete the special characters
            // without the user explicitly doing it.
            if (canAddAllSpecialCharacters(rawEntry)) {
                return AutoCompleteReturnValue.successMultiple("", SPECIAL_CHARACTERS);
            }

            // The user entered the parent special character and no other child entries could be matched.
            // The autoCompleteAddition is nothing.
            if (canAddParentSpecialCharacter(rawEntry)) {
                return AutoCompleteReturnValue.successSingle("");
            }

            // The entry clashes with the special characters prefix.
            if (children.isEmpty()) {
                // The directory is empty.
                return AutoCompleteReturnValue.failure(
                    AutoCompleteReturnValueFailure.parseFailure(
                        ParseReturnValueFailure.emptyDirectory(directory.getName())
                    )
                );
            } else {
                // The directory is not empty.
                return AutoCompleteReturnValue.failure(
                    AutoCompleteReturnValueFailure.from(
                        AutoCompleteError.NO_POSSIBLE_VALUES,
                        "AutoComplete error: Can't autoComplete values for directory='%s', prefix='%s'", directory.getName(), rawEntry
                    )
                );
            }
        }

        if (possibleValues.size() == 1) {
            // Matched a single child entry.
            final String possibility = possibleValues.get(0);
            final String autoCompleteAddition = AutoCompleteUtils.getAutoCompleteAddition(rawEntry, possibility);
            return AutoCompleteReturnValue.successSingle(autoCompleteAddition);
        }

        // Matched multiple potential child entries.
        final String longestPrefix = children.getLongestPrefix(rawEntry);

        // The special characters should only be shown if the entry doesn't clash with their prefix.
        List<String> possibilitiesWithSpecialCharacters = new ArrayList<>(possibleValues.size() + 2);
        if (canAddAllSpecialCharacters(longestPrefix)) {
            possibilitiesWithSpecialCharacters.addAll(SPECIAL_CHARACTERS);
        } else if (canAddParentSpecialCharacter(longestPrefix)) {
            possibilitiesWithSpecialCharacters.add(ShellDirectory.PARENT);
        }
        if (!possibilitiesWithSpecialCharacters.isEmpty()) {
            possibilitiesWithSpecialCharacters.addAll(possibleValues);
        } else {
            possibilitiesWithSpecialCharacters = possibleValues;
        }

        final String autoCompleteAddition = AutoCompleteUtils.getAutoCompleteAddition(rawEntry, longestPrefix);
        return AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibilitiesWithSpecialCharacters);
    }

    private boolean isParentAccepted(String entry, Predicate<ShellEntry> filter) {
        return ShellDirectory.PARENT.equals(entry) && directory.getParent() != null && filter.apply(directory.getParent());
    }

    private boolean isThisAccepted(String entry, Predicate<ShellEntry> filter) {
        return ShellDirectory.THIS.equals(entry) && filter.apply(directory);
    }

    private boolean canAddAllSpecialCharacters(String input) {
        return input.isEmpty() || ShellDirectory.THIS.equals(input);
    }

    private boolean canAddParentSpecialCharacter(String input) {
        return ShellDirectory.PARENT.equals(input);
    }
}
