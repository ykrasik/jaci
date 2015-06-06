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
import java.util.Collections;

/**
 * The CLI implementation of a directory.
 * Contains child {@link CliDirectory directories} and {@link CliCommand commands} and can retrieve them by name or offer
 * auto complete suggestions.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CliDirectory implements Identifiable {
    private final Identifier identifier;
    private final Trie<CliDirectory> childDirectories;
    private final Trie<CliCommand> childCommands;

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

    /**
     * @return The parent {@link CliDirectory}.
     */
    public Opt<CliDirectory> getParent() {
        return parent;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * @return Directory name.
     */
    public String getName() {
        return identifier.getName();
    }

    /**
     * @param name Child directory name to look up.
     * @return A child {@link CliDirectory} with the given name, if one exists.
     */
    // TODO: should this be case insensitive?
    public Opt<CliDirectory> getDirectory(String name) {
        return childDirectories.get(name);
    }

    /**
     * @return All child directories of this directory.
     */
    public Collection<CliDirectory> getChildDirectories() {
        return Collections.unmodifiableCollection(childDirectories.values());
    }

    /**
     * @param name Child command name to look up.
     * @return A child {@link CliCommand} with the given name, if one exists.
     */
    // TODO: should this be case insensitive?
    public Opt<CliCommand> getCommand(String name) {
        return childCommands.get(name);
    }

    /**
     * @return All child commands of this directory.
     */
    public Collection<CliCommand> getChildCommands() {
        return childCommands.values();
    }

    /**
     * Auto complete the given prefix with child directory possibilities.
     *
     * @param prefix Prefix to offer auto complete for.
     * @return Auto complete for child {@link CliDirectory}s that starts with the given prefix. Case insensitive.
     */
    public AutoComplete autoCompleteDirectory(String prefix) {
        final Trie<CliValueType> possibilities = childDirectories.subTrie(prefix).mapValues(DIRECTORY_VALUE_MAPPER);
        return new AutoComplete(prefix, possibilities);
    }

    /**
     * Auto complete the given prefix with child command possibilities.
     *
     * @param prefix Prefix to offer auto complete for.
     * @return Auto complete for the child {@link CliCommand}s that starts with the given prefix. Case insensitive.
     */
    public AutoComplete autoCompleteCommand(String prefix) {
        final Trie<CliValueType> possibilities = childCommands.subTrie(prefix).mapValues(COMMAND_VALUE_MAPPER);
        return new AutoComplete(prefix, possibilities);
    }

    /**
     * Auto complete the given prefix with child directory or command possibilities.
     *
     * @param prefix Prefix to offer auto complete for.
     * @return Auto complete for child entries (either {@link CliDirectory} or {@link CliCommand})
     *         that start with the given prefix. Case insensitive.
     */
    public AutoComplete autoCompleteEntry(String prefix) {
        final AutoComplete directoryAutoComplete = autoCompleteDirectory(prefix);
        final AutoComplete commandAutoComplete = autoCompleteCommand(prefix);
        return directoryAutoComplete.union(commandAutoComplete);
    }

    /**
     * Get the path from root as a string.
     *
     * @return A string representation of this directory.
     */
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

    /**
     * Construct a CLI directory from a {@link CommandDirectoryDef}.
     *
     * @param def CommandDirectoryDef to construct a CLI directory from.
     * @return A CLI directory constructed from the CommandDirectoryDef.
     */
    public static CliDirectory fromDef(@NonNull CommandDirectoryDef def) {
        final Trie<CliDirectory> childDirectories = createChildDirectories(def);
        final Trie<CliCommand> childCommands = createChildCommands(def);
        final CliDirectory directory = new CliDirectory(def.getIdentifier(), childDirectories, childCommands);

        // Link child directories to parent.
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

    /**
     * Construct a CLI directory from the given parameters.
     *
     * @param identifier Directory identifier.
     * @param commands Child CLI commands.
     * @return A CLI directory constructed from the given parameters.
     */
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
