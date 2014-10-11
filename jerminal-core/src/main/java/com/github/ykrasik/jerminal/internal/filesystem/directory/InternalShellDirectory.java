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

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteType;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An internal representation of a {@link ShellDirectory}.<br>
 * Can parse and auto complete the directory's children.<br>
 * Any changes to the underlying {@link ShellDirectory} will not be reflected in this object.
 * But don't do this. Why would you do this, anyway?
 *
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class InternalShellDirectory extends AbstractDescribable {
    private static final Function<InternalShellDirectory, AutoCompleteType> AUTO_COMPLETE_DIRECTORY_MAPPER = new Function<InternalShellDirectory, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(InternalShellDirectory input) {
            return AutoCompleteType.DIRECTORY;
        }
    };

    private static final Function<InternalCommand, AutoCompleteType> AUTO_COMPLETE_ENTRY_MAPPER = new Function<InternalCommand, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(InternalCommand input) {
            return AutoCompleteType.COMMAND;
        }
    };

    private final Optional<InternalShellDirectory> parent;
    private final Trie<InternalShellDirectory> directoryTrie;
    private final Trie<InternalCommand> commandTrie;

    public InternalShellDirectory(ShellDirectory directory) {
        this(Objects.requireNonNull(directory), Optional.<InternalShellDirectory>absent());
    }

    private InternalShellDirectory(ShellDirectory directory, Optional<InternalShellDirectory> parent) {
        super(directory.getName(), directory.getDescription());
        if (!ShellConstants.isValidName(directory.getName())) {
            throw new ShellException("Invalid name for directory: '%s'", directory.getName());
        }

        this.parent = parent;
        this.directoryTrie = createDirectoryTrie(directory);
        this.commandTrie = createCommandTrie(directory);
    }

    private Trie<InternalShellDirectory> createDirectoryTrie(ShellDirectory directory) {
        Trie<InternalShellDirectory> trie = new TrieImpl<>();
        for (ShellDirectory childDirectory : directory.getDirectories()) {
            final InternalShellDirectory childDirectoryManager = new InternalShellDirectory(childDirectory, Optional.of(this));
            trie = trie.add(childDirectory.getName(), childDirectoryManager);
        }
        return trie;
    }

    private Trie<InternalCommand> createCommandTrie(ShellDirectory directory) {
        Trie<InternalCommand> trie = new TrieImpl<>();
        for (Command command : directory.getCommands()) {
            final InternalCommand internalCommand = new InternalCommand(command);
            trie = trie.add(command.getName(), internalCommand);
        }
        return trie;
    }

    /**
     * @return The parent {@link InternalShellDirectory}.
     */
    public Optional<InternalShellDirectory> getParent() {
        return parent;
    }

    /**
     * @param name The child directory name to look up.
     * @return A child {@link InternalShellDirectory} with the given name, if one exists.
     */
    public Optional<InternalShellDirectory> getDirectory(String name) {
        return directoryTrie.get(name);
    }

    /**
     * @param name The child command name to look up.
     * @return A child {@link InternalCommand} with the given name, if one exists.
     */
    public Optional<InternalCommand> getCommand(String name) {
        return commandTrie.get(name);
    }

    /**
     * @param prefix Prefix to offer auto complete for.
     * @return A {@link Trie} containing auto complete suggestions for the a child {@link InternalShellDirectory}
     *         that starts with the given prefix.
     */
    public Trie<AutoCompleteType> autoCompleteDirectory(String prefix) {
        return directoryTrie.subTrie(prefix).map(AUTO_COMPLETE_DIRECTORY_MAPPER);
    }

    /**
     * @param prefix Prefix to offer auto complete for.
     * @return A {@link Trie} containing auto complete suggestions for any child entry that starts with the given prefix.
     */
    public Trie<AutoCompleteType> autoCompleteEntry(String prefix) {
        final Trie<AutoCompleteType> directoryAutoComplete = autoCompleteDirectory(prefix);
        final Trie<AutoCompleteType> commandAutoComplete = commandTrie.subTrie(prefix).map(AUTO_COMPLETE_ENTRY_MAPPER);
        return directoryAutoComplete.union(commandAutoComplete);
    }

    /**
     * @param recursive Whether to recurse into sub-directories.
     * @return A {@link ShellDirectory} representation of this object. If recursive, will contain the whole
     *         hierarchy, otherwise only the first-level children.
     */
    public ShellDirectory toShellDirectory(boolean recursive) {
        final MutableShellDirectory directory = new ShellDirectoryImpl(getName(), getDescription());
        appendChildren(directory, recursive);
        return directory;
    }

    private void appendChildren(MutableShellDirectory directory, boolean recursive) {
        // Append child commands.
        final Collection<InternalCommand> childCommands = commandTrie.values();
        final List<Command> commands = new ArrayList<>(childCommands.size());
        for (InternalCommand childCommand : childCommands) {
            commands.add(childCommand.getCommand());
        }
        directory.addCommands(commands);

        // Append child directories, recurse if required.
        for (InternalShellDirectory childDirectory : directoryTrie.values()) {
            final MutableShellDirectory childShellDirectory = directory.getOrCreateDirectory(childDirectory.getName(), childDirectory.getDescription());
            if (recursive) {
                childDirectory.appendChildren(childShellDirectory, true);
            }
        }
    }
}
