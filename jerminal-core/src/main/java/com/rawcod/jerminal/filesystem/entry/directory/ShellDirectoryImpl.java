package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Optional;
import com.rawcod.jerminal.collections.trie.Trie2;
import com.rawcod.jerminal.collections.trie.TrieBuilder;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public class ShellDirectoryImpl extends AbstractShellEntry implements ShellDirectory {
    private final Trie2<ShellEntry> allEntries;
    private final Trie2<ShellEntry> directories;

    private ShellDirectory parent;

    ShellDirectoryImpl(String name,
                       String description,
                       Map<String, ShellDirectory> directories,
                       Map<String, ShellCommand> commands) {
        super(name, description);

        final TrieBuilder<ShellEntry> trieBuilder = new TrieBuilder<>();
        trieBuilder.addAll(directories);
        this.directories = trieBuilder.build();

        trieBuilder.addAll(commands);
        this.allEntries = trieBuilder.build();
    }

    void setParent(ShellDirectory parent) {
        if (this.parent != null) {
            throw new ShellException("Parent was already set for directory: '%s'", getName());
        }
        this.parent = parent;
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
        return allEntries.isEmpty();
    }

    @Override
    public Collection<ShellEntry> getChildren() {
        return Collections.unmodifiableCollection(allEntries.getAllValues());
    }

    @Override
    public ShellDirectory getParent() {
        return parent;
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
            childEntry = allEntries.get(rawEntry);
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
        final Trie2<ShellEntry> childrenTrie;
        if (isDirectory) {
            childrenTrie = directories;
        } else {
            childrenTrie = allEntries;
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
