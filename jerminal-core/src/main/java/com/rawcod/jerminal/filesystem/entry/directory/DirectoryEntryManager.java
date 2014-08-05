package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

/**
 * User: ykrasik
 * Date: 20/07/2014
 * Time: 21:05
 */
public class DirectoryEntryManager {
    private static final String THIS = ".";
    private static final String PARENT = "..";

    private final ShellDirectory directory;
    private final Trie<ShellEntry> children;

    public DirectoryEntryManager(ShellDirectory directory) {
        this.directory = directory;
        this.children = new TrieImpl<>();

        children.put(THIS, directory);
        if (directory.getParent() != null) {
            children.put(PARENT, directory.getParent());
        }
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
        // Check children.
        final ShellEntry childEntry = children.get(rawEntry);
        if (childEntry == null) {
            // Give a meaningful error message.
            if (directory.isEmpty()) {
                return ParseErrors.emptyDirectory(directory.getName());
            } else {
                return ParseErrors.directoryDoesNotContainEntry(directory.getName(), rawEntry);
            }
        }

        // Child entry exists, check that it is allowed by the filter.
        if (!filter.apply(childEntry)) {
            return ParseErrors.invalidAccessToEntry(directory.getName(), rawEntry);
        }

        return ParseEntryReturnValue.success(childEntry);
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
        // Get all possible words with this prefix.
        final Optional<TrieView> childrenTrieView = Tries.getTrieViewWithFilter(children, prefix, filter);
        if (!childrenTrieView.isPresent()) {
            // Give a meaningful error message.
            if (directory.isEmpty()) {
                return AutoCompleteErrors.emptyDirectory(directory.getName());
            } else {
                return AutoCompleteErrors.noPossibleValuesForDirectoryWithPrefix(directory.getName(), prefix);
            }
        }
        return AutoCompleteReturnValue.success(prefix, childrenTrieView.get());
    }
}
