package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Optional;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.*;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public class ShellDirectoryImpl extends AbstractShellEntry implements ShellDirectory {
    private final Map<String, ShellEntry> childrenMap;
    private final Trie<ShellEntry> allChildrenTrie;
    private final Trie<ShellEntry> childDirectoriesTrie;

    private ShellDirectory parent;

    public ShellDirectoryImpl(String name) {
        this(name, "Directory");
    }

    public ShellDirectoryImpl(String name, String description) {
        super(name, description);

        this.childrenMap = new HashMap<>();
        this.allChildrenTrie = new TrieImpl<>();
        this.childDirectoriesTrie = new TrieImpl<>();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public ShellDirectory getAsDirectory() {
        return this;
    }

    @Override
    public ShellCommand getAsCommand() {
        final String message = String.format("'%s' is a directory, not a command!", getName());
        throw new IllegalStateException(message);
    }

    @Override
    public boolean isEmpty() {
        return childrenMap.isEmpty();
    }

    @Override
    public Collection<ShellEntry> getChildren() {
        return Collections.unmodifiableCollection(childrenMap.values());
    }

    @Override
    public ShellDirectory getParent() {
        return parent;
    }

    @Override
    public ShellDirectory addEntries(ShellEntry... entries) {
        for (ShellEntry entry : entries) {
            addEntry(entry);
        }
        return this;
    }

    @Override
    public ShellDirectory addEntry(ShellEntry entry) {
        if (entry == this) {
            throw new ShellException("Trying to add a directory to itself!");
        }
        if (entry.isDirectory()) {
            ((ShellDirectoryImpl) entry).setParent(this);
        }

        final String name = entry.getName();
        childrenMap.put(name, entry);

        // Automatically add a suffix to autoComplete values.
        // This will automatically add a helpful suffix to the word, when it's the only possible word.
        // For directories, add a '/'. For commands, add a space.
        final char suffix = entry.isDirectory() ? DELIMITER : ' ';
        final String nameWithSuffix = name + suffix;
        allChildrenTrie.put(nameWithSuffix, entry);
        if (entry.isDirectory()) {
            childDirectoriesTrie.put(nameWithSuffix, entry);
        }

        return this;
    }

    private void setParent(ShellDirectory parent) {
        if (this.parent != null) {
            final String message = String.format("Directory '%s' already has a parent: '%s'", getName(), this.parent.getName());
            throw new ShellException(message);
        }
        this.parent = parent;
    }

    @Override
    public ParseEntryReturnValue parseCommand(String rawCommand) {
        return doParseEntry(rawCommand, false);
    }

    @Override
    public ParseEntryReturnValue parseDirectory(String rawDirectory) {
        return doParseEntry(rawDirectory, true);
    }

    private ParseEntryReturnValue doParseEntry(String rawEntry, boolean isDirectory) {
        // Check special characters and children.
        final ShellEntry childEntry;
        if (THIS.equals(rawEntry)) {
            childEntry = this;
        } else if (PARENT.equals(rawEntry)) {
            childEntry = parent;
        } else {
            childEntry = childrenMap.get(rawEntry);
        }

        if (childEntry == null) {
            // Give a meaningful error message.
            if (isEmpty()) {
                return ParseErrors.emptyDirectory(getName());
            } else {
                return ParseErrors.directoryDoesNotContainEntry(getName(), rawEntry, isDirectory);
            }
        }

        // Child entry exists, check that it is what we are looking for..
        if (childEntry.isDirectory() != isDirectory) {
            return ParseErrors.invalidAccessToEntry(childEntry.getName(), isDirectory);
        }

        return ParseEntryReturnValue.success(childEntry);
    }

    @Override
    public AutoCompleteReturnValue autoCompleteDirectory(String prefix) {
        return doAutoCompleteEntry(prefix, true);
    }

    @Override
    public AutoCompleteReturnValue autoCompleteEntry(String prefix) {
        return doAutoCompleteEntry(prefix, false);
    }

    private AutoCompleteReturnValue doAutoCompleteEntry(String prefix, boolean isDirectory) {
        // There are 2 ways to autoComplete an entry -
        // Either only show only directories, or show all entries (directories and commands).
        // Note - special characters are never autoCompleted.
        final Trie<ShellEntry> childrenTrie;
        if (isDirectory) {
            childrenTrie = childDirectoriesTrie;
        } else {
            childrenTrie = allChildrenTrie;
        }

        // Get all children possible with this prefix.
        final Optional<TrieView> childrenTrieView = Tries.getTrieView(childrenTrie, prefix);
        if (!childrenTrieView.isPresent()) {
            // Give a meaningful error message.
            if (isEmpty()) {
                return AutoCompleteErrors.emptyDirectory(getName());
            } else {
                return AutoCompleteErrors.noPossibleValuesForDirectoryWithPrefix(getName(), prefix, isDirectory);
            }
        }
        return AutoCompleteReturnValue.success(prefix, childrenTrieView.get());
    }
}