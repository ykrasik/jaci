package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public class ShellDirectoryImpl extends AbstractShellEntry implements ShellDirectory {
    private static final Predicate<ShellEntry> DIRECTORY_FILTER = new Predicate<ShellEntry>() {
        @Override
        public boolean apply(ShellEntry input) {
            return input.isDirectory();
        }
    };

    private final Trie<ShellEntry> entries;

    private ShellDirectory parent;

    public ShellDirectoryImpl(String name,
                              String description,
                              Map<String, ShellDirectory> directories,
                              Map<String, ShellCommand> commands) {
        super(name, description);

        final TrieBuilder<ShellEntry> trieBuilder = new TrieBuilder<>();
        trieBuilder.addAll(directories);
        trieBuilder.addAll(commands);
        this.entries = trieBuilder.build();
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
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public Collection<ShellEntry> getChildren() {
        return Collections.unmodifiableCollection(entries.getValues());
    }

    @Override
    public ShellDirectory getParent() {
        return parent;
    }

    @Override
    public ShellCommand parseCommand(String rawCommand) throws ParseException {
        return (ShellCommand) doParseEntry(rawCommand, false);
    }

    @Override
    public ShellDirectory parseDirectory(String rawDirectory) throws ParseException {
        return (ShellDirectory) doParseEntry(rawDirectory, true);
    }

    private ShellEntry doParseEntry(String rawEntry, boolean isDirectory) throws ParseException {
        final Optional<ShellEntry> childEntryOptional = entries.get(rawEntry);
        if (!childEntryOptional.isPresent()) {
            // Give a meaningful error message.
            if (isEmpty()) {
                throw ParseErrors.emptyDirectory(getName());
            } else {
                throw ParseErrors.directoryDoesNotContainEntry(getName(), rawEntry, isDirectory);
            }
        }

        // Child entry exists, check that it is what we are looking for.
        final ShellEntry childEntry = childEntryOptional.get();
        if (childEntry.isDirectory() == isDirectory) {
            return childEntry;
        } else {
            throw ParseErrors.invalidAccessToEntry(childEntry.getName(), isDirectory);
        }
    }

    @Override
    public Trie<AutoCompleteType> autoCompleteDirectory(String prefix) throws ParseException {
        return doAutoCompleteEntry(prefix, true);
    }

    @Override
    public Trie<AutoCompleteType> autoCompleteEntry(String prefix) throws ParseException {
        return doAutoCompleteEntry(prefix, false);
    }

    private Trie<AutoCompleteType> doAutoCompleteEntry(String prefix, boolean isDirectory) throws ParseException {
        // There are 2 ways to autoComplete an entry -
        // Either show only directories, or show all entries (directories and commands).
        // Note - special characters are never autoCompleted.
        final Trie<ShellEntry> childrenTrie;
        if (isDirectory) {
            childrenTrie = entries.filter(DIRECTORY_FILTER);
        } else {
            childrenTrie = entries;
        }

        // Get all children possible with this prefix.
        final Trie<AutoCompleteType> possibleChildren = childrenTrie.subTrie(prefix).map(AutoCompleteMappers.entryMapper());
        if (possibleChildren.isEmpty()) {
            // Give a meaningful error message.
            if (isEmpty()) {
                throw ParseErrors.emptyDirectory(getName());
            }
        }

        return possibleChildren;
    }

}
