package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.autocomplete.AutoCompleter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.Arrays;

/**
 * User: ykrasik
 * Date: 20/07/2014
 * Time: 21:05
 */
public class DirectoryEntryManager {
    private static final String THIS = ".";
    private static final String PARENT = "..";
    private static final AutoCompleteReturnValue EMPTY_DIR_AUTO_COMPLETE = AutoCompleteReturnValue.successMultiple("", Arrays.asList(THIS, PARENT));

    private final ShellDirectory directory;
    private final Trie<ShellEntry> children;
    private final AutoCompleter<ShellEntry> autoCompleter;

    public DirectoryEntryManager(ShellDirectory directory) {
        this.directory = directory;
        this.children = new TrieImpl<>();

        children.put(THIS, directory);
        children.put(PARENT, directory.getParent());

        this.autoCompleter = new AutoCompleter<>(children);
    }

    public void addChild(ShellEntry entry) {
        children.put(entry.getName(), entry);
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
        // Check that the child entry exists.
        final ShellEntry parsedEntry = children.get(rawEntry);
        if (parsedEntry == null) {
            // Give a meaningful error message.
            final ParseEntryReturnValue failure;
            if (directory.isEmpty()) {
                failure = ParseErrors.emptyDirectory(directory.getName());
            } else {
                failure = ParseErrors.entryDoesNotExist(directory.getName(), rawEntry);
            }
            return failure;
        }

        // Child entry exists, check that it is allowed by the filter.
        if (!filter.apply(parsedEntry)) {
            return ParseEntryReturnValue.failure(ParseErrors.invalidAccessToEntry(directory.getName(), rawEntry));
        }

        return ParseEntryReturnValue.success(parsedEntry);
    }

    public AutoCompleteReturnValue autoCompleteCommand(String prefix) {
        return autoCompleteEntry(prefix, EntryFilters.FILE_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteDirectory(String prefix) {
        return autoCompleteEntry(prefix, EntryFilters.DIRECTORY_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String prefix) {
        return autoCompleteEntry(prefix, EntryFilters.NO_FILTER);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String prefix, Predicate<ShellEntry> filter) {
        if (prefix.isEmpty() && directory.isEmpty()) {
            // We are being asked to autoComplete an empty directory from an empty prefix.
            // This is a special case in which we only show the special characters,
            // but don't change the commandLine.
            return EMPTY_DIR_AUTO_COMPLETE;
        }

        final AutoCompleteReturnValue returnValue = autoCompleter.autoComplete(prefix, filter);
        if (returnValue.isSuccess() || returnValue.getFailure().getError() != AutoCompleteError.NO_POSSIBLE_VALUES) {
            return returnValue;
        }

        // AutoCompleter couldn't autoComplete.
        // Give a meaningful error message.
        if (directory.isEmpty()) {
            return AutoCompleteErrors.emptyDirectory(directory.getName());
        }

        return AutoCompleteErrors.noPossibleValuesForDirectoryWithPrefix(directory.getName(), prefix);
    }
}
