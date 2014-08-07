package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.collections.trie.Tries;
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

    private static final Predicate<ShellEntry> DIRECTORY_FILTER = new Predicate<ShellEntry>() {
        @Override
        public boolean apply(ShellEntry value) {
            return value.isDirectory();
        }
    };

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
        return doParseEntry(rawCommand, false);
    }

    public ParseEntryReturnValue parseDirectory(String rawDirectory) {
        return doParseEntry(rawDirectory, true);
    }

    private ParseEntryReturnValue doParseEntry(String rawEntry, boolean isDirectory) {
        // Check children.
        final ShellEntry childEntry = children.get(rawEntry);
        if (childEntry == null) {
            // Give a meaningful error message.
            if (directory.isEmpty()) {
                return ParseErrors.emptyDirectory(directory.getName());
            } else {
                return ParseErrors.directoryDoesNotContainEntry(directory.getName(), rawEntry, isDirectory);
            }
        }

        // Child entry exists, check that it is what we are looking for..
        if (childEntry.isDirectory() != isDirectory) {
            return ParseErrors.invalidAccessToEntry(rawEntry, isDirectory);
        }

        return ParseEntryReturnValue.success(childEntry);
    }

    public AutoCompleteReturnValue autoCompleteDirectory(String prefix) {
        return doAutoCompleteEntry(prefix, true);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String prefix) {
        return doAutoCompleteEntry(prefix, false);
    }

    private AutoCompleteReturnValue doAutoCompleteEntry(String prefix, boolean isDirectory) {
        // Get all possible words with this prefix.
        final Optional<TrieView> childrenTrieView;
        if (isDirectory) {
            childrenTrieView = Tries.getTrieViewWithFilter(children, prefix, DIRECTORY_FILTER);
        } else {
            childrenTrieView = Tries.getTrieView(children, prefix);
        }

        if (!childrenTrieView.isPresent()) {
            // Give a meaningful error message.
            if (directory.isEmpty()) {
                return AutoCompleteErrors.emptyDirectory(directory.getName());
            } else {
                return AutoCompleteErrors.noPossibleValuesForDirectoryWithPrefix(directory.getName(), prefix, isDirectory);
            }
        }
        return AutoCompleteReturnValue.success(prefix, childrenTrieView.get());
    }
}
