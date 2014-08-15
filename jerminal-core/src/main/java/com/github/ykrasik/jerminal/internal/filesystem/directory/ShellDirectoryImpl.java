/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.filesystem.directory;

import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * An implementation for a {@link ShellDirectory}.
 *
 * @author Yevgeny Krasik
 */
public class ShellDirectoryImpl extends AbstractDescribable implements ShellDirectory {
    private static final Predicate<ShellEntry> DIRECTORY_FILTER = new Predicate<ShellEntry>() {
        @Override
        public boolean apply(ShellEntry input) {
            return input.isDirectory();
        }
    };

    private final Trie<ShellEntry> entries;

    private Optional<ShellDirectory> parent;

    public ShellDirectoryImpl(String name,
                              String description,
                              Map<String, ShellDirectory> directories,
                              Map<String, ShellCommand> commands) {
        super(name, description);

        final TrieBuilder<ShellEntry> trieBuilder = new TrieBuilder<>();
        trieBuilder.addAll(directories);
        trieBuilder.addAll(commands);
        this.entries = trieBuilder.build();

        this.parent = Optional.absent();
    }

    void setParent(ShellDirectory parent) {
        if (this.parent.isPresent()) {
            throw new ShellException("Parent was already set for directory: '%s'", getName());
        }
        this.parent = Optional.of(parent);
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
    public Optional<ShellDirectory> getParent() {
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
