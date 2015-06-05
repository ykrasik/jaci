/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.directory;

import com.github.ykrasik.jemi.Identifiable;
import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.cli.assist.CliValueType;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.command.CommandDef;
import com.github.ykrasik.jemi.directory.CommandDirectoryDef;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.trie.Trie;
import com.github.ykrasik.jemi.util.trie.TrieBuilder;
import com.github.ykrasik.jemi.util.trie.Tries;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

/**
 * The CLI representation of a {@link CommandDirectoryDef}.<br>
 * Contains child {@link CliDirectory directories} and {@link CliCommand commands} and can retrieve them by name or offer
 * auto complete suggestions.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)  // Package-visible for testing
public class CliDirectory implements Identifiable {
    @NonNull private final Identifier identifier;

    @NonNull private final Trie<CliDirectory> childDirectories;
    @NonNull private final Trie<CliCommand> childCommands;

    /**
     * The parent {@link CliDirectory}.
     */
    private Opt<CliDirectory> parent = Opt.absent();

    private void setParent(CliDirectory parent) {
        if (this.parent.isPresent()) {
            throw new IllegalStateException("Trying to set the parent for a CliDirectory that already has one: " + identifier);
        }
        this.parent = Opt.of(parent);
    }

    // TODO: JavaDoc
    public Opt<CliDirectory> getParent() {
        return parent;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    // TODO: JavaDoc
    public String getName() {
        return identifier.getName();
    }

    /**
     * @param name The child directory name to look up.
     * @return A child {@link CliDirectory} with the given name, if one exists.
     */
    // TODO: should this be case insensitive?
    public Opt<CliDirectory> getDirectory(String name) {
        return childDirectories.get(name);
    }

    // TODO: JavaDoc
    public Collection<CliDirectory> getChildDirectories() {
        return childDirectories.values();
    }

    /**
     * @param name The child command name to look up.
     * @return A child {@link CliCommand} with the given name, if one exists.
     */
    // TODO: should this be case insensitive?
    public Opt<CliCommand> getCommand(String name) {
        return childCommands.get(name);
    }

    // TODO: JavaDoc
    public Collection<CliCommand> getChildCommands() {
        return childCommands.values();
    }

    /**
     * @param prefix Prefix to offer auto complete for.
     * @return A {@link Trie} containing auto complete suggestions for the a child {@link CliDirectory}
     *         that starts with the given prefix. Case insensitive.
     */
    // TODO: Wrong JavaDoc
    public AutoComplete autoCompleteDirectory(String prefix) {
        final Trie<CliValueType> possibilities = childDirectories.subTrie(prefix).mapValues(DIRECTORY_VALUE_MAPPER);
        return new AutoComplete(prefix, possibilities);
    }

    /**
     * @param prefix Prefix to offer auto complete for.
     * @return A {@link Trie} containing auto complete suggestions for the a child {@link CliCommand}
     *         that starts with the given prefix. Case insensitive.
     */
    // TODO: Wrong JavaDoc
    public AutoComplete autoCompleteCommand(String prefix) {
        final Trie<CliValueType> possibilities = childCommands.subTrie(prefix).mapValues(COMMAND_VALUE_MAPPER);
        return new AutoComplete(prefix, possibilities);
    }

    /**
     * @param prefix Prefix to offer auto complete for.
     * @return A {@link Trie} containing auto complete suggestions for any child entry (either {@link CliDirectory} or
     *         {@link CliCommand}) that starts with the given prefix. Case insensitive.
     */
    // TODO: Wrong JavaDoc
    public AutoComplete autoCompleteEntry(String prefix) {
        final AutoComplete directoryAutoComplete = autoCompleteDirectory(prefix);
        final AutoComplete commandAutoComplete = autoCompleteCommand(prefix);
        return directoryAutoComplete.union(commandAutoComplete);
    }

    // TODO: JavaDoc
    public String toPath() {
        if (!parent.isPresent()) {
            return "/";
        }
        return parent.get().toPath() + identifier.getName() + '/';
    }

    @Override
    public String toString() {
        return toPath();
    }

    // TODO: JavaDoc
    public static CliDirectory fromDef(@NonNull CommandDirectoryDef def) {
        final Trie<CliDirectory> childDirectories = createChildDirectories(def);
        final Trie<CliCommand> childCommands = createChildCommands(def);
        final CliDirectory directory = new CliDirectory(def.getIdentifier(), childDirectories, childCommands);

        // Link directories child directories to parent.
        for (CliDirectory childDirectory : childDirectories.values()) {
            childDirectory.setParent(directory);
        }
        return directory;
    }

    private static Trie<CliDirectory> createChildDirectories(CommandDirectoryDef def) {
        final TrieBuilder<CliDirectory> builder = new TrieBuilder<>();
        for (CommandDirectoryDef childDirectoryDef : def.getDirectoryDefs()) {
            final CliDirectory childDirectory = fromDef(childDirectoryDef);
            builder.add(childDirectoryDef.getIdentifier().getName(), childDirectory);
        }
        return builder.build();
    }

    private static Trie<CliCommand> createChildCommands(CommandDirectoryDef directoryDef) {
        final TrieBuilder<CliCommand> builder = new TrieBuilder<>();
        for (CommandDef def : directoryDef.getCommandDefs()) {
            final CliCommand command = CliCommand.fromDef(def);
            builder.add(command.getName(), command);
        }
        return builder.build();
    }

    // TODO: JavaDoc
    public static CliDirectory from(Identifier identifier, CliCommand... commands) {
        final Trie<CliCommand> childCommands = createChildCommands(commands);
        return new CliDirectory(identifier, Tries.<CliDirectory>emptyTrie(), childCommands);
    }

    private static Trie<CliCommand> createChildCommands(CliCommand[] commands) {
        final TrieBuilder<CliCommand> builder = new TrieBuilder<>();
        for (CliCommand command : commands) {
            builder.add(command.getName(), command);
        }
        return builder.build();
    }

    private static final CliValueType.Mapper<CliDirectory> DIRECTORY_VALUE_MAPPER = new CliValueType.Mapper<>(CliValueType.DIRECTORY);
    private static final CliValueType.Mapper<CliCommand> COMMAND_VALUE_MAPPER = new CliValueType.Mapper<>(CliValueType.COMMAND);
}
