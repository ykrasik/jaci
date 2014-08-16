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
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.google.common.base.Optional;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;

import java.util.Collection;

/**
 * A <b>immutable</b> container for {@link ShellDirectory directories} and {@link ShellCommand commands}.
 *
 * @author Yevgeny Krasik
 */
public interface ShellDirectory extends ShellEntry {
    /**
     * Returns 'true' if this {@link ShellDirectory} has no children.
     */
    boolean isEmpty();

    /**
     * Returns this {@link ShellDirectory}'s children.
     */
    Collection<ShellEntry> getChildren();

    /**
     * Returns this {@link ShellDirectory}'s parent {@link ShellDirectory}.
     */
    Optional<ShellDirectory> getParent();

    /**
     * Parse the input as a child {@link ShellCommand}.
     * @throws ParseException If no such child {@link ShellCommand} exists.
     */
    ShellCommand parseCommand(String rawCommand) throws ParseException;

    /**
     * Parse the input as a child {@link ShellDirectory}.
     * @throws ParseException If no such child {@link ShellDirectory} exists.
     */
    ShellDirectory parseDirectory(String rawDirectory) throws ParseException;

    /**
     * Offer auto complete suggestions for the a child {@link ShellDirectory} that starts with the given prefix.
     * @throws ParseException If this {@link ShellDirectory} is empty.
     */
    // TODO: Do I really want to throw this exception? Directories aren't supposed to be empty anyway.
    Trie<AutoCompleteType> autoCompleteDirectory(String prefix) throws ParseException;

    /**
     * Offer auto complete suggestions for the a child {@link ShellDirectory} or {@link ShellCommand}
     * that starts with the given prefix.
     * @throws ParseException If this {@link ShellDirectory} is empty.
     */
    // TODO: Do I really want to throw this exception? Directories aren't supposed to be empty anyway.
    Trie<AutoCompleteType> autoCompleteEntry(String prefix) throws ParseException;
}
